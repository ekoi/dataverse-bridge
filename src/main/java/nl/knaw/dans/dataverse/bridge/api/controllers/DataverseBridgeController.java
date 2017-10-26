package nl.knaw.dans.dataverse.bridge.api.controllers;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.PreBag;
import gov.loc.repository.bagit.transformer.impl.ChainingCompleter;
import gov.loc.repository.bagit.transformer.impl.DefaultCompleter;
import gov.loc.repository.bagit.transformer.impl.TagManifestCompleter;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import nl.knaw.dans.dataverse.bridge.bagit.BagInfoCompleter;
import nl.knaw.dans.dataverse.bridge.converter.*;
import nl.knaw.dans.dataverse.bridge.db.dao.ArchivingReportDao;
import nl.knaw.dans.dataverse.bridge.db.dao.DvnTdrUserDao;
import nl.knaw.dans.dataverse.bridge.db.dao.TdrDao;
import nl.knaw.dans.dataverse.bridge.db.domain.ArchivingReport;
import nl.knaw.dans.dataverse.bridge.db.domain.DvnTdrUser;
import nl.knaw.dans.dataverse.bridge.db.domain.Tdr;
import nl.knaw.dans.dataverse.bridge.tdrplugins.IDataverseIngest;
import nl.knaw.dans.dataverse.bridge.tdrplugins.danseasy.IngestToEasy;
import nl.knaw.dans.dataverse.bridge.util.DvnBridgeHelper;
import nl.knaw.dans.dataverse.bridge.util.Status;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * Class DataverseBridgeController
 * Created by Eko Indarto
 */
@RequestMapping("/handler")
@Controller
public class DataverseBridgeController {

    private static final Logger LOG = LoggerFactory.getLogger(DataverseBridgeController.class);

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ArchivingReportDao archivingReportDao;

    @Autowired
    private DvnTdrUserDao dvnTdrUserDao;

    @Autowired
    private TdrDao tdrDao;

    @RequestMapping(
            value = "/ingest/{hdlPrefix}/{hdl}/target/{tdrName}",
            method = RequestMethod.POST,
            params = {"dvnUser"})
    public ResponseEntity ingestToTdr(@PathVariable String hdlPrefix, @PathVariable String hdl, @PathVariable String tdrName,
                                      String dvnUser) {

        LOG.debug("Request URL: " +  DvnBridgeHelper.getRequestUrl());
        Tdr tdr = tdrDao.getByName(tdrName);
        if (tdr == null) {
            LOG.error("ERROR no Trust Digital Repository with the name '" + tdrName + "'");
            return new ResponseEntity(DvnBridgeHelper.emptyJsonResponse(), HttpStatus.BAD_REQUEST);//Just temporary. Not the right way.

        }

        DvnTdrUser dvnTdrUser = dvnTdrUserDao.getByDvnUserAndTdrName(dvnUser, tdr.getId());
        if (dvnTdrUser == null){
            LOG.error("ERROR no Dataverse user '" + dvnUser + "' and Trust Digital Repository name '" + tdrName + "'");
            return new ResponseEntity(DvnBridgeHelper.emptyJsonResponse(), HttpStatus.BAD_REQUEST);//Just temporary. Not the right way.
        }

        if(!isValidTdrCredentials(tdr.getIri(), dvnTdrUser.getTdrUsername(), dvnTdrUser.getTdrPassword())) {
            LOG.error("ERROR, Invallid TDR CREDENTIALS for user '" + dvnUser + "' and Trust Digital Repository name '" + tdrName + "'");
            return new ResponseEntity(DvnBridgeHelper.emptyJsonResponse(), HttpStatus.FORBIDDEN);//Just temporary. Not the right way.
        }
        Environment env = context.getEnvironment();
        XsltDvn2TdrTransformer xdeit = new XsltDvn2TdrTransformer(
                            env.getProperty("dataverse.ddi.export.url") + hdlPrefix + "/" + hdl
                                        , env.getProperty("dataverse.bridge.base.xsl.url"));
        LOG.info("Parsing....");
        DdiParser dp = new DdiParser(xdeit.getDocument());
        DvnBridgeDataset dvnBridgeDataset = dp.parse();
        LOG.info("Parsing is done...");
        ArchivingReport archivingReport = archivingReportDao.findByDatasetAndVersionAndDvnTdrUserId(dvnBridgeDataset.getPid(), dvnBridgeDataset.getVersion(), dvnTdrUser);

        if (archivingReport == null) { //The dataset isn't exported to a repo yet.
            LOG.info("No archiving report for " + dvnBridgeDataset.getPid());
            java.nio.file.Path bagTempDir = xdeit.createTempDirectory();
            LOG.info("Temporary bag directory: " + bagTempDir);

            List<DvnFile> dvnFiles = dvnBridgeDataset.getFiles();
            for (DvnFile dvnFile : dvnFiles) {
                dvnFile.setFilepath("data/" + dvnFile.getTitle());
                File dvnFileForIngest = new File(bagTempDir + "/" + dvnFile.getTitle());
                try {
                    //Check whether the file restricted or not, if it restricted use api-token to download it.
                    String url = dvnFile.getDvnFileUri();
                    //In our dataverse test server, the files are located on the Dataverse production production, so use the given file location
                    url = url.replaceAll("http([^<]*)/api/access/datafile", env.getProperty("dataverse.files.location"));
                    if (checkFilePermission(url) == FilePermissionStatus.RESTRICTED) {
                        dvnFile.setAccessRights("RESTRICTED_REQUEST");
                        FileUtils.copyURLToFile(new URL(url + "?key=" + dvnTdrUser.getDvnUserApitoken()), dvnFileForIngest);
                    } else {
                        FileUtils.copyURLToFile(new URL(url), dvnFileForIngest);
                    }
                } catch (IOException e) {
                    LOG.error("ERROR, msg: " + e.getMessage());
                    return new ResponseEntity(DvnBridgeHelper.emptyJsonResponse(), HttpStatus.BAD_REQUEST);//Just temporary. Not the right way.
                }
            }

            archivingReport = new ArchivingReport(dvnBridgeDataset.getPid(), Status.PROGRESS, dvnBridgeDataset.getVersion(), dvnTdrUser);
            archivingReportDao.create(archivingReport);
            long id = archivingReport.getId();
            try {

                Flowable.fromCallable(() -> {
                    composeBagit(xdeit, dvnBridgeDataset, bagTempDir);

                    File tempCopy = DvnBridgeHelper.copyToTarget(bagTempDir.toFile());
                    IDataverseIngest di = new IngestToEasy();
                    final String easyResponse = di.execute(tempCopy, new IRI(tdr.getIri()), dvnTdrUser.getTdrUsername(), dvnTdrUser.getTdrPassword());
                    LOG.info(easyResponse);
                    if (easyResponse == null || easyResponse.isEmpty()) {
                        LOG.error("ERROR no response, please check the target repository.");
                        //delete the record
                        //archivingReportServiceLocal.deleteById(insertedAr.getId());Don't delete the record but put status and report
                    } else {
                        LOG.info("Update the ArchivingReport record.");
                        ArchivingReport ar2 = archivingReportDao.getById(id);
                        ar2.setReport(easyResponse);
                        ar2.setLandingpage(di.getLandingPage());
                        ar2.setDoi(di.getDoi());
                        ar2.setStatus(Status.ARCHIVED.toString());
                        ar2.setEndIngestTime(new Date());
                        archivingReportDao.update(ar2);
                    }
                    return easyResponse;
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.single())
                        .subscribe(LOG::info, Throwable::printStackTrace);

            } catch (Exception e) {
                LOG.error("Error when ingesting process is running, msg: " + e.getMessage() );
            }
            return  new ResponseEntity(archivingReport, HttpStatus.OK);
        } else {
            return new ResponseEntity(archivingReport, HttpStatus.OK);
        }
    }

    private boolean isValidTdrCredentials(String url, String tdrUsername, String tdrPassword) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        UsernamePasswordCredentials creds
                = new UsernamePasswordCredentials(tdrUsername, tdrPassword);
        try {
            httpGet.addHeader(new BasicScheme().authenticate(creds, httpGet, null));
            CloseableHttpResponse response = client.execute(httpGet);
            //only looking for response code.
            if (response.getStatusLine().getStatusCode() == org.apache.http.HttpStatus.SC_OK)
                return true;
        } catch (AuthenticationException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void composeBagit(XsltDvn2TdrTransformer xdeit, DvnBridgeDataset dvnBridgeDataset, java.nio.file.Path bagTempDir) {
        BagFactory bf = new BagFactory();
        BagInfoCompleter bic = new BagInfoCompleter(bf);
        DefaultCompleter dc = new DefaultCompleter(bf);
        dc.setPayloadManifestAlgorithm(Manifest.Algorithm.SHA1);
        TagManifestCompleter tmc = new TagManifestCompleter(bf);
        tmc.setTagManifestAlgorithm(Manifest.Algorithm.SHA1);

        ChainingCompleter completer = new ChainingCompleter(dc, new BagInfoCompleter(bf), tmc);

        PreBag pb = bf.createPreBag(bagTempDir.toFile());
        pb.makeBagInPlace(BagFactory.Version.V0_97, false, completer);

        Bag b = bf.createBag(bagTempDir.toFile());
        xdeit.createMetadata();

        //Check whether the dataset contains at least one restricted file.
        //In this case, it needs to create files.xml as replacement of the xslt generated files.xml
        List<DvnFile> dfiles = dvnBridgeDataset.getFiles();
        for (DvnFile d : dfiles) {
            if (d.getAccessRights() != null && d.getAccessRights().equals("RESTRICTED_REQUEST")) {
                //create files.xml
                FilesXmlCreator fxc = new FilesXmlCreator();
                File f = new File(bagTempDir.toString() + "/metadata/files.xml");
                if (f.exists())
                    f.delete();
                fxc.create(dfiles, f);
                break;
            }
        }
    }

    private JsonObject getResponseAsJsonObject(String url) {
        JsonObject jsonObject = null;
        try {
            URL validUrl = new URL(url);
            if (validUrl != null) {
                JsonReader reader = javax.json.Json.createReader(
                        new StringReader(IOUtils.toString(validUrl, "UTF-8")));
                jsonObject = reader.readObject();
                reader.close();
            } else
                LOG.error(url + " is not valid.");
        } catch (IOException e) {
            LOG.error("IOException, message: " + e.getMessage());
        }
        return jsonObject;
    }

    private FilePermissionStatus checkFilePermission(String url) {
        URL validUrl;
        try {
            validUrl = new URL(url);
            HttpURLConnection huc = (HttpURLConnection) validUrl.openConnection();
            int rc = huc.getResponseCode();
            if (rc == HttpURLConnection.HTTP_OK)
                return FilePermissionStatus.OK;
            else if (rc == HttpURLConnection.HTTP_FORBIDDEN)
                return FilePermissionStatus.RESTRICTED;
            else
                LOG.error(url + " response gives status other 200 (HTTP_OK). Response code: " + rc);
        } catch (MalformedURLException e) {
            LOG.error("MalformedURLException, message: " + e.getMessage());
        } catch (IOException e) {
            LOG.error("IOException, message: " + e.getMessage());
        }
        return FilePermissionStatus.OTHER;
    }

    enum FilePermissionStatus {
        OTHER,
        OK,
        RESTRICTED;

        public String toString() {
            switch (this) {
                case OK:
                    return "OK";
                case RESTRICTED:
                    return "RESTRICTED";
            }
            return "OTHER";//default
        }
    }
}

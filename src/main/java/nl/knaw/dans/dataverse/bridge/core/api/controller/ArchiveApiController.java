package nl.knaw.dans.dataverse.bridge.core.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.PreBag;
import gov.loc.repository.bagit.transformer.impl.ChainingCompleter;
import gov.loc.repository.bagit.transformer.impl.DefaultCompleter;
import gov.loc.repository.bagit.transformer.impl.TagManifestCompleter;
import io.reactivex.Flowable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import nl.knaw.dans.dataverse.bridge.core.db.dao.ArchivedDao;
import nl.knaw.dans.dataverse.bridge.core.db.domain.Archived;
import nl.knaw.dans.dataverse.bridge.core.bagit.BagInfoCompleter;
import nl.knaw.dans.dataverse.bridge.core.util.StateEnum;
import nl.knaw.dans.dataverse.bridge.ingest.ArchivedObject;
import nl.knaw.dans.dataverse.bridge.ingest.IDataverseIngest;
import nl.knaw.dans.dataverse.bridge.ingest.tdrplugins.danseasy.EasyFilesXmlCreator;
import nl.knaw.dans.dataverse.bridge.ingest.tdrplugins.danseasy.IngestToEasy;
import nl.knaw.dans.dataverse.bridge.core.util.BridgeHelper;
import nl.knaw.dans.dataverse.bridge.generated.api.ArchiveApi;
import nl.knaw.dans.dataverse.bridge.generated.api.NotFoundException;
import nl.knaw.dans.dataverse.bridge.generated.model.Error;
import nl.knaw.dans.dataverse.bridge.generated.model.IngestData;
import nl.knaw.dans.dataverse.bridge.source.dataverse.*;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Document;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-27T17:13:14.758+02:00")

@Controller
public class ArchiveApiController implements ArchiveApi {

    @Autowired
    Environment env;

    @Value("${server.address}")
    private String serverAddress;
    @Value("${server.port}")
    private String serverPort;
    @Value("${server.contextPath}")
    private String contextPath;

    @Autowired
    ArchivedDao archivedDao;

    private static final Logger LOG = LoggerFactory.getLogger(ArchiveApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public ArchiveApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @Override
    public Optional<ObjectMapper> getObjectMapper() {
        return Optional.ofNullable(objectMapper);
    }

    @Override
    public Optional<HttpServletRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @Override
    @ApiOperation(value = "peration to retrive all Archived datasets", nickname = "getAll", notes = "Operation to retrive all Archived datasets", response = nl.knaw.dans.dataverse.bridge.core.db.domain.Archived.class, responseContainer = "List", tags={ "archiving", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Plugin response", response = nl.knaw.dans.dataverse.bridge.core.db.domain.Archived.class, responseContainer = "List"),
            @ApiResponse(code = 200, message = "unexpected error", response = Error.class) })
    @RequestMapping(value = "/archive/get-all",
            produces = { "application/json" },
            method = RequestMethod.GET)
    public ResponseEntity<List<nl.knaw.dans.dataverse.bridge.core.db.domain.Archived>> getAll() {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    List<Archived> archiveds = archivedDao.getAll();
                    return new ResponseEntity<>(getObjectMapper().get().readValue(objectMapper.writeValueAsString(archiveds), List.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default ArchiveApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    @ApiOperation(value = "Operation to retrive a state of an Archived dataset", nickname = "getState", notes = "Operation to retrive a state of an Archived dataset by filtering pid, version, tdr target.", response = nl.knaw.dans.dataverse.bridge.core.db.domain.Archived.class, tags={ "archiving", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Plugin response", response = nl.knaw.dans.dataverse.bridge.core.db.domain.Archived.class),
            @ApiResponse(code = 200, message = "unexpected error", response = Error.class) })
    @RequestMapping(value = "/archive/state",
            produces = { "application/json" },
            method = RequestMethod.GET)
    public ResponseEntity<nl.knaw.dans.dataverse.bridge.core.db.domain.Archived> getState(@NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "srcXml", required = true) String srcXml,@NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "srcVersion", required = true) String srcVersion,@NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "targetIri", required = true) String targetIri) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    Archived dbArchived = archivedDao.getBySrcxmlSrcversionTargetiri(srcXml, srcVersion, targetIri);
                    if (dbArchived == null) {
                        return new ResponseEntity<>(getObjectMapper().get().readValue(objectMapper.writeValueAsString(dbArchived), nl.knaw.dans.dataverse.bridge.core.db.domain.Archived.class), HttpStatus.NOT_FOUND);
                    }

                    return new ResponseEntity<>(getObjectMapper().get().readValue(objectMapper.writeValueAsString(dbArchived), nl.knaw.dans.dataverse.bridge.core.db.domain.Archived.class), HttpStatus.OK);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default ArchiveApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Override
    @ApiOperation(value = "Operation to create a new Archive", nickname = "createArchive", notes = "Add a new archive to TDR", response = nl.knaw.dans.dataverse.bridge.core.db.domain.Archived.class, tags={ "archiving", })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Dataset succesfully created.", response = nl.knaw.dans.dataverse.bridge.core.db.domain.Archived.class),
            @ApiResponse(code = 400, message = "Dataset couldn't have been created."),
            @ApiResponse(code = 405, message = "Invalid input") })
    @RequestMapping(value = "/archive/create",
            produces = { "application/json" },
            consumes = { "application/json" },
            method = RequestMethod.POST)
    public ResponseEntity<nl.knaw.dans.dataverse.bridge.core.db.domain.Archived> createArchive(@ApiParam(value = "Dataset object that needs to be added to the Archived's table." ,required=true )  @Valid @RequestBody IngestData ingestData) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    Archived dbArchived = archivedDao.getBySrcxmlSrcversionTargetiri(ingestData.getSrcData().getSrcXml()
                                                                                        , ingestData.getSrcData().getSrcVersion()
                                                                                        , ingestData.getTdrData().getIri());
                    if (dbArchived != null) {
                        //existing archived or archiving in progress
                        return new ResponseEntity<>(getObjectMapper().get().readValue(objectMapper.writeValueAsString(dbArchived), nl.knaw.dans.dataverse.bridge.core.db.domain.Archived.class), HttpStatus.OK);
                    }

                    if (invalidCredentials(ingestData))
                        return new ResponseEntity<>(HttpStatus.FORBIDDEN);

                    if (ingestData.getSrcData().getAppName().equals("DATAVERSE") && ingestData.getTdrData().getAppName().equals("EASY")) {
                        String bridgeServerBaseUrl = "http://" + serverAddress + ":" + serverPort + contextPath + "/";
                        dbArchived = ingestToEASY(bridgeServerBaseUrl, ingestData);
                    }
                    return new ResponseEntity<>(getObjectMapper().get().readValue(objectMapper.writeValueAsString(dbArchived), nl.knaw.dans.dataverse.bridge.core.db.domain.Archived.class), HttpStatus.CREATED);
                } catch (IOException e) {
                    if (e.getMessage().contains("Connection timed out")) {
                        log.error(e.getMessage());
                        return new ResponseEntity<>(HttpStatus.REQUEST_TIMEOUT);
                    }
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                } catch (NotFoundException e) {
                    log.error("NotFoundException: " + e.getMessage());
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default ArchiveApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    private boolean invalidCredentials(IngestData ingestData) throws URISyntaxException, IOException {
        //check TDR credentials
        CloseableHttpClient httpClient = BridgeHelper.createHttpClient((new IRI(ingestData.getTdrData().getIri())).toURI()
                                                                            , ingestData.getTdrData().getUsername()
                                                                            , ingestData.getTdrData().getPassword());
        HttpGet httpGet = new HttpGet("http://deasy.dans.knaw.nl/sword2/servicedocument");
        CloseableHttpResponse response = httpClient.execute(httpGet);
        return (response.getStatusLine().getStatusCode() == org.apache.http.HttpStatus.SC_FORBIDDEN);
    }

    private Archived ingestToEASY(String bridgeServerBaseUrl, IngestData ingestData) throws NotFoundException, IOException {

        IngestDataComposer ingestDataComposer = new IngestDataComposer(bridgeServerBaseUrl, ingestData).invoke();
        Dv2TdrTransformer dv2TdrTransformer = ingestDataComposer.getDv2TdrTransformer();
        DvBridgeDataset dvBridgeDataset = ingestDataComposer.getDvBridgeDataset();
        Path bagTempDir = ingestDataComposer.getBagTempDir();

        Archived archived = createNewArchived(ingestData);
        final ArchivedObjectHolder archivedObjectHolderState = new ArchivedObjectHolder();
        Flowable.fromCallable(() -> {
            composeBagit(dv2TdrTransformer, dvBridgeDataset, bagTempDir);

            File tempCopy = BridgeHelper.copyToTarget(bagTempDir.toFile());
            IDataverseIngest di = new IngestToEasy();
            final ArchivedObject easyResponse = di.execute(tempCopy, new IRI(ingestData.getTdrData().getIri()), ingestData.getTdrData().getUsername(), ingestData.getTdrData().getPassword());
            archived.setState(StateEnum.IN_PROGRESS.toString());
            archivedDao.create(archived);
            archivedObjectHolderState.setArchivedObject(easyResponse);
            LOG.info("status: " + easyResponse.getStatus());
            return archived;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .doOnComplete(new Action() {
                    @Override
                    public void run()  {
                        ArchivedObject ao = archivedObjectHolderState.getArchivedObject();
                        String status  = ao.getStatus();
                        archived.setLandingPage(ao.getLandingPage());
                        archived.setDoi(ao.getPid());
                        archived.setEndTime(new Date());
                        archived.setState(ao.getStatus());
                        LOG.info("Ingest finish. Status " + ao.getStatus());
                        archivedDao.update(archived);
                        if (!ao.getStatus().equals(StateEnum.ARCHIVED)) {
                            //send mail to admin
                        }
//                        if (archivedObjectHolderState.getState().equals("SUCCESS")){
//                            archivedDao.update(archived);
//                            //check state, it can be failed or archived
//                            //send mail to ingester
//                            LOG.info("Archiving finish.");
//                        } else {
//                            //rollback
//                          //  archivedDao.delete(archived);
//                        }
                    }
                })
                .subscribe();

        return archived;
    }
    private class ArchivedObjectHolder {
        private boolean finish;
        ArchivedObject archivedObject;

        public ArchivedObject getArchivedObject() {
            return archivedObject;
        }

        public void setArchivedObject(ArchivedObject archivedObject) {
            this.archivedObject = archivedObject;
        }

        public boolean isFinish() {
            return finish;
        }

        public void setFinish(boolean finish) {
            this.finish = finish;
        }
    }

    private Archived createNewArchived(IngestData ingestData) {
        Archived archived = new Archived();
        archived.setStartTime(new Date());
        archived.setSrcXml(ingestData.getSrcData().getSrcXml());
        archived.setSrcVersion(ingestData.getSrcData().getSrcVersion());
        archived.setSrcAppName(ingestData.getSrcData().getAppName());
        archived.setTargetIri(ingestData.getTdrData().getIri());
        archived.setTdrAppName(ingestData.getTdrData().getAppName());
        return archived;
    }


    private void composeBagit(Dv2TdrTransformer dv2TdrTransformer, DvBridgeDataset dvBridgeDataset, java.nio.file.Path bagTempDir) throws TransformerException, ParserConfigurationException, IOException {
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
        dv2TdrTransformer.createMetadata();

        //Check whether the dataset contains at least one restricted file.
        //In this case, it needs to create files.xml as replacement of the xslt generated files.xml
        List<DvFile> dfiles = dvBridgeDataset.getFiles();
        for (DvFile d : dfiles) {
            if (d.getAccessRights() != null && d.getAccessRights().equals("RESTRICTED_REQUEST")) {
                //create files.xml
                EasyFilesXmlCreator fxc = new EasyFilesXmlCreator();
                File f = new File(bagTempDir.toString() + "/metadata/files.xml");
                if (f.exists())
                    f.delete();
                fxc.create(dfiles, f);
                break;
            }
        }
    }

    private class IngestDataComposer {
        private IngestData ingestData;
        private Dv2TdrTransformer dv2TdrTransformer;
        private DvBridgeDataset dvBridgeDataset;
        private Path bagTempDir;
        private String bridgeServerBaseUrl;

        public IngestDataComposer(String bridgeServerBaseUrl, IngestData ingestData) {
            this.bridgeServerBaseUrl = bridgeServerBaseUrl;
            this.ingestData = ingestData;
        }

        public Dv2TdrTransformer getDv2TdrTransformer() {
            return dv2TdrTransformer;
        }

        public DvBridgeDataset getDvBridgeDataset() {
            return dvBridgeDataset;
        }

        public Path getBagTempDir() {
            return bagTempDir;
        }

        public IngestDataComposer invoke() throws NotFoundException, IOException {
            dv2TdrTransformer = new Dv2TdrTransformer(ingestData.getSrcData().getSrcXml()
                                                        , new StreamSource(bridgeServerBaseUrl + env.getProperty("bridge.xsl.source.easy.dataset"))
                                                        , new StreamSource(bridgeServerBaseUrl + env.getProperty("bridge.xsl.source.easy.files")));
            Document ddiDocument = dv2TdrTransformer.getDocument();
            DdiParser dp = new DdiParser(ddiDocument, createExportedDdiFile(), createExportedJsonFile());
            dvBridgeDataset = dp.parse();
            bagTempDir = dv2TdrTransformer.createTempDirectory();
            LOG.info("Temporary bag directory: " + bagTempDir);
            List<DvFile> dvFiles = dvBridgeDataset.getFiles();

            for (DvFile dvFile : dvFiles) {
                dvFile.setFilepath("data/" + dvFile.getTitle());
                File dvnFileForIngest = new File(bagTempDir + "/" + dvFile.getTitle());
                try {
                    //Check whether the file restricted or not, if it restricted use api-token to download it.
                    String url = dvFile.getDvFileUri();
                    //Since the URL of th files is hardcoded to 'https'( see: systemConfg.getDataverseSiteUrl()),
                    // for ddvn, replace to https
                    url = url.replace("https://ddvn.dans.knaw.nl","http://ddvn.dans.knaw.nl");
                    if (FilePermissionChecker.check(url) == FilePermissionStatus.RESTRICTED) {
                        dvFile.setAccessRights("RESTRICTED_REQUEST");
                        //FileUtils.copyURLToFile(new URL(url + "?key=" + dvnTdrUser.getDvnUserApitoken()), dvnFileForIngest);
                    } else {
                        FileUtils.copyURLToFile(new URL(url), dvnFileForIngest);
                    }
                } catch (IOException e) {
                    LOG.error("ERROR, IOException: " + e.getMessage());
                }
            }
            return this;
        }

        private DvFile createExportedDvFile(String filename, String url) {
            DvFile exportedDvFile = new DvFile();
            exportedDvFile.setTitle(filename);
            exportedDvFile.setFilepath(filename);
            exportedDvFile.setDvFileUri(url);
            return exportedDvFile;
        }

        private DvFile createExportedDdiFile() {
            //ddi xml: http://ddvn.dans.knaw.nl:8080/api/datasets/export?exporter=ddi&persistentId=hdl:12345/JLO8HN
            return createExportedDvFile(getExportedDvFilename("xml"), ingestData.getSrcData().getSrcXml());
        }

        private DvFile createExportedJsonFile() {
            //json: http://ddvn.dans.knaw.nl:8080/api/datasets/:persistentId/?persistentId=hdl:12345/JLO8HN
            String url = ingestData.getSrcData().getSrcXml().replace("export?exporter=ddi&", ":persistentId/?");
            return createExportedDvFile(getExportedDvFilename("json"), url);
        }

        private String getExportedDvFilename(String ext) {
            return (ingestData.getSrcData().getSrcXml().split("persistentId=")[1])
                                        .replace(":","-")
                                        .replace("/","-") + "." + ext;
        }
    }
    public String getDdiDocumentAsString(Document ddiDocument) {
        DOMSource domSource = new DOMSource(ddiDocument);
        StringWriter writer = new StringWriter();
        try {
            TransformerFactory.newInstance().newTransformer().transform(domSource, new StreamResult(writer));
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }
}

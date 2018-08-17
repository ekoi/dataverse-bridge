package nl.knaw.dans.dataverse.bridge.core.api.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
import nl.knaw.dans.dataverse.bridge.core.bagit.BagInfoCompleter;
import nl.knaw.dans.dataverse.bridge.core.db.dao.ArchivedDao;
import nl.knaw.dans.dataverse.bridge.core.db.domain.Archived;
import nl.knaw.dans.dataverse.bridge.core.util.BridgeHelper;
import nl.knaw.dans.dataverse.bridge.core.util.StateEnum;
import nl.knaw.dans.dataverse.bridge.generated.api.ArchiveApi;
import nl.knaw.dans.dataverse.bridge.generated.model.Error;
import nl.knaw.dans.dataverse.bridge.generated.model.IngestData;
import nl.knaw.dans.dataverse.bridge.ingest.ArchivedObject;
import nl.knaw.dans.dataverse.bridge.ingest.IDataverseIngest;
import nl.knaw.dans.dataverse.bridge.ingest.tdrplugins.danseasy.Dv2EasyTransformer;
import nl.knaw.dans.dataverse.bridge.ingest.tdrplugins.danseasy.IngestToEasy;
import org.apache.abdera.i18n.iri.IRI;
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
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-27T17:13:14.758+02:00")

@Controller
public class ArchiveApiController implements ArchiveApi {

    @Autowired
    Environment env;
    @Autowired
    private JavaMailSender mailSender;

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
                        LOG.error("The following request is NOT FOUND: srcXml: " + srcXml + "\tsrcVersion: " + srcVersion + "\ttargetIri: " + targetIri);
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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

                    int statusCode = checkCredentials(ingestData);
                    switch (statusCode) {
                        case org.apache.http.HttpStatus.SC_REQUEST_TIMEOUT:
                            return new ResponseEntity<>(HttpStatus.REQUEST_TIMEOUT);
                        case org.apache.http.HttpStatus.SC_FORBIDDEN:
                            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                        case org.apache.http.HttpStatus.SC_OK:
                            if (ingestData.getSrcData().getAppName().equals("DATAVERSE") && ingestData.getTdrData().getAppName().equals("EASY")) {
                                String bridgeServerBaseUrl = "http://" + serverAddress + ":" + serverPort + contextPath + "/";
                                dbArchived = ingestToEASY(bridgeServerBaseUrl, ingestData);
                                if (dbArchived != null)
                                    return new ResponseEntity<>(getObjectMapper().get().readValue(objectMapper.writeValueAsString(dbArchived), Archived.class), HttpStatus.CREATED);
                            }
                    }
                } catch (URISyntaxException e) {
                    log.error("URISyntaxException: " + e.getMessage());
                } catch (JsonParseException e) {
                    log.error("Couldn't serialize response for content type application/json", e);;
                } catch (JsonMappingException e) {
                    log.error("JsonMappingException: " + e.getMessage());
                } catch (JsonProcessingException e) {
                    log.error("JsonProcessingException: " + e.getMessage());
                } catch (IOException e) {
                    log.error("IOException: " + e.getMessage());
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default ArchiveApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    @ApiOperation(value = "Deletes a record", nickname = "deleteById", notes = "", tags={ "archiving", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Record is deleted"),
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Record not found") })
    @RequestMapping(value = "/archive/{id}",
            produces = { "application/xml", "application/json" },
            method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteById(@ApiParam(value = "" ,required=true) @RequestHeader(value="api_key", required=true) String apiKey,@ApiParam(value = "Record id to delete",required=true) @PathVariable("id") Long id) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if(!apiKey.equals( env.getProperty("bridge.apikey")))
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

            Archived dbArchived = archivedDao.getById(id);
            if (archivedDao != null) {
                archivedDao.delete(dbArchived);
                return new ResponseEntity<>(HttpStatus.OK);
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default ArchiveApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Override
    @ApiOperation(value = "Deletes a record", nickname = "deleteByParams", notes = "", tags={ "archiving", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Record is deleted"),
            @ApiResponse(code = 400, message = "Invalid Paramas supplied"),
            @ApiResponse(code = 404, message = "Record not found") })
    @RequestMapping(value = "/archive",
            produces = { "application/xml", "application/json" },
            method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteByParams(@ApiParam(value = "" ,required=true) @RequestHeader(value="api_key", required=true) String apiKey,@NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "srcXml", required = true) String srcXml,@NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "srcVersion", required = true) String srcVersion,@NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "targetIri", required = true) String targetIri) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            Archived dbArchived = archivedDao.getBySrcxmlSrcversionTargetiri(srcXml, srcVersion, targetIri);
            if (archivedDao != null) {
                archivedDao.delete(dbArchived);
                return new ResponseEntity<>(HttpStatus.OK);
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default ArchiveApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Override
    @ApiOperation(value = "Updated Archive", nickname = "updateArchive", notes = "Update the existing Archive.", tags={ "archiving", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Record is updated"),
            @ApiResponse(code = 400, message = "Invalid id supplied"),
            @ApiResponse(code = 404, message = "Archived not found") })
    @RequestMapping(value = "/archive",
            produces = { "application/json", "application/xml" },
            method = RequestMethod.PUT)
    public ResponseEntity<Void> updateArchive(@ApiParam(value = "" ,required=true) @RequestHeader(value="api_key", required=true) String apiKey,@ApiParam(value = "Updated archive object" ,required=true )  @Valid @RequestBody nl.knaw.dans.dataverse.bridge.core.db.domain.Archived archive) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if(!apiKey.equals( env.getProperty("bridge.apikey")))
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

            Archived dbArchived = archivedDao.getBySrcxmlSrcversionTargetiri(archive.getSrcXml(), archive.getSrcVersion(), archive.getTargetIri());
            if (dbArchived == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);

            archivedDao.update(archive);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default ArchiveApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    private int checkCredentials(IngestData ingestData) throws URISyntaxException {
        //check TDR credentials
        try(CloseableHttpClient httpClient = BridgeHelper.createHttpClient((new IRI(ingestData.getTdrData().getIri())).toURI()
                                                                            , ingestData.getTdrData().getUsername()
                                                                            , ingestData.getTdrData().getPassword())){
            HttpGet httpGet = new HttpGet(ingestData.getTdrData().getIri());
            CloseableHttpResponse response = httpClient.execute(httpGet);
            return response.getStatusLine().getStatusCode();
        } catch (IOException e) {
            return org.apache.http.HttpStatus.SC_REQUEST_TIMEOUT;
        }
    }

    private Archived ingestToEASY(String bridgeServerBaseUrl, IngestData ingestData) {
        Archived archived = createNewArchived(ingestData);
        final ArchivedObjectHolder archivedObjectHolderState = new ArchivedObjectHolder();
        Flowable.fromCallable(() -> {
            Dv2EasyTransformer dv2EasyTransformer = new Dv2EasyTransformer(ingestData.getSrcData().getSrcXml()
                    , ingestData.getSrcData().getApiToken()
                    , new StreamSource(bridgeServerBaseUrl + env.getProperty("bridge.xsl.source.easy.dataset"))
                    , new StreamSource(bridgeServerBaseUrl + env.getProperty("bridge.xsl.source.easy.files")));
            boolean metadataIsCreated = dv2EasyTransformer.createMetadata();
            if (!metadataIsCreated) {
                LOG.error("ERROR: Metadata is not created.");
                return null;
            }
            Path bagTempDir = dv2EasyTransformer.getBagTempDir();
            composeBagit(dv2EasyTransformer);
            IDataverseIngest di = new IngestToEasy();
            final ArchivedObject easyResponse = di.execute(bagTempDir.toFile(), new IRI(ingestData.getTdrData().getIri()), ingestData.getTdrData().getUsername(), ingestData.getTdrData().getPassword());
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
        archived.setState(StateEnum.IN_PROGRESS.toString());
        archivedDao.create(archived);
        return archived;
    }


    private void composeBagit(Dv2EasyTransformer dv2EasyTransformer) throws TransformerException, ParserConfigurationException, IOException {
        BagFactory bf = new BagFactory();
        BagInfoCompleter bic = new BagInfoCompleter(bf);
        DefaultCompleter dc = new DefaultCompleter(bf);
        dc.setPayloadManifestAlgorithm(Manifest.Algorithm.SHA1);
        TagManifestCompleter tmc = new TagManifestCompleter(bf);
        tmc.setTagManifestAlgorithm(Manifest.Algorithm.SHA1);
        ChainingCompleter completer = new ChainingCompleter(dc, new BagInfoCompleter(bf), tmc);
        PreBag pb = bf.createPreBag(dv2EasyTransformer.getBagTempDir().toFile());
        pb.makeBagInPlace(BagFactory.Version.V0_97, false, completer);
        Bag b = bf.createBag(dv2EasyTransformer.getBagTempDir().toFile());
    }
}

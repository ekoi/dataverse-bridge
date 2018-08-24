package nl.knaw.dans.dataverse.bridge.core.api.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import nl.knaw.dans.dataverse.bridge.core.api.config.BridgeConfEnvironment;
import nl.knaw.dans.dataverse.bridge.core.common.IAction;
import nl.knaw.dans.dataverse.bridge.core.common.ResponseDataHolder;
import nl.knaw.dans.dataverse.bridge.core.common.TdrConf;
import nl.knaw.dans.dataverse.bridge.core.db.dao.ArchivedDao;
import nl.knaw.dans.dataverse.bridge.core.db.domain.Archived;
import nl.knaw.dans.dataverse.bridge.core.util.BridgeHelper;
import nl.knaw.dans.dataverse.bridge.core.util.StateEnum;
import nl.knaw.dans.dataverse.bridge.exception.BridgeException;
import nl.knaw.dans.dataverse.bridge.generated.api.ArchiveApi;
import nl.knaw.dans.dataverse.bridge.generated.model.Error;
import nl.knaw.dans.dataverse.bridge.generated.model.IngestData;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
/*
    @author Eko Indarto
    This class is needed since the Spring's autowiring happens too late.
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-27T17:13:14.758+02:00")

@Controller
public class ArchiveApiController implements ArchiveApi {
    @Autowired
    Environment env;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    ArchivedDao archivedDao;

    @Autowired
    BridgeConfEnvironment bcenv;

    private static final Logger LOG = LoggerFactory.getLogger(ArchiveApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    private static List<TdrConf> tdrConfList = new ArrayList<TdrConf>();

    @org.springframework.beans.factory.annotation.Autowired
    public ArchiveApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
        tdrConfList = bcenv.getTdrConfList();
        LOG.info(tdrConfList.get(0).getTdrName());
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
    @ApiOperation(value = "Operation to retrive all Archived datasets", nickname = "getAll", notes = "Operation to retrive all Archived datasets", response = nl.knaw.dans.dataverse.bridge.core.db.domain.Archived.class, responseContainer = "List", tags={ "Archiving", })
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
    @ApiOperation(value = "Operation to retrive a state of an Archived dataset", nickname = "getState", notes = "Operation to retrive a state of an Archived dataset by filtering pid, version, tdr target.", response = nl.knaw.dans.dataverse.bridge.core.db.domain.Archived.class, tags={ "Archiving", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Plugin response", response = nl.knaw.dans.dataverse.bridge.core.db.domain.Archived.class),
            @ApiResponse(code = 200, message = "unexpected error", response = Error.class) })
    @RequestMapping(value = "/archive/state",
            produces = { "application/json" },
            method = RequestMethod.GET)
    public ResponseEntity<nl.knaw.dans.dataverse.bridge.core.db.domain.Archived> getState(@NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "srcMetadataXml", required = true) String srcMetadataXml,@NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "srcMetadataVersion", required = true) String srcMetadataVersion,@NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "targetTdrName", required = true) String targetTdrName) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    Archived dbArchived = archivedDao.getBySrcxmlSrcversionTargetiri(srcMetadataXml, srcMetadataVersion, targetTdrName);
                    if (dbArchived == null) {
                        LOG.error("The following request is NOT FOUND: srcMetadataXml: " + srcMetadataXml + "\tsrcMetadataVersion: " + srcMetadataVersion + "\ttargetTdrName: " + targetTdrName);
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
    @ApiOperation(value = "Operation to create a new Archive", nickname = "createArchive", notes = "Add a new archive to TDR", response = nl.knaw.dans.dataverse.bridge.core.db.domain.Archived.class, tags={ "Archiving", })
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
                    Optional<TdrConf> tdrConf = tdrConfList.stream().filter(x -> x.getTdrName().equals(ingestData.getTdrData().getTdrName())).findAny();
                    if (tdrConf.isPresent()) {
                        int statusCode = checkCredentials(tdrConf.get().getIri()
                                                            , ingestData.getTdrData().getUsername()
                                                            , ingestData.getTdrData().getPassword()
                                                            , env.getProperty("bridge.tdr.timeout", Integer.class));
                        switch (statusCode) {
                            case org.apache.http.HttpStatus.SC_REQUEST_TIMEOUT:
                                return new ResponseEntity<>(HttpStatus.REQUEST_TIMEOUT);
                            case org.apache.http.HttpStatus.SC_FORBIDDEN:
                                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                            case org.apache.http.HttpStatus.SC_OK:
                                Archived dbArchived = archivedDao.getBySrcxmlSrcversionTargetiri(ingestData.getSrcData().getSrcXml()
                                        , ingestData.getSrcData().getSrcVersion()
                                        , ingestData.getTdrData().getTdrName());
                                if (dbArchived != null) {
                                    //existing archived or archiving in progress
                                    return new ResponseEntity<>(getObjectMapper().get().readValue(objectMapper.writeValueAsString(dbArchived), nl.knaw.dans.dataverse.bridge.core.db.domain.Archived.class), HttpStatus.OK);
                                }
                                IngestToTdr(ingestData, tdrConf.get());
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
                } catch (IllegalAccessException e) {
                    log.error("IllegalAccessException: " + e.getMessage());
                } catch (InstantiationException e) {
                    log.error("InstantiationException: " + e.getMessage());
                } catch (ClassNotFoundException e) {
                    log.error("ClassNotFoundException: " + e.getMessage());
                    //send mail
                }

            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default ArchiveApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    @ApiOperation(value = "", nickname = "getById", notes = "", response = nl.knaw.dans.dataverse.bridge.core.db.domain.Archived.class, tags={ "Archiving", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Record Id to search", response = nl.knaw.dans.dataverse.bridge.core.db.domain.Archived.class),
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Record not found") })
    @RequestMapping(value = "/archive/{id}",
            produces = { "application/json" },
            method = RequestMethod.GET)
    public ResponseEntity<nl.knaw.dans.dataverse.bridge.core.db.domain.Archived> getById(@ApiParam(value = "Record id",required=true) @PathVariable("id") Long id) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("{  \"srcXml\" : \"srcXml\",  \"landingPage\" : \"landingPage\",  \"pid\" : \"pid\",  \"startTime\" : \"2000-01-23\",  \"id\" : 0,  \"endTime\" : \"2000-01-23\",  \"state\" : \"IN-PROGRESS\",  \"srcVersion\" : \"srcVersion\",  \"targetIri\" : \"targetIri\"}", nl.knaw.dans.dataverse.bridge.core.db.domain.Archived.class), HttpStatus.NOT_IMPLEMENTED);
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

    @ApiOperation(value = "", nickname = "getByIdAndState", notes = "", response = nl.knaw.dans.dataverse.bridge.core.db.domain.Archived.class, tags={ "Archiving", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Record Id to search", response = nl.knaw.dans.dataverse.bridge.core.db.domain.Archived.class),
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Record not found") })
    @RequestMapping(value = "/archive/{id}/{state}",
            produces = { "application/json" },
            method = RequestMethod.GET)
    public ResponseEntity<nl.knaw.dans.dataverse.bridge.core.db.domain.Archived> getByIdAndState(@ApiParam(value = "Record id",required=true) @PathVariable("id") Long id,@ApiParam(value = "",required=true) @PathVariable("state") String state) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("{  \"srcXml\" : \"srcXml\",  \"landingPage\" : \"landingPage\",  \"pid\" : \"pid\",  \"startTime\" : \"2000-01-23\",  \"id\" : 0,  \"endTime\" : \"2000-01-23\",  \"state\" : \"IN-PROGRESS\",  \"srcVersion\" : \"srcVersion\",  \"targetIri\" : \"targetIri\"}", nl.knaw.dans.dataverse.bridge.core.db.domain.Archived.class), HttpStatus.NOT_IMPLEMENTED);
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

    private Archived IngestToTdr(IngestData ingestData, TdrConf tdrCfg) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        LOG.info(">>>>>>> Tryng to ingest to " + ingestData.getTdrData().getTdrName() + " from url source: " + ingestData.getSrcData().getSrcXml() + " To ");
        Archived archived = createNewArchived(ingestData, tdrCfg);
        Class actionClass = Class.forName(tdrCfg.getActionClassName());
        IAction action = (IAction)actionClass.newInstance();
        Flowable.fromCallable(() -> {
            String bagDir = env.getProperty("bridge.temp.dir.bags");
            Map<String, String> transformResult = action.transform(ingestData.getSrcData().getSrcXml(), ingestData.getSrcData().getApiToken(), tdrCfg.getXsl());
            Optional<File> bagitFile = action.composeBagit(bagDir, ingestData.getSrcData().getApiToken(), ingestData.getSrcData().getSrcXml(), transformResult);
            if(bagitFile.isPresent()){
                archived.setBagitDir(bagitFile.get().getAbsolutePath().replace(".zip", ""));
            }
            ResponseDataHolder responseDataHolder = action.execute(bagitFile, new IRI(tdrCfg.getIri()), ingestData.getTdrData().getUsername(), Optional.of(ingestData.getTdrData().getPassword()));

            return responseDataHolder;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .doOnError(ex -> {
                    String msg="";
                    if (ex instanceof BridgeException) {
                        BridgeException be = (BridgeException) ex;
                        msg = "[" + be.getClassName() + "] " + be.getMessage();

                    } else {
                        msg = ex.getMessage();
                    }
                    LOG.error(msg);
                    String prevMsg = archived.getAuditLog();
                    if (prevMsg != null)
                        msg = prevMsg + "|" + msg;
                    archived.setAuditLog(msg);
                    archived.setState(StateEnum.ERROR.toString());
                    archived.setEndTime(new Date());
                    archivedDao.update(archived);
                })
                .subscribe(erd -> {
                    saveAndClean(archived, erd);
                }, throwable -> {
                    LOG.error(throwable.getMessage());
                });
        return archived;
    }

    private void saveAndClean(Archived archived, ResponseDataHolder erd) {
        archived.setLandingPage(erd.getLandingPage());
        archived.setPid(erd.getPid());
        archived.setEndTime(new Date());
        archived.setState(erd.getState());
        archived.setAuditLog(erd.getFeedXml());
        LOG.info("Ingest finish. Status " + erd.getState());
        if (erd.getState().equals(StateEnum.ARCHIVED.toString())) {
            //send mail to admin
            //delete bagitdir and its zip.
            LOG.info(archived.getBagitDir());
            File bagDirToDelete = FileUtils.getFile(archived.getBagitDir());
            boolean bagDirIsDeleted = FileUtils.deleteQuietly(bagDirToDelete);
            File bagZipFileToDelete = FileUtils.getFile(archived.getBagitDir() + ".zip");
            boolean bagZipFileIsDeleted = FileUtils.deleteQuietly(bagZipFileToDelete);
            if (bagDirIsDeleted && bagZipFileIsDeleted) {
                LOG.info("Bagit files are deleted.");
                archived.setBagitDir("DELETED");
            } else {
                LOG.warn(bagDirToDelete.getAbsolutePath() + " is not deleted");
                LOG.warn(bagZipFileToDelete + " is not deleted");
            }
        }
        archivedDao.update(archived);
    }

    @Override
    @ApiOperation(value = "Deletes a record", nickname = "deleteById", notes = "", tags={ "Archiving", })
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
            if (dbArchived != null) {
                archivedDao.delete(dbArchived);
                return new ResponseEntity<>(HttpStatus.OK);
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default ArchiveApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Override
    @ApiOperation(value = "Deletes a record", nickname = "deleteByParams", notes = "", tags={ "Archiving", })
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
            if (dbArchived != null) {
                archivedDao.delete(dbArchived);
                return new ResponseEntity<>(HttpStatus.OK);
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default ArchiveApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Override
    @ApiOperation(value = "Updated Archive", nickname = "updateArchive", notes = "Update the existing Archive.", tags={ "Archiving", })
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

            Archived dbArchived = archivedDao.getBySrcxmlSrcversionTargetiri(archive.getSrcMetadataXml(), archive.getSrcMetadataVersion(), archive.getTargetIri());
            if (dbArchived == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);

            archivedDao.update(archive);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default ArchiveApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    private int checkCredentials(String tdrIri, String uid, String pwd, int timeout) throws URISyntaxException {
        //check TDR credentials
        //see https://stackoverflow.com/questions/21574478/what-is-the-difference-between-closeablehttpclient-and-httpclient-in-apache-http
        try(CloseableHttpClient httpClient = BridgeHelper.createHttpClient((new IRI(tdrIri).toURI()), uid, pwd, timeout)){
            HttpGet httpGet = new HttpGet(tdrIri);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            return response.getStatusLine().getStatusCode();
        } catch (IOException e) {
            return org.apache.http.HttpStatus.SC_REQUEST_TIMEOUT;
        }
    }

    private Archived createNewArchived(IngestData ingestData, TdrConf tdrConf) {
        Archived archived = new Archived();
        archived.setStartTime(new Date());
        archived.setSrcMetadataXml(ingestData.getSrcData().getSrcXml());
        archived.setSrcMetadataVersion(ingestData.getSrcData().getSrcVersion());
        archived.setTargetIri(tdrConf.getIri());
        archived.setTdrName(tdrConf.getTdrName());
        archived.setState(StateEnum.IN_PROGRESS.toString());
        archivedDao.create(archived);
        return archived;
    }

    private enum CredentialsType {
        UP("username-password"),
        AT("api-token");

        private String value;

        CredentialsType(String value) {
            this.value = value;
        }

        public String toString() {
            return String.valueOf(value);
        }

        public static CredentialsType fromValue(String text) {
            for (CredentialsType b : CredentialsType.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }
}

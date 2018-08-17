/**
 * NOTE: This class is auto generated by the swagger code generator program (2.3.1).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package nl.knaw.dans.dataverse.bridge.generated.api;

import nl.knaw.dans.dataverse.bridge.generated.model.Error;
import nl.knaw.dans.dataverse.bridge.generated.model.IngestData;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-08-17T12:02:34.262+02:00")

@Api(value = "archive", description = "the archive API")
public interface ArchiveApi {

    Logger log = LoggerFactory.getLogger(ArchiveApi.class);

    default Optional<ObjectMapper> getObjectMapper() {
        return Optional.empty();
    }

    default Optional<HttpServletRequest> getRequest() {
        return Optional.empty();
    }

    default Optional<String> getAcceptHeader() {
        return getRequest().map(r -> r.getHeader("Accept"));
    }

    @ApiOperation(value = "Operation to create a new Archive", nickname = "createArchive", notes = "Add a new archive to TDR", response = nl.knaw.dans.dataverse.bridge.core.db.domain.Archived.class, tags={ "archiving", })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "Dataset succesfully created.", response = nl.knaw.dans.dataverse.bridge.core.db.domain.Archived.class),
        @ApiResponse(code = 400, message = "Dataset couldn't have been created."),
        @ApiResponse(code = 405, message = "Invalid input") })
    @RequestMapping(value = "/archive/create",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    default ResponseEntity<nl.knaw.dans.dataverse.bridge.core.db.domain.Archived> createArchive(@ApiParam(value = "Dataset object that needs to be added to the Archived's table." ,required=true )  @Valid @RequestBody IngestData ingestData) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("{  \"srcXml\" : \"srcXml\",  \"landingPage\" : \"landingPage\",  \"startTime\" : \"2000-01-23\",  \"id\" : 0,  \"endTime\" : \"2000-01-23\",  \"state\" : \"IN-PROGRESS\",  \"srcVersion\" : \"srcVersion\",  \"targetIri\" : \"targetIri\",  \"doi\" : \"doi\"}", nl.knaw.dans.dataverse.bridge.core.db.domain.Archived.class), HttpStatus.NOT_IMPLEMENTED);
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


    @ApiOperation(value = "Deletes a record", nickname = "deleteById", notes = "", tags={ "archiving", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Record is deleted"),
        @ApiResponse(code = 400, message = "Invalid ID supplied"),
        @ApiResponse(code = 404, message = "Record not found") })
    @RequestMapping(value = "/archive/{id}",
        produces = { "application/xml", "application/json" }, 
        method = RequestMethod.DELETE)
    default ResponseEntity<Void> deleteById(@ApiParam(value = "" ,required=true) @RequestHeader(value="api_key", required=true) String apiKey,@ApiParam(value = "Record id to delete",required=true) @PathVariable("id") Long id) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default ArchiveApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Deletes a record", nickname = "deleteByParams", notes = "", tags={ "archiving", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Record is deleted"),
        @ApiResponse(code = 400, message = "Invalid Paramas supplied"),
        @ApiResponse(code = 404, message = "Record not found") })
    @RequestMapping(value = "/archive",
        produces = { "application/xml", "application/json" }, 
        method = RequestMethod.DELETE)
    default ResponseEntity<Void> deleteByParams(@ApiParam(value = "" ,required=true) @RequestHeader(value="api_key", required=true) String apiKey,@NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "srcXml", required = true) String srcXml,@NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "srcVersion", required = true) String srcVersion,@NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "targetIri", required = true) String targetIri) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default ArchiveApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "peration to retrive all Archived datasets", nickname = "getAll", notes = "Operation to retrive all Archived datasets", response = nl.knaw.dans.dataverse.bridge.core.db.domain.Archived.class, responseContainer = "List", tags={ "archiving", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Plugin response", response = nl.knaw.dans.dataverse.bridge.core.db.domain.Archived.class, responseContainer = "List"),
        @ApiResponse(code = 200, message = "unexpected error", response = Error.class) })
    @RequestMapping(value = "/archive/get-all",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<List<nl.knaw.dans.dataverse.bridge.core.db.domain.Archived>> getAll() {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("[ {  \"srcXml\" : \"srcXml\",  \"landingPage\" : \"landingPage\",  \"startTime\" : \"2000-01-23\",  \"id\" : 0,  \"endTime\" : \"2000-01-23\",  \"state\" : \"IN-PROGRESS\",  \"srcVersion\" : \"srcVersion\",  \"targetIri\" : \"targetIri\",  \"doi\" : \"doi\"}, {  \"srcXml\" : \"srcXml\",  \"landingPage\" : \"landingPage\",  \"startTime\" : \"2000-01-23\",  \"id\" : 0,  \"endTime\" : \"2000-01-23\",  \"state\" : \"IN-PROGRESS\",  \"srcVersion\" : \"srcVersion\",  \"targetIri\" : \"targetIri\",  \"doi\" : \"doi\"} ]", List.class), HttpStatus.NOT_IMPLEMENTED);
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


    @ApiOperation(value = "Operation to retrive a state of an Archived dataset", nickname = "getState", notes = "Operation to retrive a state of an Archived dataset by filtering pid, version, tdr target.", response = nl.knaw.dans.dataverse.bridge.core.db.domain.Archived.class, tags={ "archiving", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Plugin response", response = nl.knaw.dans.dataverse.bridge.core.db.domain.Archived.class),
        @ApiResponse(code = 200, message = "unexpected error", response = Error.class) })
    @RequestMapping(value = "/archive/state",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<nl.knaw.dans.dataverse.bridge.core.db.domain.Archived> getState(@NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "srcXml", required = true) String srcXml,@NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "srcVersion", required = true) String srcVersion,@NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "targetIri", required = true) String targetIri) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("{  \"srcXml\" : \"srcXml\",  \"landingPage\" : \"landingPage\",  \"startTime\" : \"2000-01-23\",  \"id\" : 0,  \"endTime\" : \"2000-01-23\",  \"state\" : \"IN-PROGRESS\",  \"srcVersion\" : \"srcVersion\",  \"targetIri\" : \"targetIri\",  \"doi\" : \"doi\"}", nl.knaw.dans.dataverse.bridge.core.db.domain.Archived.class), HttpStatus.NOT_IMPLEMENTED);
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


    @ApiOperation(value = "Updated Archive", nickname = "updateArchive", notes = "Update the existing Archive.", tags={ "archiving", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Record is updated"),
        @ApiResponse(code = 400, message = "Invalid id supplied"),
        @ApiResponse(code = 404, message = "Archived not found") })
    @RequestMapping(value = "/archive",
        produces = { "application/json", "application/xml" }, 
        method = RequestMethod.PUT)
    default ResponseEntity<Void> updateArchive(@ApiParam(value = "" ,required=true) @RequestHeader(value="api_key", required=true) String apiKey,@ApiParam(value = "Updated archive object" ,required=true )  @Valid @RequestBody nl.knaw.dans.dataverse.bridge.core.db.domain.Archived archive) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default ArchiveApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}

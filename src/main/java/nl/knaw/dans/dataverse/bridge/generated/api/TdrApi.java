/**
 * NOTE: This class is auto generated by the swagger code generator program (2.3.1).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package nl.knaw.dans.dataverse.bridge.generated.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import nl.knaw.dans.dataverse.bridge.generated.model.Error;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-08-24T14:46:26.508+02:00")

@Api(value = "tdr", description = "the tdr API")
public interface TdrApi {

    Logger log = LoggerFactory.getLogger(TdrApi.class);

    default Optional<ObjectMapper> getObjectMapper() {
        return Optional.empty();
    }

    default Optional<HttpServletRequest> getRequest() {
        return Optional.empty();
    }

    default Optional<String> getAcceptHeader() {
        return getRequest().map(r -> r.getHeader("Accept"));
    }

    @ApiOperation(value = "Operation to retrive all TDR Configuration", nickname = "getAllTdrConf", notes = "Operation to retrive all TDR Configuration", response = nl.knaw.dans.dataverse.bridge.core.common.TdrConf.class, responseContainer = "List", tags={ "TDR Configuration", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Plugin response", response = nl.knaw.dans.dataverse.bridge.core.common.TdrConf.class, responseContainer = "List"),
        @ApiResponse(code = 200, message = "unexpected error", response = Error.class) })
    @RequestMapping(value = "/tdr/get-all",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<List<nl.knaw.dans.dataverse.bridge.core.common.TdrConf>> getAllTdrConf() {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("[ {  \"xsl\" : [ {    \"xslName\" : \"xslName\",    \"location\" : \"location\"  }, {    \"xslName\" : \"xslName\",    \"location\" : \"location\"  } ],  \"iri\" : \"iri\",  \"actionClassName\" : \"actionClassName\",  \"tdrName\" : \"tdrName\"}, {  \"xsl\" : [ {    \"xslName\" : \"xslName\",    \"location\" : \"location\"  }, {    \"xslName\" : \"xslName\",    \"location\" : \"location\"  } ],  \"iri\" : \"iri\",  \"actionClassName\" : \"actionClassName\",  \"tdrName\" : \"tdrName\"} ]", List.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default TdrApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Operation to retrive a TDR Configuration", nickname = "getTdrConfByName", notes = "Operation to retrive all TDR Configuration", response = nl.knaw.dans.dataverse.bridge.core.common.TdrConf.class, tags={ "TDR Configuration", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Plugin response", response = nl.knaw.dans.dataverse.bridge.core.common.TdrConf.class),
        @ApiResponse(code = 200, message = "unexpected error", response = Error.class) })
    @RequestMapping(value = "/tdr/get/{name}",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<nl.knaw.dans.dataverse.bridge.core.common.TdrConf> getTdrConfByName(@ApiParam(value = "",required=true) @PathVariable("name") String name) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("{  \"xsl\" : [ {    \"xslName\" : \"xslName\",    \"location\" : \"location\"  }, {    \"xslName\" : \"xslName\",    \"location\" : \"location\"  } ],  \"iri\" : \"iri\",  \"actionClassName\" : \"actionClassName\",  \"tdrName\" : \"tdrName\"}", nl.knaw.dans.dataverse.bridge.core.common.TdrConf.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default TdrApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}

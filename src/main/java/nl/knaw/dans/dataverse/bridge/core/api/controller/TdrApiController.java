package nl.knaw.dans.dataverse.bridge.core.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import nl.knaw.dans.dataverse.bridge.core.api.config.BridgeConfEnvironment;
import nl.knaw.dans.dataverse.bridge.core.common.TdrConf;
import nl.knaw.dans.dataverse.bridge.generated.api.TdrApi;
import nl.knaw.dans.dataverse.bridge.generated.model.Error;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
    @author Eko Indarto
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-08-23T16:39:10.920+02:00")

@Controller
public class TdrApiController implements TdrApi {

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    BridgeConfEnvironment bcenv;

    @org.springframework.beans.factory.annotation.Autowired
    public TdrApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    private static List<TdrConf> tdrConfList = new ArrayList<TdrConf>();

    @Override
    public Optional<ObjectMapper> getObjectMapper() {
        return Optional.ofNullable(objectMapper);
    }

    @Override
    public Optional<HttpServletRequest> getRequest() {
        return Optional.ofNullable(request);
    }



    @Override
    @ApiOperation(value = "Operation to retrive all TDR Configuration", nickname = "getAllTdrConf", notes = "Operation to retrive all TDR Configuration", response = nl.knaw.dans.dataverse.bridge.core.common.TdrConf.class, responseContainer = "List", tags={ "TDR Configuration", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Plugin response", response = nl.knaw.dans.dataverse.bridge.core.common.TdrConf.class, responseContainer = "List"),
            @ApiResponse(code = 200, message = "unexpected error", response = Error.class) })
    @RequestMapping(value = "/tdr/get-all",
            produces = { "application/json" },
            method = RequestMethod.GET)
    public ResponseEntity<List<nl.knaw.dans.dataverse.bridge.core.common.TdrConf>> getAllTdrConf() {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("[ {  \"credentialType\" : \"credentialType\",  \"xsl\" : [ {    \"xslName\" : \"xslName\",    \"location\" : \"location\"  }, {    \"xslName\" : \"xslName\",    \"location\" : \"location\"  } ],  \"iri\" : \"iri\",  \"actionClassName\" : \"actionClassName\",  \"tdrName\" : \"tdrName\"}, {  \"credentialType\" : \"credentialType\",  \"xsl\" : [ {    \"xslName\" : \"xslName\",    \"location\" : \"location\"  }, {    \"xslName\" : \"xslName\",    \"location\" : \"location\"  } ],  \"iri\" : \"iri\",  \"actionClassName\" : \"actionClassName\",  \"tdrName\" : \"tdrName\"} ]", List.class), HttpStatus.NOT_IMPLEMENTED);
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

    @Override
    @ApiOperation(value = "Operation to retrive a TDR Configuration", nickname = "getTdrConfByName", notes = "Operation to retrive all TDR Configuration", response = nl.knaw.dans.dataverse.bridge.core.common.TdrConf.class, tags={ "TDR Configuration", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Plugin response", response = nl.knaw.dans.dataverse.bridge.core.common.TdrConf.class),
            @ApiResponse(code = 200, message = "unexpected error", response = Error.class) })
    @RequestMapping(value = "/tdr/get/{name}",
            produces = { "application/json" },
            method = RequestMethod.GET)
    public ResponseEntity<nl.knaw.dans.dataverse.bridge.core.common.TdrConf> getTdrConfByName(@ApiParam(value = "",required=true) @PathVariable("name") String name) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("{  \"credentialType\" : \"credentialType\",  \"xsl\" : [ {    \"xslName\" : \"xslName\",    \"location\" : \"location\"  }, {    \"xslName\" : \"xslName\",    \"location\" : \"location\"  } ],  \"iri\" : \"iri\",  \"actionClassName\" : \"actionClassName\",  \"tdrName\" : \"tdrName\"}", nl.knaw.dans.dataverse.bridge.core.common.TdrConf.class), HttpStatus.NOT_IMPLEMENTED);
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

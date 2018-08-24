package nl.knaw.dans.dataverse.bridge.core.api.config;

import nl.knaw.dans.dataverse.bridge.core.common.TdrConf;
import nl.knaw.dans.dataverse.bridge.core.common.XsltSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/*
    @author Eko Indarto
    This class is needed since the Spring's autowiring happens too late.
 */
@Configuration
public class BridgeConfEnvironment implements EnvironmentAware {
    private static final Logger LOG = LoggerFactory.getLogger(BridgeConfEnvironment.class);
    private static Environment env;
    private static List<TdrConf> tdrConfList = new ArrayList<TdrConf>();

//    public static String getProperty(String key) {
//        return env.getProperty(key);
//    }

    @Override
    public void setEnvironment(Environment env) {
        BridgeConfEnvironment.env = env;
        LOG.info("Registering TDRs Configuration:");
        initConf();
        tdrConfList.forEach(i -> {LOG.info("tdr-name: " + i.getTdrName());
            LOG.info("iri: " + i.getIri());
            LOG.info("xsl: " + i.getXsl());
            LOG.info("action-class-name: " + i.getActionClassName());
        });
    }

    public static List<TdrConf> getTdrConfList() {
        return tdrConfList;
    }

    private void initConf(){

        JsonReader reader = Json.createReader(new StringReader(env.getProperty("bridge.tdr.conf")));
        JsonArray tcJsonArray = reader.readArray();
        reader.close();
        tdrConfList= tcJsonArray.stream().map(JsonObject.class::cast)
                .map(i -> new TdrConf(((JsonObject) i).getString("tdr-name")
                        , ((JsonObject) i).getString("iri")
                        , ((JsonObject) i).getString("action-class-name")
                        , (((JsonObject) i).getJsonArray("xsl")).stream()
                        .map(JsonObject.class::cast).map(j ->
                                new XsltSource(j.getString("xsl-name"), j.getString("xsl-url")))
                        .collect(Collectors.toList())
                )).collect(Collectors.toList());
    }

}
package nl.knaw.dans.dataverse.bridge.core.common;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.util.Map;
import java.util.stream.Collectors;

/*
 * @author: Eko Indarto
 */
public class TdrCfg{
    private String credentialType;
    private String iri;
    private Map<String, String> xsl;
    private String actionClassName;

    public TdrCfg(String credentialType, String iri, JsonArray xslJsonArray, String actionClassName) {
        this.credentialType = credentialType;
        this.actionClassName = actionClassName;
        this.iri = iri;
        this.xsl = xslJsonArray.stream().map(JsonObject.class::cast).collect(Collectors.toMap(joName->joName.getString("name"),joFileName->joFileName.getString("url")));
    }

    public String getCredentialType() {
        return credentialType;
    }

    public String getIri() {
        return iri;
    }

    public Map<String, String> getXsl() {
        return xsl;
    }

    public String getActionClassName() {
        return actionClassName;
    }
}

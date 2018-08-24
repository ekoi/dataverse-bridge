package nl.knaw.dans.dataverse.bridge.core.common;

import java.util.Map;
/**
 * Class DvFileList
 * Created by Eko Indarto.
 */
public class DvFileList {
    private String apiToken;
    private Map<String, String> restrictedFiles;
    private Map<String, String> publicFiles;

    public DvFileList(String apiToken, Map<String, String> restrictedFiles, Map<String, String> publicFiles) {
        this.apiToken = apiToken;
        this.restrictedFiles = restrictedFiles;
        this.publicFiles = publicFiles;
    }

    public String getApiToken() {
        return apiToken;
    }

    public Map<String, String> getRestrictedFiles() {
        return restrictedFiles;
    }

    public Map<String, String> getPublicFiles() {
        return publicFiles;
    }
}

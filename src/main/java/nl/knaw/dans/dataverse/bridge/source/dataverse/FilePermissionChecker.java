package nl.knaw.dans.dataverse.bridge.source.dataverse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FilePermissionChecker {
    private static final Logger LOG = LoggerFactory.getLogger(FilePermissionChecker.class);
    public static FilePermissionStatus check(String url) {
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
}

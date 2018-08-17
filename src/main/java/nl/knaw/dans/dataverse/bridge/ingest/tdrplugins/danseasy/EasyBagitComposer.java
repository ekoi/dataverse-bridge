package nl.knaw.dans.dataverse.bridge.ingest.tdrplugins.danseasy;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class EasyBagitComposer {
    private static final Logger LOG = LoggerFactory.getLogger(EasyBagitComposer.class);
    private Path bagitDir;
    private Path metadataDir;
    private Path bagTempDir;

    public EasyBagitComposer(String bagitBaseDir) {
        bagTempDir = createTempDirectory(bagitBaseDir);
    }

    public boolean buildEasyXml(String datasetXml, String filesXml) {
        LOG.info("bagitDir: " + bagitDir);
        LOG.info("bagitDir absoluth path " + bagitDir.toAbsolutePath());
        metadataDir = Paths.get(bagitDir + "/metadata");
        try {
            Files.createDirectories(metadataDir);
            createDatasetXmlFile(datasetXml);
            createFilesXmlFile(filesXml);
            return true;
        } catch (IOException e) {
            LOG.error("ERROR: buildEasyBagit - IOException, caused by: " + e.getMessage());
        }
        return false;
    }

    public boolean createDdiAndJsonXml(String ddiEportUrl){
        try {
            FileUtils.copyURLToFile(new URL(ddiEportUrl), new File(bagTempDir + "/data/" +getExportedDvFilename(ddiEportUrl,"xml")));
            //json: http://ddvn.dans.knaw.nl:8080/api/datasets/:persistentId/?persistentId=hdl:12345/JLO8HN
            FileUtils.copyURLToFile(new URL(ddiEportUrl.replace("export?exporter=ddi&", ":persistentId/?"))
                    ,  new File(bagTempDir + "/data/" +getExportedDvFilename(ddiEportUrl, "json")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean downloadFiles(Map<String, String> restrictedFiles, String apiToken, Map<String, String> publicFiles) throws IOException {
        try {
            publicFiles.forEach((k, v) -> {
                LOG.info("Start Download file: " + v + " to file: " + k);
                try {
                    FileUtils.copyURLToFile(new URL(v),  new File(bagTempDir + "/data/" + k));
                } catch (IOException e) {
                    throwWrapped(e);
                }
                LOG.info("Download file: " + k + " is finish");
            });

            restrictedFiles.forEach((k, v) -> {
                LOG.info("Start Download file: " + v + " to file: " + k);
                try {
                    FileUtils.copyURLToFile(new URL(v + "?key=" + apiToken), new File(bagTempDir + "/data/" + k));
                } catch (IOException e) {
                    throwWrapped(e);
                }
                LOG.info("Download file: " + k + " is finish");
            });

        }
        catch (WrappedException w) {
            throw (IOException) w.cause;
        }

        return false;
    }

    static WrappedException throwWrapped(Throwable t) {
        throw new WrappedException(t);
    }

    private String getExportedDvFilename(String ddiEportUrl, String ext) {
        return (ddiEportUrl.split("persistentId=")[1])
                .replace(":","-")
                .replace("/","-") + "." + ext;
    }
    private void createDatasetXmlFile(String datasetXml) throws IOException {
        File datasetXmlFile = new File(metadataDir + "/dataset.xml");
        datasetXmlFile.createNewFile();
        Files.write(datasetXmlFile.toPath(), datasetXml.getBytes());
    }

    private void createFilesXmlFile(String filesXml) throws IOException {
        File filesXmlFile = new File(metadataDir + "/files.xml");
        Files.write(filesXmlFile.toPath(), filesXml.getBytes());
    }

    private Path createTempDirectory(String baseDir) {
        try {
            bagitDir = Files.createTempDirectory(Paths.get(baseDir), "bagit");
            return bagitDir;
        } catch (IOException e) {
            LOG.error("ERROR: transformToFilesXmlAndCopyFiles - createTempDirectory - IOException, caused by: " + e.getMessage());
        }
        return null;//TODO
    }
    //File dvnFileForIngest = new File(bagTempDir + "/data/" + title);
    private void downloadFile(String url, File dvnFileForIngest, String apiToken /*boolean restrictedFile*/) throws IOException {
        //if (restrictedFile) {
        if (apiToken != null) {
            LOG.info("Start Download file: " + url + " to file: " + dvnFileForIngest);
            FileUtils.copyURLToFile(new URL(url + "?key=" + apiToken), dvnFileForIngest);
            LOG.info("Download file: " + dvnFileForIngest + " is finish");
        } else {
            LOG.info("Start Download file: " + url + " to file: " + dvnFileForIngest);
            FileUtils.copyURLToFile(new URL(url), dvnFileForIngest);
            LOG.info("Download file: " + dvnFileForIngest + " is finish");
        }
    }

    public Path getBagTempDir() {
        return bagTempDir;
    }
}

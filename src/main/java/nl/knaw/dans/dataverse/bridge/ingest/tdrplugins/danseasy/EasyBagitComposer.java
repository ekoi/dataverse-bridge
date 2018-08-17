package nl.knaw.dans.dataverse.bridge.ingest.tdrplugins.danseasy;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.PreBag;
import gov.loc.repository.bagit.transformer.impl.ChainingCompleter;
import gov.loc.repository.bagit.transformer.impl.DefaultCompleter;
import gov.loc.repository.bagit.transformer.impl.TagManifestCompleter;
import nl.knaw.dans.dataverse.bridge.core.bagit.BagInfoCompleter;
import nl.knaw.dans.dataverse.bridge.core.util.BridgeHelper;
import nl.knaw.dans.dataverse.bridge.exception.BridgeException;
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

    public EasyBagitComposer(String bagitBaseDir) throws BridgeException {
        bagTempDir = createTempDirectory(bagitBaseDir);
    }

    public void buildEasyBag(String datasetXml, String filesXml) throws IOException {
        LOG.info("bagitDir: " + bagitDir);
        LOG.info("bagitDir absoluth path " + bagitDir.toAbsolutePath());
        metadataDir = Paths.get(bagitDir + "/metadata");
        Files.createDirectories(metadataDir);
        createDatasetXmlFile(datasetXml);
        createFilesXmlFile(filesXml);
    }

    public void createDdiAndJsonXml(String ddiEportUrl) throws IOException {
            FileUtils.copyURLToFile(new URL(ddiEportUrl), new File(bagTempDir + "/data/" +getExportedDvFilename(ddiEportUrl,"xml")));
            //json: http://ddvn.dans.knaw.nl:8080/api/datasets/:persistentId/?persistentId=hdl:12345/JLO8HN
            FileUtils.copyURLToFile(new URL(ddiEportUrl.replace("export?exporter=ddi&", ":persistentId/?"))
                    ,  new File(bagTempDir + "/data/" +getExportedDvFilename(ddiEportUrl, "json")));
    }
    /*
    How can I throw CHECKED exceptions from inside Java 8 streams?
    Oracle messed it up.
    They cling on the concept of checked exceptions, but inconsistently forgot to take care of checked exceptions when designing the functional interfaces, streams, lambda etc.
    see: https://stackoverflow.com/questions/27644361/how-can-i-throw-checked-exceptions-from-inside-java-8-streams

     This method will throw runtime exception.
     */

    /*
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
        catch (BridgeException w) {
            throw (IOException) w.cause;
        }

        return false;
    }*/



    public void downloadFiles(Map<String, String> restrictedFiles, String apiToken, Map<String, String> publicFiles) throws BridgeException {

        for (Map.Entry<String, String> publicFile : publicFiles.entrySet()){
            try {
                FileUtils.copyURLToFile(new URL(publicFile.getValue()),  new File(bagTempDir + "/data/" + publicFile.getKey()));
            } catch (IOException e) {
                throw new BridgeException("[EasyBagitComposer - downloadFiles] Public File. URL: " + publicFile.getValue()
                        + " File name: " + publicFile.getKey() + "; errror msg: " + e.getMessage(), e, "IOException");
            }
        }
        for (Map.Entry<String, String> restrictedFile : restrictedFiles.entrySet()){
            try {
                FileUtils.copyURLToFile(new URL(restrictedFile.getValue() + "?key=" + apiToken), new File(bagTempDir + "/data/" + restrictedFile.getKey()));
            } catch (IOException e) {
                throw new BridgeException("[EasyBagitComposer - downloadFiles] - Restricted File. URL: " + restrictedFile.getValue()
                        + " File name: " + restrictedFile.getKey() + "; errror msg: " + e.getMessage(), e, "IOException");
            }
        }
    }

    public void composeBagit() {
        BagFactory bf = new BagFactory();
        BagInfoCompleter bic = new BagInfoCompleter(bf);
        DefaultCompleter dc = new DefaultCompleter(bf);
        dc.setPayloadManifestAlgorithm(Manifest.Algorithm.SHA1);
        TagManifestCompleter tmc = new TagManifestCompleter(bf);
        tmc.setTagManifestAlgorithm(Manifest.Algorithm.SHA1);
        ChainingCompleter completer = new ChainingCompleter(dc, new BagInfoCompleter(bf), tmc);
        PreBag pb = bf.createPreBag(bagTempDir.toFile());
        pb.makeBagInPlace(BagFactory.Version.V0_97, false, completer);
        Bag b = bf.createBag(bagTempDir.toFile());
    }

    public File createBagitZip() throws Exception {
        File zipFile = new File(bagTempDir.toFile().getAbsolutePath() + ".zip");
        BridgeHelper.zipDirectory(bagTempDir.toFile(), zipFile);
        return zipFile;
    }

//    static BridgeException throwBridge(Throwable t) {
//        throw new BridgeException(t);
//    }

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

    private Path createTempDirectory(String baseDir) throws BridgeException {
        try {
            bagitDir = Files.createTempDirectory(Paths.get(baseDir), "bagit");
            return bagitDir;
        } catch (IOException e) {
            LOG.error("ERROR: transformToFilesXmlAndCopyFiles - createTempDirectory - IOException, caused by: " + e.getMessage());
            throw new BridgeException(e.getMessage(), e, "IOException");
        }
    }

    public Path getBagTempDir() {
        return bagTempDir;
    }
}

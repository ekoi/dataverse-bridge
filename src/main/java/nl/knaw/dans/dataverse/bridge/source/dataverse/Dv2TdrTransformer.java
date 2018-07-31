package nl.knaw.dans.dataverse.bridge.source.dataverse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by akmi on 26/04/17.
 */
public class Dv2TdrTransformer {
    private static final Logger LOG = LoggerFactory.getLogger(Dv2TdrTransformer.class);

    private static String DDI_EXPORT_URL;
    private Templates cachedXSLTDataset;
    private Templates cachedXSLTFiles;
    private String datasetXml;
    private String filesXml;
    private File datasetXmlFile;
    private File filesXmlFile;
    private Document ddiDocument;
    private Path bagitDir;
    private Path metadataDir;

    public Dv2TdrTransformer(String ddiEportUrl, Source s1, Source s2) {
        this.DDI_EXPORT_URL = ddiEportUrl;
        init(s1, s2);
    }

    private void init(Source srcXsltDataset, Source srcXsltFiles) {
        TransformerFactory transFact = new net.sf.saxon.TransformerFactoryImpl();
        try {
            cachedXSLTDataset = transFact.newTemplates(srcXsltDataset);
            cachedXSLTFiles = transFact.newTemplates(srcXsltFiles);
        } catch (TransformerConfigurationException e) {
            LOG.error("ERROR: TransformerConfigurationException, caused by: " + e.getMessage());
        }
    }

    private void transformToDataset(Document doc) {
        try {
            Transformer transformer = cachedXSLTDataset.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            datasetXml = writer.toString();
        } catch (TransformerConfigurationException e) {
            LOG.error("ERROR: transformToDataset - TransformerConfigurationException, caused by: " + e.getMessage());
        } catch (TransformerException e) {
            LOG.error("ERROR: transformToDataset - TransformerException, caused by: " + e.getMessage());
        }
    }

    private void transformToFiles(Document doc) {
        try {
            Transformer transformer = cachedXSLTFiles.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            filesXml = writer.toString();
        } catch (TransformerConfigurationException e) {
            LOG.error("ERROR: transformToDataset - TransformerConfigurationException, caused by: " + e.getMessage());
        } catch (TransformerException e) {
            LOG.error("ERROR: transformToDataset - TransformerException, caused by: " + e.getMessage());
        }
    }

    public Path createTempDirectory() {
        try {
            bagitDir = Files.createTempDirectory("bagit");
            URI u = bagitDir.toUri();
            String s = bagitDir.toString();
            return bagitDir;
        } catch (IOException e) {
            LOG.error("ERROR: transformToFiles - createTempDirectory - IOException, caused by: " + e.getMessage());
        }
        return null;//TODO
    }

    public void createMetadata() {
        ddiDocument = getDocument();
        //createTempDirectories();
        transformToDataset(ddiDocument);
        transformToFiles(ddiDocument);
        LOG.info("bagitDir: " + bagitDir);
        LOG.info("bagitDir absoluth path " + bagitDir.toAbsolutePath());
        metadataDir = Paths.get(bagitDir + "/metadata");
        try {
            Files.createDirectories(metadataDir);
            createDatasetXmlFile();
            createFilesXmlFile();
        } catch (IOException e) {
            LOG.error("ERROR: createMetadata - IOException, caused by: " + e.getMessage());
        }
    }

    private void createDatasetXmlFile() {
        datasetXmlFile = new File(metadataDir + "/dataset.xml");
        try {
            datasetXmlFile.createNewFile();
            Files.write(datasetXmlFile.toPath(), getDatasetXml().getBytes());
        } catch (IOException e) {
            LOG.error("ERROR: createDatasetXmlFile - IOException, caused by: " + e.getMessage());
        }
    }

    private void createFilesXmlFile() {
        filesXmlFile = new File(metadataDir + "/files.xml");
        try {
            filesXmlFile.createNewFile();
            Files.write(filesXmlFile.toPath(), getFilesXml().getBytes());
        } catch (IOException e) {
            LOG.error("ERROR: createFilesXmlFile - IOException, caused by: " + e.getMessage());
        }
    }

    public Document getDocument() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder;
        Document doc = null;
        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse(DDI_EXPORT_URL);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            LOG.error("ERROR: getDocument - ParserConfigurationException | SAXException | IOException, caused by: " + e.getMessage());
        }
        return doc;
    }

    public String getDatasetXml() {
        return datasetXml;
    }

    public String getFilesXml() {
        System.out.println(filesXml);
        return filesXml;
    }

}

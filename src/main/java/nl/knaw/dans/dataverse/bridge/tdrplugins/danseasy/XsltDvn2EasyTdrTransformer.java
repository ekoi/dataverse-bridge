package nl.knaw.dans.dataverse.bridge.tdrplugins.danseasy;

import nl.knaw.dans.dataverse.bridge.tdrplugins.XsltDvn2TdrTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
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
public class XsltDvn2EasyTdrTransformer extends XsltDvn2TdrTransformer {
    private static final Logger LOG = LoggerFactory.getLogger(XsltDvn2EasyTdrTransformer.class);

    private Templates cachedXSLTDataset;
    private Templates cachedXSLTFiles;
    private File datasetXmlFile;
    private File filesXmlFile;
    private Document doc;


    public XsltDvn2EasyTdrTransformer(String ddiEportUrl, String xslBaseUrl) {
        super(ddiEportUrl, xslBaseUrl);
        init();
    }

    private void init() {
        TransformerFactory transFact = new net.sf.saxon.TransformerFactoryImpl();
        try {
            Source srcXsltDataset = new StreamSource(new ClassPathResource(XSL_BASE_URL+ "/dvn-ddi2ddm-dataset.xsl").getInputStream());
            Source srcXsltFiles = new StreamSource(new ClassPathResource(XSL_BASE_URL + "/dvn-ddi2ddm-files.xsl").getInputStream());
            LOG.info("srcXsltDataset: " + srcXsltDataset);
            LOG.info("srcXsltFiles: " + srcXsltFiles);
            cachedXSLTDataset = transFact.newTemplates(srcXsltDataset);
            cachedXSLTFiles = transFact.newTemplates(srcXsltFiles);
        } catch (TransformerConfigurationException e) {
            LOG.error("ERROR: TransformerConfigurationException, caused by: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void transformToDataset(Document doc) {
        try {
            Transformer transformer = cachedXSLTDataset.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            setDatasetXml(writer.toString());
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    private void transformToFiles(Document doc) {
        try {
            Transformer transformer = cachedXSLTFiles.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            setFilesXml(writer.toString());
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    public void createMetadata() {
        doc = getDocument();
        //createTempDirectories();
        transformToDataset(doc);
        transformToFiles(doc);
        LOG.info("bagitDir: " + bagitDir);
        LOG.info("bagitDir absoluth path " + bagitDir.toAbsolutePath());
        metadataDir = Paths.get(bagitDir + "/metadata");
        try {
            Files.createDirectories(metadataDir);
            createDatasetXmlFile();
            createFilesXmlFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    protected void createDatasetXmlFile() {
        datasetXmlFile = new File(metadataDir + "/dataset.xml");
        try {
            datasetXmlFile.createNewFile();
            boolean b = datasetXmlFile.exists();
            Path pp = Files.write(datasetXmlFile.toPath(), getDatasetXml().getBytes());
            File f = pp.toFile();
            boolean c = f.isFile();
            boolean d = f.exists();
            System.out.println(pp.toUri());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void createFilesXmlFile() {
        filesXmlFile = new File(metadataDir + "/files.xml");
        try {
            filesXmlFile.createNewFile();
            Files.write(filesXmlFile.toPath(), getFilesXml().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

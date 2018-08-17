package nl.knaw.dans.dataverse.bridge.ingest.tdrplugins.danseasy;

import nl.knaw.dans.dataverse.bridge.core.util.FilePermissionChecker;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Created by akmi on 26/04/17.
 */
public class Dv2EasyTransformer {
    private static final Logger LOG = LoggerFactory.getLogger(Dv2EasyTransformer.class);

    private static String DDI_EXPORT_URL;
    private String apiToken;
    private Templates cachedXSLTDataset;
    private Templates cachedXSLTFiles;
    private String datasetXml;
    private String filesXml;
    private File datasetXmlFile;
    private File filesXmlFile;
    private Document ddiDocument;
    private Path bagitDir;
    private Path metadataDir;
    private Path bagTempDir;


    public Dv2EasyTransformer(String ddiEportUrl, String apiToken, Source srcXsltDataset, Source srcXsltFiles) {
        this.DDI_EXPORT_URL = ddiEportUrl;
        this.apiToken = apiToken;
        bagTempDir = createTempDirectory();
        init(srcXsltDataset, srcXsltFiles);
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

    private void transformToFilesXmlAndCopyFiles(Document doc) {

        LOG.info("Temporary bag directory: " + bagTempDir);
        XPath xPath = XPathFactory.newInstance().newXPath();
        try {
            NodeList otherMatElementList = (NodeList) xPath.evaluate("//*[local-name()='otherMat']", doc, XPathConstants.NODESET);
            for(int i = 0; i < otherMatElementList.getLength(); i++) {
                Node otherMatElement = otherMatElementList.item(i);
                if (otherMatElement != null) {
                    Node lablElement = (Node) xPath.evaluate("./*[local-name()='labl']", otherMatElement, XPathConstants.NODE);
                    String title = lablElement.getTextContent();
                    Node uriNode = otherMatElement.getAttributes().getNamedItem("URI");
                    if (uriNode != null) {
                        String url = otherMatElement.getAttributes().getNamedItem("URI").getNodeValue();
                        if (url != null) {
                            File dvnFileForIngest = new File(bagTempDir + "/data/" + title);
                            url = url.replace("https://ddvn.dans.knaw.nl", "http://ddvn.dans.knaw.nl");//https is hardcoded in SystemConfig - getDataverseSiteUrl()
                            boolean restrictedFile = (FilePermissionChecker.check(url) == FilePermissionChecker.PermissionStatus.RESTRICTED);
                            if (restrictedFile) {
                                Node restrictedNode = ddiDocument.createElement("restricted");
                                restrictedNode.setNodeValue("true");
                                Text nodeVal = doc.createTextNode("true");
                                restrictedNode.appendChild(nodeVal);
                                otherMatElement.appendChild(restrictedNode);
                            }
                            downloadFile(url, dvnFileForIngest, restrictedFile);
                        }
                    }
                }
            }
            Transformer transformer = cachedXSLTFiles.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            filesXml = writer.toString();
            LOG.debug("filesXml: " + filesXml);
        } catch (XPathExpressionException e) {
            LOG.error("XPathExpressionException, causes by: " + e.getMessage());
        } catch (MalformedURLException e) {
            LOG.error("MalformedURLException, causes by: " + e.getMessage());
        } catch (IOException e) {
            LOG.error("IOException, causes by: " + e.getMessage());
        } catch (TransformerConfigurationException e) {
            LOG.error("ERROR: transformToDataset - TransformerConfigurationException, caused by: " + e.getMessage());
        } catch (TransformerException e) {
            LOG.error("ERROR: transformToDataset - TransformerException, caused by: " + e.getMessage());
        }
    }

    private void downloadFile(String url, File dvnFileForIngest, boolean restrictedFile) throws IOException {
        if (restrictedFile) {
            LOG.info("Start Download file: " + url + " to file: " + dvnFileForIngest);
            FileUtils.copyURLToFile(new URL(url + "?key=" + apiToken), dvnFileForIngest);
            LOG.info("Download file: " + dvnFileForIngest + " is finish");
        } else {
            LOG.info("Start Download file: " + url + " to file: " + dvnFileForIngest);
            FileUtils.copyURLToFile(new URL(url), dvnFileForIngest);
            LOG.info("Download file: " + dvnFileForIngest + " is finish");
        }
    }

    public Path createTempDirectory() {
        try {
            bagitDir = Files.createTempDirectory(Paths.get("/Users/akmi/TEMP-WORKS/dataverse-generated/bagit-temp/bags"), "bagit");
            return bagitDir;
        } catch (IOException e) {
            LOG.error("ERROR: transformToFilesXmlAndCopyFiles - createTempDirectory - IOException, caused by: " + e.getMessage());
        }
        return null;//TODO
    }

    public boolean createMetadata() {
        ddiDocument = getDocument();
        if (ddiDocument == null)
            return false;

        transformToDataset(ddiDocument);
        transformToFilesXmlAndCopyFiles(ddiDocument);
        LOG.info("bagitDir: " + bagitDir);
        LOG.info("bagitDir absoluth path " + bagitDir.toAbsolutePath());
        metadataDir = Paths.get(bagitDir + "/metadata");
        try {
            Files.createDirectories(metadataDir);
            createDatasetXmlFile();
            createFilesXmlFile();
            FileUtils.copyURLToFile(new URL(DDI_EXPORT_URL), new File(bagTempDir + "/data/" +getExportedDvFilename("xml")));
            //json: http://ddvn.dans.knaw.nl:8080/api/datasets/:persistentId/?persistentId=hdl:12345/JLO8HN
            FileUtils.copyURLToFile(new URL(DDI_EXPORT_URL.replace("export?exporter=ddi&", ":persistentId/?"))
                    ,  new File(bagTempDir + "/data/" +getExportedDvFilename("json")));
            return true;
        } catch (IOException e) {
            LOG.error("ERROR: createMetadata - IOException, caused by: " + e.getMessage());
        }
        return false;
    }

    private String getExportedDvFilename(String ext) {
        return (DDI_EXPORT_URL.split("persistentId=")[1])
                .replace(":","-")
                .replace("/","-") + "." + ext;
    }

    private void createDatasetXmlFile() throws IOException {
        datasetXmlFile = new File(metadataDir + "/dataset.xml");
        datasetXmlFile.createNewFile();
        Files.write(datasetXmlFile.toPath(), getDatasetXml().getBytes());
    }

    private void createFilesXmlFile() throws IOException {
        filesXmlFile = new File(metadataDir + "/files.xml");
        Files.write(filesXmlFile.toPath(), getFilesXml().getBytes());
    }

    public Document getDocument() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder;
        Document doc = null;
        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse(DDI_EXPORT_URL);
        } catch (ParserConfigurationException e) {
            LOG.error("ERROR: getDocument - ParserConfigurationException, caused by: " + e.getMessage());
        } catch (SAXException e) {
            LOG.error("ERROR: getDocument - SAXException, caused by: " + e.getMessage());
        } catch (IOException e) {
            LOG.error("ERROR: getDocument - IOException, caused by: " + e.getMessage());
        }
        return doc;
    }

    public String getDatasetXml() {
        return datasetXml;
    }

    public String getFilesXml() {
        LOG.info(filesXml);
        return filesXml;
    }

    public Path getBagTempDir() {
        return bagTempDir;
    }
}

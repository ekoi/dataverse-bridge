package nl.knaw.dans.dataverse.bridge.ingest.tdrplugins.danseasy;

import nl.knaw.dans.dataverse.bridge.core.util.FilePermissionChecker;
import nl.knaw.dans.dataverse.bridge.exception.BridgeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
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
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by akmi on 26/04/17.
 */
public class Dv2EasyTransformer {
    private static final Logger LOG = LoggerFactory.getLogger(Dv2EasyTransformer.class);

    private static String DDI_EXPORT_URL;

    private Templates cachedXSLTDataset;
    private Templates cachedXSLTFiles;
    private String datasetXml;
    private String filesXml;
    private XPath xPath = XPathFactory.newInstance().newXPath();
    private Map<String, String> restrictedFiles = new HashMap<String, String>();
    private Map<String, String> publicFiles = new HashMap<String, String>();


    public Dv2EasyTransformer(String ddiEportUrl, String apiToken, Source srcXsltDataset, Source srcXsltFiles) throws BridgeException {
        this.DDI_EXPORT_URL = ddiEportUrl;
        init(srcXsltDataset, srcXsltFiles);
        build();
    }

    private void build() throws BridgeException {
        Document ddiDocument = getDocument();//buildDocument
        transformToDataset(ddiDocument);
        transformToFilesXml(ddiDocument);
        if (!restrictedFiles.isEmpty())
            fixedAccessRight();
    }
    private void init(Source srcXsltDataset, Source srcXsltFiles) throws BridgeException {
        TransformerFactory transFact = new net.sf.saxon.TransformerFactoryImpl();
        try {
            cachedXSLTDataset = transFact.newTemplates(srcXsltDataset);
            cachedXSLTFiles = transFact.newTemplates(srcXsltFiles);
        } catch (TransformerConfigurationException e) {
            LOG.error("ERROR: TransformerConfigurationException, caused by: " + e.getMessage());
            throw new BridgeException("init - TransformerConfigurationException, caused by: " + e.getMessage()
                    , e, this.getClass());
        }
    }

    private void transformToDataset(Document doc) throws BridgeException {
        try {
            Transformer transformer = cachedXSLTDataset.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            datasetXml = writer.toString();
            LOG.info(datasetXml);
        } catch (TransformerConfigurationException e) {
            LOG.error("ERROR: transformToDataset - TransformerConfigurationException, caused by: " + e.getMessage());
            throw new BridgeException("transformToDataset - TransformerConfigurationException, caused by: " + e.getMessage()
                    , e, this.getClass());
        } catch (TransformerException e) {
            LOG.error("ERROR: transformToDataset - TransformerException, caused by: " + e.getMessage());
            throw new BridgeException("transformToDataset - TransformerException, caused by: " + e.getMessage(), e
                    , this.getClass());
        }
    }

    private void transformToFilesXml(Document doc) throws BridgeException {
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
                            url = url.replace("https://ddvn.dans.knaw.nl", "http://ddvn.dans.knaw.nl");//https is hardcoded in SystemConfig - getDataverseSiteUrl()
                            boolean restrictedFile = (FilePermissionChecker.check(url) == FilePermissionChecker.PermissionStatus.RESTRICTED);
                            if (restrictedFile) {
                                Node restrictedNode = doc.createElement("restricted");
                                //restrictedNode.setNodeValue("true");
                                Text nodeVal = doc.createTextNode("true");
                                restrictedNode.appendChild(nodeVal);
                                otherMatElement.appendChild(restrictedNode);
                                restrictedFiles.put(title, url);
                            } else {
                                publicFiles.put(title, url);
                            }
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
            throw new BridgeException("transformToFilesXml - XPathExpressionException, caused by: " + e.getMessage(), e
                    , this.getClass());
        } catch (TransformerConfigurationException e) {
            LOG.error("ERROR: transformToDataset - TransformerConfigurationException, caused by: " + e.getMessage());
            throw new BridgeException("transformToFilesXml - TransformerException, caused by: " + e.getMessage(), e
                    , this.getClass());
        } catch (TransformerException e) {
            LOG.error("ERROR: transformToDataset - TransformerException, caused by: " + e.getMessage());
            throw new BridgeException("transformToFilesXml - TransformerException, caused by: " + e.getMessage(), e
                    , this.getClass());
        }
    }

    private Document getDocument() throws BridgeException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder;
        Document doc = null;
        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse(DDI_EXPORT_URL);
        } catch (ParserConfigurationException e) {
            LOG.error("ERROR: getDocument - ParserConfigurationException, caused by: " + e.getMessage());
            throw new BridgeException("getDocument - ParserConfigurationException, caused by: " + e.getMessage(), e
                    , this.getClass());
        } catch (SAXException e) {
            LOG.error("ERROR: getDocument - SAXException, caused by: " + e.getMessage());
            throw new BridgeException("SAXException - ParserConfigurationException, caused by: " + e.getMessage(), e
                    , this.getClass());
        } catch (IOException e) {
            LOG.error("ERROR: getDocument - IOException, caused by: " + e.getMessage());
            throw new BridgeException("getDocument - IOException, caused by: " + e.getMessage(), e
                    , this.getClass());
        }
        return doc;
    }

    public String getDatasetXml() {
        return datasetXml;
    }

    public String getFilesXml() {
        return filesXml;
    }

    public Map<String, String> getRestrictedFiles() {
        return restrictedFiles;
    }

    public Map<String, String> getPublicFiles() {
        return publicFiles;
    }

    /*This accessRight workaround: permission request is possible per file in Dataverse,
    * but this is not exported to DDI*/
    private void fixedAccessRight() throws BridgeException {
        Document datasetDoc= loadXMLFromString(datasetXml);
        try {
            Node accessRightsNode = (Node) xPath.evaluate("//*[local-name()='accessRights']", datasetDoc, XPathConstants.NODE);
            accessRightsNode.setTextContent("REQUEST_PERMISSION");
            Transformer tf = TransformerFactory.newInstance().newTransformer();
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            StringWriter writer = new StringWriter();
            tf.transform(new DOMSource(datasetDoc), new StreamResult(writer));
            datasetXml = writer.toString();
            LOG.debug(datasetXml);
        } catch (XPathExpressionException e) {
            LOG.error("XPathExpressionException, causes by: " + e.getMessage());
            throw new BridgeException("fixedAccessRight - XPathExpressionException, caused by: " + e.getMessage(), e
                    , this.getClass());
        } catch (TransformerConfigurationException e) {
            LOG.error("XPathExpressionException, causes by: " + e.getMessage());
            throw new BridgeException("fixedAccessRight - TransformerConfigurationException, caused by: " + e.getMessage(), e
                    , this.getClass());
        } catch (TransformerException e) {
            LOG.error("XPathExpressionException, causes by: " + e.getMessage());
            throw new BridgeException("fixedAccessRight - TransformerException, caused by: " + e.getMessage(), e
                    , this.getClass());
        }

    }

    private Document loadXMLFromString(String xml) throws BridgeException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xml));
            return builder.parse(is);
        } catch (ParserConfigurationException e) {
            LOG.error("ParserConfigurationException, causes by: " + e.getMessage());
            throw new BridgeException("loadXMLFromString - XPathExpressionException, caused by: " + e.getMessage(), e
                    , this.getClass());
        } catch (SAXException e) {
            LOG.error("SAXException, causes by: " + e.getMessage());
            throw new BridgeException("loadXMLFromString - XPathExpressionException, caused by: " + e.getMessage(), e
                    , this.getClass());
        } catch (IOException e) {
            LOG.error("IOException, causes by: " + e.getMessage());
            throw new BridgeException("loadXMLFromString - XPathExpressionException, caused by: " + e.getMessage(), e
                    , this.getClass());
        }
    }
}

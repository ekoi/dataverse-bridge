package nl.knaw.dans.dataverse.bridge.tdrplugins.dataverse;

import nl.knaw.dans.dataverse.bridge.exception.BridgeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;


/**
 * Created by akmi on 26/04/17.
 */
public class Dv2DvTransformer {
    private static final Logger LOG = LoggerFactory.getLogger(Dv2DvTransformer.class);

    private static String DCTERMS_EXPORT_URL;

    private Templates cachedXSLTDv2Atom;
    private Templates cachedXSLTFiles;
    private String datasetXml;
    private XPath xPath = XPathFactory.newInstance().newXPath();


    public Dv2DvTransformer(String dctermsExportUrl, Source srcXslt) throws BridgeException {
        this.DCTERMS_EXPORT_URL = dctermsExportUrl;
        init(srcXslt);
        build();
    }

    private void build() throws BridgeException {
        Document ddiDocument = getDocument();//buildDocument
        transformToDataset(ddiDocument);
    }
    private void init(Source srcXslt) throws BridgeException {
        TransformerFactory transFact = new net.sf.saxon.TransformerFactoryImpl();
        try {
            cachedXSLTDv2Atom = transFact.newTemplates(srcXslt);
        } catch (TransformerConfigurationException e) {
            LOG.error("ERROR: TransformerConfigurationException, caused by: " + e.getMessage());
            throw new BridgeException("init - TransformerConfigurationException, caused by: " + e.getMessage()
                    , e, this.getClass());
        }
    }

    private void transformToDataset(Document doc) throws BridgeException {
        try {

            Transformer transformer = cachedXSLTDv2Atom.newTransformer();
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



    private Document getDocument() throws BridgeException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder;
        Document doc = null;
        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse(DCTERMS_EXPORT_URL);
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

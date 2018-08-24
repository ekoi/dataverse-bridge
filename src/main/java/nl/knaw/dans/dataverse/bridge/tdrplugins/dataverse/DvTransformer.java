package nl.knaw.dans.dataverse.bridge.tdrplugins.dataverse;

import nl.knaw.dans.dataverse.bridge.core.common.ITransform;
import nl.knaw.dans.dataverse.bridge.core.common.XsltSource;
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
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
/*
Dv2DvTransformer dat = new Dv2DvTransformer(ingestData.getSrcData().getSrcXml()
                                                    , new StreamSource(bridgeServerBaseUrl + env.getProperty("bridge.xsl.source.dataverse")));
                                    String str = dat.getDatasetXml();
                                    byte[] bb = BridgeHelper.readChunk(new ByteArrayInputStream(str.getBytes()), str.length());
                                    IRI iri = new IRI(ingestData.getTdrData().getIri());
                                    CloseableHttpClient http = BridgeHelper.createHttpClient(iri.toURI(), ingestData.getTdrData().getUsername(), ingestData.getTdrData().getPassword(), 60000);
                                    HttpUriRequest request = RequestBuilder.create("POST").setUri(iri.toURI()).setConfig(RequestConfig.custom()

                                            .setExpectContinueEnabled(true).build())
                                                    .addHeader("Content-Type", "application/atom+xml")
                                                    .setEntity(new ByteArrayEntity(bb)) //
                                                    .build();
                                                    CloseableHttpResponse response = http.execute(request);
                                                    int mm = response.getStatusLine().getStatusCode();
                                                    LOG.debug(mm+"");
 */

public class DvTransformer implements ITransform {
    private static final Logger LOG = LoggerFactory.getLogger(DvTransformer.class);
    private Templates cachedXSLTDv2Atom;
    private String dctermsAtomXml;

    @Override
    public Map<String, String> getTransformResult(String dvDctermsMetadataUrl, String apiToken, List<XsltSource> xlsList) throws BridgeException {
//        AtomicReference<String> srcMetadataXml= new AtomicReference<>("");
//        xlsList.forEach((k, v) -> {srcMetadataXml.set(v);});
//        init(new StreamSource(srcMetadataXml.get()));
//        build(dvDctermsMetadataUrl);
        return null;
    }
    private void build(String dvDctermsMetadataUrl) throws BridgeException {
        Document dctermsDocument = getDocument(dvDctermsMetadataUrl);//buildDocument
        transformToAtomEntry(dctermsDocument);
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

    private void transformToAtomEntry(Document doc) throws BridgeException {
        try {

            Transformer transformer = cachedXSLTDv2Atom.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            dctermsAtomXml = writer.toString();
            LOG.info(dctermsAtomXml);
        } catch (TransformerConfigurationException e) {
            LOG.error("ERROR: transformToAtomEntry - TransformerConfigurationException, caused by: " + e.getMessage());
            throw new BridgeException("transformToAtomEntry - TransformerConfigurationException, caused by: " + e.getMessage()
                    , e, this.getClass());
        } catch (TransformerException e) {
            LOG.error("ERROR: transformToAtomEntry - TransformerException, caused by: " + e.getMessage());
            throw new BridgeException("transformToAtomEntry - TransformerException, caused by: " + e.getMessage(), e
                    , this.getClass());
        }
    }



    private Document getDocument(String srcMetadataUrl) throws BridgeException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder;
        Document doc = null;
        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse(srcMetadataUrl);
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

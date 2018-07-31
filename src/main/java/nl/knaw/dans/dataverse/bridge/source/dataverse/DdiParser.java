package nl.knaw.dans.dataverse.bridge.source.dataverse;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by akmi on 02/05/17.
 */
public class DdiParser {
    private final Document doc;
    private DvFile exportedDdi;
    private DvFile exportedJson;
    private static final Logger LOG = LoggerFactory.getLogger(DdiParser.class);

    public DdiParser(Document doc, DvFile exportedDdi, DvFile exportedJson) {
        this.doc = doc;
        this.exportedDdi = exportedDdi;
        this.exportedJson = exportedJson;
//        try {
//            printDocument(doc, System.out);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (TransformerException e) {
//            e.printStackTrace();
//        }
    }

    public static void printDocument(Document doc, OutputStream out) throws IOException, TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.transform(new DOMSource(doc),
                new StreamResult(new OutputStreamWriter(out, "UTF-8")));
    }
    public DvBridgeDataset parse() {
        DvBridgeDataset dvBridgeDataset = null;
        List<DvFile> dvFiles = new ArrayList<DvFile>();
        dvFiles.add(0, exportedDdi);
        dvFiles.add(1, exportedJson);
        XPathFactory xpathFactory = XPathFactory.newInstance();

        // Create XPath object
        XPath xPath = xpathFactory.newXPath();
        try {
            Node pidNode = (Node) xPath.evaluate("//*[local-name()='IDNo']", doc, XPathConstants.NODE);
            String pid = pidNode.getTextContent();
            dvBridgeDataset = new DvBridgeDataset(pid);

            Node dvNode = (Node) xPath.evaluate("//*[local-name()='version']", doc, XPathConstants.NODE);
            dvBridgeDataset.setVersion(Integer.parseInt(dvNode.getTextContent()));

            Node depDateNode = (Node) xPath.evaluate("//*[local-name()='depDate']", doc, XPathConstants.NODE);
            dvBridgeDataset.setDepositDate(DateTime.parse(depDateNode.getTextContent()));

            Node otherMatElement = (Node) xPath.evaluate("//*[local-name()='otherMat']", doc, XPathConstants.NODE);
            if (otherMatElement != null) {
                dvFiles.add(createDvnFile(xPath, otherMatElement));

                NodeList siblings = (NodeList) xPath.evaluate("following-sibling::*", otherMatElement, XPathConstants.NODESET);

                for (int i = 0; i < siblings.getLength(); ++i) {
                    Node node = siblings.item(i);
                    dvFiles.add(createDvnFile(xPath, node));
                }
            }

        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        if (dvBridgeDataset != null) {
            dvBridgeDataset.setFiles(dvFiles);
            return dvBridgeDataset;
        } else
            return null;
    }


    private DvFile createDvnFile(XPath xPath, Node otherMatElement) throws XPathExpressionException {
        DvFile dvnf = new DvFile();
        NamedNodeMap nnm = otherMatElement.getAttributes();

        dvnf.setDvFileUri(nnm.getNamedItem("URI").getNodeValue());
        dvnf.setId(Integer.parseInt(dvnf.getDvFileUri().toString().split("/api/access/datafile/")[1]));

        Node lablElement = (Node) xPath.evaluate("./*[local-name()='labl']", otherMatElement, XPathConstants.NODE);
        if (lablElement != null)
            dvnf.setTitle(lablElement.getTextContent());

//        Node txtElement = (Node) xPath.evaluate("./*[local-name()='txt']", otherMatElement, XPathConstants.NODE);
//        if (txtElement != null)
//            dvnf.setDescription(txtElement.getTextContent());


        Node notesElement = (Node) xPath.evaluate("./*[local-name()='notes']", otherMatElement, XPathConstants.NODE);
        if (notesElement != null)
            dvnf.setFormat(notesElement.getTextContent());

//        Node depDateElement = (Node) xPath.evaluate("//*[local-name()='depDate']", otherMatElement, XPathConstants.NODE);
//        if (depDateElement != null)
//            dvnf.setCreated(depDateElement.getTextContent());


        return dvnf;

    }
}

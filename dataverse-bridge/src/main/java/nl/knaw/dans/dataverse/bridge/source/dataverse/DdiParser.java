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
    private DvnFile originalDdi;
    private static final Logger LOG = LoggerFactory.getLogger(DdiParser.class);

    public DdiParser(Document doc, DvnFile originalDdi) {
        this.doc = doc;
        this.originalDdi = originalDdi;
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
    public DvnBridgeDataset parse() {
        DvnBridgeDataset dvnBridgeDataset = null;
        List<DvnFile> dvnFiles = new ArrayList<DvnFile>();
        dvnFiles.add(0, originalDdi);
        XPathFactory xpathFactory = XPathFactory.newInstance();

        // Create XPath object
        XPath xPath = xpathFactory.newXPath();
        try {
            Node pidNode = (Node) xPath.evaluate("//*[local-name()='IDNo']", doc, XPathConstants.NODE);
            String pid = pidNode.getTextContent();
            dvnBridgeDataset = new DvnBridgeDataset(pid);

            Node dvNode = (Node) xPath.evaluate("//*[local-name()='version']", doc, XPathConstants.NODE);
            dvnBridgeDataset.setVersion(Integer.parseInt(dvNode.getTextContent()));

            Node depDateNode = (Node) xPath.evaluate("//*[local-name()='depDate']", doc, XPathConstants.NODE);
            dvnBridgeDataset.setDepositDate(DateTime.parse(depDateNode.getTextContent()));

            Node otherMatElement = (Node) xPath.evaluate("//*[local-name()='otherMat']", doc, XPathConstants.NODE);
            if (otherMatElement != null) {
                dvnFiles.add(createDvnFile(xPath, otherMatElement));

                NodeList siblings = (NodeList) xPath.evaluate("following-sibling::*", otherMatElement, XPathConstants.NODESET);

                for (int i = 0; i < siblings.getLength(); ++i) {
                    Node node = siblings.item(i);
                    dvnFiles.add(createDvnFile(xPath, node));
                }
            }

        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        if (dvnBridgeDataset != null) {
            dvnBridgeDataset.setFiles(dvnFiles);
            return dvnBridgeDataset;
        } else
            return null;
    }


    private DvnFile createDvnFile(XPath xPath, Node otherMatElement) throws XPathExpressionException {
        DvnFile dvnf = new DvnFile();
        NamedNodeMap nnm = otherMatElement.getAttributes();

        dvnf.setDvnFileUri(nnm.getNamedItem("URI").getNodeValue());
        dvnf.setId(Integer.parseInt(dvnf.getDvnFileUri().toString().split("/api/access/datafile/")[1]));

        Node lablElement = (Node) xPath.evaluate("./*[local-name()='labl']", otherMatElement, XPathConstants.NODE);
        if (lablElement != null)
            dvnf.setTitle(lablElement.getTextContent());

        Node notesElement = (Node) xPath.evaluate("./*[local-name()='notes']", otherMatElement, XPathConstants.NODE);
        if (notesElement != null)
            dvnf.setFormat(notesElement.getTextContent());

        Node depDateElement = (Node) xPath.evaluate("//*[local-name()='depDate']", otherMatElement, XPathConstants.NODE);


        return dvnf;

    }
}

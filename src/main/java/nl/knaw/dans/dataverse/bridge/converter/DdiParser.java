package nl.knaw.dans.dataverse.bridge.converter;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by akmi on 02/05/17.
 */
public class DdiParser {
    private final Document doc;
    private static final Logger LOG = LoggerFactory.getLogger(DdiParser.class);

    public DdiParser(Document doc) {
        this.doc = doc;
    }

    public DvnBridgeDataset parse() {
        DvnBridgeDataset dvnBridgeDataset = null;
        List<DvnFile> dvnFiles = new ArrayList<DvnFile>();
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
            dvnFiles.add(createDatafile(xPath, otherMatElement));

            NodeList siblings = (NodeList) xPath.evaluate("following-sibling::*", otherMatElement, XPathConstants.NODESET);

            for (int i = 0; i < siblings.getLength(); ++i) {
                Node node = siblings.item(i);
                dvnFiles.add(createDatafile(xPath, node));
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


    private DvnFile createDatafile(XPath xPath, Node otherMatElement) throws XPathExpressionException {
        DvnFile dvnf = new DvnFile();
        NamedNodeMap nnm = otherMatElement.getAttributes();

        dvnf.setDvnFileUri(nnm.getNamedItem("URI").getNodeValue());
        dvnf.setId(Integer.parseInt(dvnf.getDvnFileUri().toString().split("/api/access/datafile/")[1]));

        Node lablElement = (Node) xPath.evaluate("./*[local-name()='labl']", otherMatElement, XPathConstants.NODE);
        if (lablElement != null)
            dvnf.setTitle(lablElement.getTextContent());

        //DON'T DELETE THIS. I need to deal with this case.
        //if (dataFile != null) {
        // dvnf.setFilesytemname(dataFile.getFilesystemname());
        //   dvnf.setFilepath("data/" + dvnf.getTitle());
        //     if( dataFile.isRestricted())
        //        dvnf.setAccessRights("RESTRICTED_REQUEST");
        // }

        Node txtElement = (Node) xPath.evaluate("./*[local-name()='txt']", otherMatElement, XPathConstants.NODE);
        if (txtElement != null)
            dvnf.setDescription(txtElement.getTextContent());


        Node notesElement = (Node) xPath.evaluate("./*[local-name()='notes']", otherMatElement, XPathConstants.NODE);
        if (notesElement != null)
            dvnf.setFormat(notesElement.getTextContent());

        Node depDateElement = (Node) xPath.evaluate("//*[local-name()='depDate']", otherMatElement, XPathConstants.NODE);
        if (depDateElement != null)
            dvnf.setCreated(depDateElement.getTextContent());


        return dvnf;

    }
}

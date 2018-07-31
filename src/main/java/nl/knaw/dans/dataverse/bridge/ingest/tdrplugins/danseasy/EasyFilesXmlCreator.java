package nl.knaw.dans.dataverse.bridge.ingest.tdrplugins.danseasy;

import nl.knaw.dans.dataverse.bridge.source.dataverse.DvFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.StringWriter;
import java.util.List;

/**
 * Class EasyFilesXmlCreator
 * Created by Eko Indarto
 */
public class EasyFilesXmlCreator {
    private static final Logger LOG = LoggerFactory.getLogger(EasyFilesXmlCreator.class);

    public void create(List<DvFile> efal, File f) throws ParserConfigurationException, TransformerException {
            DocumentBuilderFactory dbFactory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder =
                    dbFactory.newDocumentBuilder();

            Document doc = dBuilder.newDocument();
            Element files = doc.createElement("files");
            files.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:dcterms", "http://purl.org/dc/terms/");
            doc.appendChild(files);
            for (DvFile efa : efal) {
                files.appendChild(createFileElement(doc, efa));
            }

            TransformerFactory transformerFactory =
                    TransformerFactory.newInstance();
            Transformer transformer =
                    null;
            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(doc);

            StringWriter writer = new StringWriter();
            transformer.transform(source, new StreamResult(writer));
            String output = writer.toString();
            LOG.info(output);
            StreamResult result = new StreamResult(f);
            transformer.transform(source, result);

    }

    private Element createFileElement(Document doc, DvFile efa) {

        Element file = doc.createElement("file");

        // setting attribute to element
        Attr attr = doc.createAttribute("filepath");
        attr.setValue(efa.getFilepath());
        file.setAttributeNode(attr);

        Element title = doc.createElement("dcterms:title");
        title.appendChild(
                doc.createTextNode(efa.getTitle()));
        file.appendChild(title);

        Element format = doc.createElement("dcterms:format");
        format.appendChild(
                doc.createTextNode(efa.getFormat()));
        file.appendChild(format);

        Element accessRights = doc.createElement("dcterms:accessRights");
        accessRights.appendChild(
                doc.createTextNode(efa.getAccessRights()));
        file.appendChild(accessRights);

        return file;
    }
}
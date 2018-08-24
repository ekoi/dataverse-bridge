package nl.knaw.dans.dataverse.bridge.core.common;

import nl.knaw.dans.dataverse.bridge.exception.BridgeException;

import java.io.File;
import java.util.Map;
/*
 * @author: Eko Indarto
 */
public interface IBagitComposer {
    public File buildBag(String bagitBaseDir, String srcExportedUrl, Map<String, String> transformedXml, DvFileList dvFileList) throws BridgeException;
}

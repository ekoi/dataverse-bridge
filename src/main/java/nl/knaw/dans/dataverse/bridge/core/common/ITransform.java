package nl.knaw.dans.dataverse.bridge.core.common;

import nl.knaw.dans.dataverse.bridge.exception.BridgeException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/*
 * @author: Eko Indarto
 */
public interface ITransform {
    Map<String, String> getTransformResult(String dvMetadataUrl, String apiToken, List<XsltSource> xlsList) throws BridgeException;
    default Optional<DvFileList> getDvFileList(String apiToken) {
        return Optional.empty();
    }
}

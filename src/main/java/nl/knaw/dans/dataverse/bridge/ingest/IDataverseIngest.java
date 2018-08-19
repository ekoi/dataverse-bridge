package nl.knaw.dans.dataverse.bridge.ingest;

import nl.knaw.dans.dataverse.bridge.exception.BridgeException;
import org.apache.abdera.i18n.iri.IRI;

import java.io.File;

/**
 * Created by akmi on 04/05/17.
 */
public interface IDataverseIngest {
    public ResponseDataHolder execute(File bagDir, IRI colIri, String uid, String pw) throws BridgeException;
}

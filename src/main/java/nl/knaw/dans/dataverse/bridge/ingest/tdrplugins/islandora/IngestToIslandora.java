package nl.knaw.dans.dataverse.bridge.ingest.tdrplugins.islandora;

import nl.knaw.dans.dataverse.bridge.ingest.IDataverseIngest;
import nl.knaw.dans.dataverse.bridge.ingest.ResponseDataHolder;
import org.apache.abdera.i18n.iri.IRI;

import java.io.File;

/**
 * Class IngestToIslandora
 * Created by Eko Indarto
 */
public class IngestToIslandora implements IDataverseIngest {
    @Override
    public ResponseDataHolder execute(File bagDir, IRI colIri, String uid, String pw) {
        return null;
    }
}

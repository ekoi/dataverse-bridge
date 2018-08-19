package nl.knaw.dans.dataverse.bridge.ingest.tdrplugins.archivematica;


import nl.knaw.dans.dataverse.bridge.ingest.IDataverseIngest;
import nl.knaw.dans.dataverse.bridge.ingest.ResponseDataHolder;
import org.apache.abdera.i18n.iri.IRI;

import java.io.File;

/**
 * Class IngestToArchivematica
 * Created by Eko Indarto
 */
public class IngestToArchivematica implements IDataverseIngest {
    @Override
    public ResponseDataHolder execute(File bagDir, IRI colIri, String uid, String pw) {
        return null;
    }
}

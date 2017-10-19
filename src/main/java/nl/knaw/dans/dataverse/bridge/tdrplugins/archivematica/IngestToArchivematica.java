package nl.knaw.dans.dataverse.bridge.tdrplugins.archivematica;

import nl.knaw.dans.dataverse.bridge.tdrplugins.IDataverseIngest;
import org.apache.abdera.i18n.iri.IRI;

import java.io.File;

/**
 * Class IngestToArchivematica
 * Created by Eko Indarto
 */
public class IngestToArchivematica implements IDataverseIngest {
    @Override
    public String execute(File bagDir, IRI colIri, String uid, String pw) {
        return null;
    }

    @Override
    public String getLandingPage() {
        return null;
    }

    @Override
    public String getDoi() {
        return null;
    }
}

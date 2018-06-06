package nl.knaw.dans.dataverse.bridge.ingest.tdrplugins.islandora;

import nl.knaw.dans.dataverse.bridge.ingest.tdrplugins.IDataverseIngest;
import org.apache.abdera.i18n.iri.IRI;

import java.io.File;

/**
 * Class IngestToIslandora
 * Created by Eko Indarto
 */
public class IngestToIslandora implements IDataverseIngest {
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

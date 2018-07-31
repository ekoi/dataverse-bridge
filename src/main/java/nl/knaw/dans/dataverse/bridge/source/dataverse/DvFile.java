package nl.knaw.dans.dataverse.bridge.source.dataverse;

/**
 * Created by akmi on 08/05/17.
 */
public class DvFile {
    /*
    Example:
    <otherMat ID="f6249" URI="https://dataverse.nl/api/access/datafile/6249" level="datafile">
        <labl>Fig3_Bifurcation.R</labl>
        <txt>Phase planes depicting the bifurcation analysis of plant-herbivore models with bioturbation,
        showing that the interplay between bioturbation and biocompaction strongly expands the conditions under which
        heterogeneity can persist in grazing ecosystems, with A) only bioturbation feedback and B)
        the consequence of combining bioturbation and biocompaction feedbacks.
        </txt>
        <notes level="file" type="DATAVERSE:CONTENTTYPE" subject="Content/MIME Type">type/x-r-syntax</notes>
    </otherMat>
    id = 6249
    dvnFileUri = https://dataverse.nl/api/access/datafile/6249
    dvnFilename = Fig3_Bifurcation.R
    For the filesytemname attribute, the data comes from DataFile table of Dataverse Database.
     */
    private int id;
    private String dvnFileUri;
    private String filesytemname;
    private String filepath;
    private String title;
    private String description;
    private String format;
    private String created;
    private String accessRights;


    public String getDvFileUri() {
        return dvnFileUri;
    }

    public void setDvFileUri(String dvnFileUri) {
        this.dvnFileUri = dvnFileUri;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getAccessRights() {
        return accessRights;
    }

    public void setAccessRights(String accessRights) {
        this.accessRights = accessRights;
    }


}

package nl.knaw.dans.dataverse.bridge.source.dataverse;

public enum FilePermissionStatus {
    OTHER,
    OK,
    RESTRICTED;

    public String toString() {
        switch (this) {
            case OK:
                return "OK";
            case RESTRICTED:
                return "RESTRICTED";
        }
        return "OTHER";//default
    }
}

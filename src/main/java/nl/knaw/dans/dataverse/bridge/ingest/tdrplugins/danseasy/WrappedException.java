package nl.knaw.dans.dataverse.bridge.ingest.tdrplugins.danseasy;

public class WrappedException extends RuntimeException {
    Throwable cause;

    WrappedException(Throwable cause) { this.cause = cause; }
}

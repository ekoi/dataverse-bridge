package nl.knaw.dans.dataverse.bridge.exception;

public class BridgeException extends Exception {
    private String className;
    public Throwable cause;

    public BridgeException(Throwable cause) { this.cause = cause; }
    public BridgeException(String message, String className) {
        super(message);
        this.className = className;
    }

    public BridgeException(String message, Throwable error, String className) {
        super(message, error);
        this.className = className;
    }

    public BridgeException(Throwable e, String className) {
        super(e);
        this.className = className;
    }

    public String getClassName() {
        return className;
    }
}

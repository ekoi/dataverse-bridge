package nl.knaw.dans.dataverse.bridge.exception;
/*
 * @author: Eko Indarto
 */
public class BridgeException extends Exception {
    private String className;
    public Throwable cause;

    public BridgeException(Throwable cause) { this.cause = cause; }
    public BridgeException(String message, Class clazz) {
        super(message);
        this.className = clazz.getName();
    }

    public BridgeException(String message, Throwable error, Class clazz) {
        super(message, error);
        this.className = clazz.getName();
    }

    public BridgeException(Throwable e, Class clazz) {
        super(e);
        this.className = clazz.getName();
    }

    public String getClassName() {
        return className;
    }
}

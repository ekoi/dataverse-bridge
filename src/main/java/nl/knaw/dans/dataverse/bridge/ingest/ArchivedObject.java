package nl.knaw.dans.dataverse.bridge.ingest;

public class ArchivedObject {
    private String status;
    private String pid;
    private String landingPage;
    private String auditLogResponse;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getLandingPage() {
        return landingPage;
    }

    public void setLandingPage(String landingPage) {
        this.landingPage = landingPage;
    }

    public String getAuditLogResponse() {
        return auditLogResponse;
    }

    public void setAuditLogResponse(String auditLogResponse) {
        this.auditLogResponse = auditLogResponse;
    }
}

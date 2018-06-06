package nl.knaw.dans.dataverse.bridge.core.db.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "_archived", uniqueConstraints =
@UniqueConstraint(columnNames = {"srcXml", "srcVersion", "targetIri"}))
public class Archived   {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  private String srcXml;

  @NotNull
  private String srcAppName;

  @NotNull
  private String srcVersion;

  @NotNull
  private String targetIri;

  @NotNull
  private String tdrAppName;

  private String doi;

  @NotNull
  @Temporal(value = TemporalType.TIMESTAMP)
  private Date startTime;

  @Temporal(value = TemporalType.TIMESTAMP)
  private Date endTime;

  private String landingPage;

  /**
   * Gets or Sets status
   */
  public enum StateEnum {
    IN_PROGRESS("IN-PROGRESS"),
    
    FAILED("FAILED"),
    
    ARCHIVED("ARCHIVED");

    private String value;

    StateEnum(String value) {
      this.value = value;
    }
    
    public String toString() {
      return String.valueOf(value);
    }

    public static StateEnum fromValue(String text) {
      for (StateEnum b : StateEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @NotNull
  private String state;

  public Long getId() {
    return id;
  }

  public String getDoi() {
    return doi;
  }

  public void setDoi(String doi) {
    this.doi = doi;
  }

  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }

  public String getLandingPage() {
    return landingPage;
  }

  public void setLandingPage(String landingPage) {
    this.landingPage = landingPage;
  }

  public String getSrcXml() {
    return srcXml;
  }

  public void setSrcXml(String srcXml) {
    this.srcXml = srcXml;
  }

  public String getSrcAppName() {
    return srcAppName;
  }

  public void setSrcAppName(String srcAppName) {
    this.srcAppName = srcAppName;
  }

  public String getSrcVersion() {
    return srcVersion;
  }

  public void setSrcVersion(String srcVersion) {
    this.srcVersion = srcVersion;
  }

  public String getTargetIri() {
    return targetIri;
  }

  public void setTargetIri(String targetIri) {
    this.targetIri = targetIri;
  }

  public String getTdrAppName() {
    return tdrAppName;
  }

  public void setTdrAppName(String tdrAppName) {
    this.tdrAppName = tdrAppName;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }
}


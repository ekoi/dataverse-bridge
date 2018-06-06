package nl.knaw.dans.dataverse.bridge.generated.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * TdrData
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-30T15:10:34.530+02:00")

public class TdrData   {
  @JsonProperty("username")
  private String username = null;

  @JsonProperty("password")
  private String password = null;

  @JsonProperty("appName")
  private String appName = null;

  @JsonProperty("iri")
  private String iri = null;

  public TdrData username(String username) {
    this.username = username;
    return this;
  }

  /**
   * Get username
   * @return username
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public TdrData password(String password) {
    this.password = password;
    return this;
  }

  /**
   * Get password
   * @return password
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public TdrData appName(String appName) {
    this.appName = appName;
    return this;
  }

  /**
   * Get appName
   * @return appName
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public TdrData iri(String iri) {
    this.iri = iri;
    return this;
  }

  /**
   * Get iri
   * @return iri
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public String getIri() {
    return iri;
  }

  public void setIri(String iri) {
    this.iri = iri;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TdrData tdrData = (TdrData) o;
    return Objects.equals(this.username, tdrData.username) &&
        Objects.equals(this.password, tdrData.password) &&
        Objects.equals(this.appName, tdrData.appName) &&
        Objects.equals(this.iri, tdrData.iri);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, password, appName, iri);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TdrData {\n");
    
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("    password: ").append(toIndentedString(password)).append("\n");
    sb.append("    appName: ").append(toIndentedString(appName)).append("\n");
    sb.append("    iri: ").append(toIndentedString(iri)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}


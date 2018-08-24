package nl.knaw.dans.dataverse.bridge.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * SrcData
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-08-24T14:46:26.508+02:00")

public class SrcData   {
  @JsonProperty("srcXml")
  private String srcXml = null;

  @JsonProperty("srcVersion")
  private String srcVersion = null;

  @JsonProperty("apiToken")
  private String apiToken = null;

  public SrcData srcXml(String srcXml) {
    this.srcXml = srcXml;
    return this;
  }

  /**
   * exported xml link
   * @return srcXml
  **/
  @ApiModelProperty(required = true, value = "exported xml link")
  @NotNull


  public String getSrcXml() {
    return srcXml;
  }

  public void setSrcXml(String srcXml) {
    this.srcXml = srcXml;
  }

  public SrcData srcVersion(String srcVersion) {
    this.srcVersion = srcVersion;
    return this;
  }

  /**
   * Get srcVersion
   * @return srcVersion
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public String getSrcVersion() {
    return srcVersion;
  }

  public void setSrcVersion(String srcVersion) {
    this.srcVersion = srcVersion;
  }

  public SrcData apiToken(String apiToken) {
    this.apiToken = apiToken;
    return this;
  }

  /**
   * Api Token is optional
   * @return apiToken
  **/
  @ApiModelProperty(value = "Api Token is optional")


  public String getApiToken() {
    return apiToken;
  }

  public void setApiToken(String apiToken) {
    this.apiToken = apiToken;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SrcData srcData = (SrcData) o;
    return Objects.equals(this.srcXml, srcData.srcXml) &&
        Objects.equals(this.srcVersion, srcData.srcVersion) &&
        Objects.equals(this.apiToken, srcData.apiToken);
  }

  @Override
  public int hashCode() {
    return Objects.hash(srcXml, srcVersion, apiToken);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SrcData {\n");
    
    sb.append("    srcXml: ").append(toIndentedString(srcXml)).append("\n");
    sb.append("    srcVersion: ").append(toIndentedString(srcVersion)).append("\n");
    sb.append("    apiToken: ").append(toIndentedString(apiToken)).append("\n");
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


package nl.knaw.dans.dataverse.bridge.core.common;

import java.util.Objects;

/**
 * XsltSource
 * @Author: Eko Indarto
 */
public class XsltSource {
  private String xslName;
  private String xslUrl;

  public XsltSource(String xslName, String xslUrl) {
    this.xslName = xslName;
    this.xslUrl = xslUrl;
  }

  public XsltSource xslName(String xslName) {
    this.xslName = xslName;
    return this;
  }

  /**
   * Get xslName
   * @return xslName
  **/
  public String getXslName() {
    return xslName;
  }

  public void setXslName(String xslName) {
    this.xslName = xslName;
  }

  public XsltSource location(String location) {
    this.xslUrl = location;
    return this;
  }

  /**
   * Get xslUrl
   * @return xslUrl
  **/
  public String getXslUrl() {
    return xslUrl;
  }

  public void setXslUrl(String xslUrl) {
    this.xslUrl = xslUrl;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    XsltSource xsltSource = (XsltSource) o;
    return Objects.equals(this.xslName, xsltSource.xslName) &&
        Objects.equals(this.xslUrl, xsltSource.xslUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(xslName, xslUrl);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class XsltSource {\n");

    sb.append("    xslName: ").append(toIndentedString(xslName)).append("\n");
    sb.append("    xslUrl: ").append(toIndentedString(xslUrl)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}


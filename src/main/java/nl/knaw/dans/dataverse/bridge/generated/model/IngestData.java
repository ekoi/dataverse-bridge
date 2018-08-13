package nl.knaw.dans.dataverse.bridge.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * IngestData
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-08-10T22:28:04.437+02:00")

public class IngestData   {
  @JsonProperty("srcData")
  private SrcData srcData = null;

  @JsonProperty("tdrData")
  private TdrData tdrData = null;

  public IngestData srcData(SrcData srcData) {
    this.srcData = srcData;
    return this;
  }

  /**
   * Get srcData
   * @return srcData
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public SrcData getSrcData() {
    return srcData;
  }

  public void setSrcData(SrcData srcData) {
    this.srcData = srcData;
  }

  public IngestData tdrData(TdrData tdrData) {
    this.tdrData = tdrData;
    return this;
  }

  /**
   * Get tdrData
   * @return tdrData
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public TdrData getTdrData() {
    return tdrData;
  }

  public void setTdrData(TdrData tdrData) {
    this.tdrData = tdrData;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    IngestData ingestData = (IngestData) o;
    return Objects.equals(this.srcData, ingestData.srcData) &&
        Objects.equals(this.tdrData, ingestData.tdrData);
  }

  @Override
  public int hashCode() {
    return Objects.hash(srcData, tdrData);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class IngestData {\n");
    
    sb.append("    srcData: ").append(toIndentedString(srcData)).append("\n");
    sb.append("    tdrData: ").append(toIndentedString(tdrData)).append("\n");
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


package nl.knaw.dans.dataverse.bridge.core.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * TdrConf
 * Created by Eko Indarto
 */

public class TdrConf {
    private String tdrName;
    private String iri;
    private String actionClassName;
    private List<XsltSource> xsl;

    public TdrConf(String tdrName, String iri, String actionClassName, List<XsltSource> xsl) {
        this.tdrName = tdrName;
        this.iri = iri;
        this.actionClassName = actionClassName;
        this.xsl = xsl;
    }

    public TdrConf tdrName(String tdrName) {
        this.tdrName = tdrName;
        return this;
    }

    public String getTdrName() {
        return tdrName;
    }

    public void setTdrName(String tdrName) {
        this.tdrName = tdrName;
    }

    public TdrConf actionClassName(String actionClassName) {
        this.actionClassName = actionClassName;
        return this;
    }

    public String getIri() {
        return iri;
    }

    public String getActionClassName() {
        return actionClassName;
    }

    public void setActionClassName(String actionClassName) {
        this.actionClassName = actionClassName;
    }

    public TdrConf xsl(List<XsltSource> xsl) {
        this.xsl = xsl;
        return this;
    }

    public TdrConf addXslItem(XsltSource xslItem) {
        if (this.xsl == null) {
            this.xsl = new ArrayList<>();
        }
        this.xsl.add(xslItem);
        return this;
    }

    public List<XsltSource> getXsl() {
        return xsl;
    }

    public void setXsl(List<XsltSource> xsl) {
        this.xsl = xsl;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TdrConf tdrConf = (TdrConf) o;
        return Objects.equals(this.tdrName, tdrConf.tdrName) &&
                Objects.equals(this.actionClassName, tdrConf.actionClassName) &&
                Objects.equals(this.xsl, tdrConf.xsl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tdrName, actionClassName, xsl);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class TdrConf {\n");

        sb.append("    tdrName: ").append(toIndentedString(tdrName)).append("\n");
        sb.append("    actionClassName: ").append(toIndentedString(actionClassName)).append("\n");
        sb.append("    xsl: ").append(toIndentedString(xsl)).append("\n");
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


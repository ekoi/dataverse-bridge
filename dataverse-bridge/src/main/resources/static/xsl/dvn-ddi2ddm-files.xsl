<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
        xmlns:ddi="ddi:codebook:2_5"
        xmlns:dcterms="http://purl.org/dc/terms/"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xsi:schemaLocation="ddi:codebook:2_5 http://www.ddialliance.org/Specification/DDI-Codebook/2.5/XMLSchema/codebook.xsd"
        version="2.0">
    <xsl:output encoding="UTF-8" indent="yes" method="xml"/>
    <xsl:template match="/">
        <files>
            <xsl:element name="file">
                <xsl:variable name="fn" select="/ddi:codeBook/ddi:docDscr/ddi:citation/ddi:titlStmt/ddi:IDNo"/>
                <xsl:variable name="fn2" select="replace($fn,':','-')"/>
                <xsl:variable name="filename" select="replace($fn2,'/','-')"/>
                <xsl:attribute name="filepath" select="concat('data/',$filename, '.xml')"/>
                <dcterms:title><xsl:value-of select="$filename"/></dcterms:title>
                <dcterms:format>application/xml</dcterms:format>
            </xsl:element>
            <xsl:call-template name="item0"/>
        </files>
    </xsl:template>

    <xsl:template name="item0">
        <xsl:for-each select="/ddi:codeBook/ddi:otherMat/ddi:labl">
            <xsl:element name="file">
                <xsl:attribute name="filepath" select="concat('data/',.)"/>
                <dcterms:title><xsl:value-of select="."/></dcterms:title>
                <dcterms:description><xsl:value-of select="../ddi:txt"/></dcterms:description>
                <dcterms:format><xsl:value-of select="../ddi:notes"/></dcterms:format>
                <dcterms:created><xsl:value-of select="/ddi:codeBook/ddi:stdyDscr/ddi:stdyInfo/ddi:depDate"/></dcterms:created>
                <dcterms:accessRights/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>

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
            <xsl:call-template name="item0"/>
            <xsl:variable name="s1" select="/ddi:codeBook/ddi:stdyDscr/ddi:citation/ddi:titlStmt/ddi:IDNo"/>
            <xsl:variable name="s2" select="replace($s1, 'hdl:', '')"/>
            <xsl:variable name="s3" select="replace($s2, '/', '-')"/>
            <xsl:element name="file">
                <xsl:attribute name="filepath" select="concat('data/',$s3, '.dbar')"/>
                <dcterms:title>DANS DataverseBridge Archiving Report.txt</dcterms:title>
                <dcterms:description>DANS DataverseBridge Archiving Report.</dcterms:description>
                <dcterms:format>text/plain</dcterms:format>
                <dcterms:created><xsl:value-of  select="format-dateTime(current-dateTime(), '[D01]-[M01]-[Y0001]')"/></dcterms:created>
                <dcterms:accessRights>RESTRICTED_REQUEST</dcterms:accessRights>
            </xsl:element>
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
                <dcterms:accessRights>RESTRICTED_REQUEST</dcterms:accessRights>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>

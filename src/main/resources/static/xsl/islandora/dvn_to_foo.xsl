<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
        xmlns:ddi="ddi:codebook:2_5"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xsi:schemaLocation="ddi:codebook:2_5 http://www.ddialliance.org/Specification/DDI-Codebook/2.5/XMLSchema/codebook.xsd"
        version="2.0">
    <xsl:output encoding="UTF-8" indent="yes" method="xml"/>
    <xsl:template match="/">
        <files>
            <file>RELS-EXT.rdf</file>
            <file>MODS.xml</file>
            <file>DC.bin</file>
            <xsl:call-template name="item0"/>
        </files>
    </xsl:template>

    <xsl:template name="item0">
        <xsl:for-each select="/ddi:codeBook/ddi:otherMat/ddi:labl">
            <xsl:element name="file"><xsl:value-of select="."/>
                
            </xsl:element>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>

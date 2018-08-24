<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/2005/Atom"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:dcterms="http://purl.org/dc/terms/"
                exclude-result-prefixes="xs"
                version="2.0">
    <xsl:output encoding="UTF-8" indent="yes" method="xml"/>
    <xsl:output method="xml" indent="yes"/>
    <xsl:template match="/">
        <entry>
            <xsl:call-template name="eko"/>
        </entry>
    </xsl:template>
    <xsl:template name="eko">
        <xsl:copy-of copy-namespaces="no" select=".//dcterms:*"/>
    </xsl:template>
</xsl:stylesheet>
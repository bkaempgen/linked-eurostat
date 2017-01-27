<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
   xmlns="http://www.w3.org/1999/xhtml"
   xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
   xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
   xmlns:cop="http://ontologycentral.com/copernicus/ns#"
   xmlns:foaf="http://xmlns.com/foaf/0.1/"
   xmlns:dc="http://purl.org/dc/elements/1.1/"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:nt="urn:eu.europa.ec.eurostat.navtree"
   version="1.0">

  <xsl:output method="xml"
	      encoding="utf-8"/>

  <xsl:template match="nt:tree">
    <rdf:RDF>
      <xsl:apply-templates/>
    </rdf:RDF>
  </xsl:template>

  <xsl:template match="nt:branch">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="nt:children">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="nt:leaf">
    <rdf:Description>
      <xsl:attribute name="rdf:about">./id/<xsl:value-of select="nt:code"/></xsl:attribute>
      <dc:title><xsl:value-of select="nt:title[@language='en']"/></dc:title>
      <dc:date><xsl:value-of select="nt:lastUpdate"/></dc:date>
    </rdf:Description>
  </xsl:template>

  <xsl:template match="*"/>
</xsl:stylesheet>

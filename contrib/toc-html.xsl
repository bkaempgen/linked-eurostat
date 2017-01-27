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
	      encoding="utf-8"
	      doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
	      doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" />

  <xsl:template match="nt:tree">
    <html>
      <head>
	<title>Table of contents</title>
	<style type="text/css" media="screen">
	  .title { font-size: 120% }

	  dl {
	    margin-top: 0em;
	    padding-top: 0em;
	    margin-left: 1em;
	  }
	</style>
      </head>
      <body>
	<div>
	  <a href="/">Home</a>
	</div>

	<h1>Table of Contents</h1>

	<p>
	  As of <xsl:value-of  select="current-dateTime()"/>.
	</p>

	<xsl:apply-templates/>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="nt:branch">
    <dl>
      <dt>
	<a>
	  <xsl:attribute name="name"><xsl:value-of select="nt:title[@language='en']"/></xsl:attribute>
	  <xsl:value-of select="nt:title[@language='en']"/>
	</a>
      </dt>
      <dd>
	<xsl:apply-templates/>
      </dd>
    </dl>
  </xsl:template>

  <xsl:template match="nt:children">
    <ul>
    <xsl:apply-templates/>
    </ul>
  </xsl:template>

  <xsl:template match="nt:leaf">
    <li>
      <a>
	<xsl:attribute name="href">./id/<xsl:value-of select="nt:code"/></xsl:attribute>
	<xsl:value-of select="nt:title[@language='en']"/>
      </a>
    </li>
  </xsl:template>

  <xsl:template match="*"/>
</xsl:stylesheet>

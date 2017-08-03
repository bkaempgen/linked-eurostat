<?xml version='1.0' encoding='utf-8'?>

<xsl:stylesheet
   xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
   xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
   xmlns:dcterms="http://purl.org/dc/terms/"
   xmlns:ical="http://www.w3.org/2002/12/cal/ical#"
   xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
   xmlns:gaap="http://edgarwrap.ontologycentral.com/vocab/us-gaap#"
   xmlns:sdmx-measure="http://purl.org/linked-data/sdmx/2009/measure#"
   xmlns:qb="http://purl.org/linked-data/cube#"
   xmlns:skos="http://www.w3.org/2004/02/skos/core#"
   xmlns:foaf="http://xmlns.com/foaf/0.1/"
   xmlns:sdmx="http://www.SDMX.org/resources/SDMXML/schemas/v2_0/message"
   xmlns:common="http://www.SDMX.org/resources/SDMXML/schemas/v2_0/common"
   xmlns:compact="http://www.SDMX.org/resources/SDMXML/schemas/v2_0/compact"
   xmlns:cross="http://www.SDMX.org/resources/SDMXML/schemas/v2_0/cross"
   xmlns:generic="http://www.SDMX.org/resources/SDMXML/schemas/v2_0/generic"
   xmlns:query="http://www.SDMX.org/resources/SDMXML/schemas/v2_0/query"
   xmlns:structure="http://www.SDMX.org/resources/SDMXML/schemas/v2_0/structure"
   xmlns:utility="http://www.SDMX.org/resources/SDMXML/schemas/v2_0/utility"
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   xsi:schemaLocation="http://www.SDMX.org/resources/SDMXML/schemas/v2_0/message SDMXMessage.xsd"
   version='1.0'>

  <xsl:output method='xml' encoding='utf-8'/>

  <xsl:variable name="lowercase" select="'abcdefghijklmnopqrstuvwxyz'" />
  <xsl:variable name="uppercase" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'" />

  <xsl:template match='sdmx:Structure'>
    <rdf:RDF>
      <rdf:Description rdf:about="">
	<rdfs:comment>No guarantee of correctness! USE AT YOUR OWN RISK!</rdfs:comment>
	<dcterms:publisher>Eurostat (http://epp.eurostat.ec.europa.eu/) via Linked Eurostat (http://estatwrap.ontologycentral.com/)</dcterms:publisher>
	<foaf:topic rdf:resource="#dsd"/>
      </rdf:Description>

      <!--
	  The dimension components serve to identify the observations. A set of values for all the dimension components is sufficient to identify a single observation. Examples of dimensions include the time to which the observation applies, or a geographic region which the observation covers.
	  
	  The measure components represent the phenomenon being observed.
	  
	  The attribute components allow us to qualify and interpret the observed value(s). They enable specification of the units of measures, any scaling factors and metadata such as the status of the observation (e.g. estimated, provisional).
	-->

      <qb:DataStructureDefinition rdf:about="#dsd">
	<foaf:page rdf:resource=""/>
	<qb:component>
	  <rdf:Description>
	    <qb:measure>
	      <rdfs:Property rdf:about="http://purl.org/linked-data/sdmx/2009/measure#obsValue">
	      <rdfs:range rdf:resource="http://www.w3.org/2001/XMLSchema#double"/>
	      </rdfs:Property>
	    </qb:measure>
	  </rdf:Description>
	</qb:component>
	<qb:component>
	  <rdf:Description>
	    <qb:dimension>
	      <rdfs:Property rdf:about="http://purl.org/dc/terms/date">
		<rdfs:range rdf:resource="http://www.w3.org/2001/XMLSchema#date"/>
	      </rdfs:Property>
	    </qb:dimension>
	  </rdf:Description>
	</qb:component>
	<!--
	<qb:component>
	  <rdf:Description>
	    <qb:dimension>
	      <rdfs:Property rdf:about="http://ontologycentral.com/2009/01/eurostat/ns#geo">
		<rdfs:range rdf:resource="http://rdfdata.eionet.europa.eu/ramon/ontology/NUTSRegion"/>
	      </rdfs:Property>
	    </qb:dimension>
	  </rdf:Description>
	</qb:component>
	-->

	<xsl:for-each select="sdmx:CodeLists/structure:CodeList">
	  <qb:component>
	    <rdf:Description>
	      <xsl:choose>
		<xsl:when test="@id = 'CL_OBS_STATUS'">
		  <qb:attribute>
		    <rdfs:Property rdf:about="http://ontologycentral.com/2009/01/eurostat/ns#obs_status">
		      <rdfs:range rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
		      <qb:codeList>
			<xsl:attribute name="rdf:resource">#<xsl:value-of select="translate(@id, $uppercase, $lowercase)"/></xsl:attribute>
		      </qb:codeList>
		    </rdfs:Property>
		  </qb:attribute>
		</xsl:when>
		<xsl:when test="@id = 'CL_FREQ'">
		  <qb:attribute>
		    <rdfs:Property rdf:about="http://ontologycentral.com/2009/01/eurostat/ns#freq">
		      <rdfs:range rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
		      <qb:codeList>
			<xsl:attribute name="rdf:resource">#<xsl:value-of select="translate(@id, $uppercase, $lowercase)"/></xsl:attribute>
		      </qb:codeList>
		    </rdfs:Property>
		  </qb:attribute>
		</xsl:when>
		<xsl:when test="@id = 'CL_TIME_FORMAT'">
		  <qb:attribute>
		    <rdfs:Property rdf:about="http://ontologycentral.com/2009/01/eurostat/ns#timeformat">
		      <rdfs:range rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
		      <qb:codeList>
			<xsl:attribute name="rdf:resource">#<xsl:value-of select="translate(@id, $uppercase, $lowercase)"/></xsl:attribute>
		      </qb:codeList>
		    </rdfs:Property>
		  </qb:attribute>
		</xsl:when>
		<xsl:when test="@id = 'CL_GEO'">
		  <qb:attribute>
		    <rdfs:Property rdf:about="http://ontologycentral.com/2009/01/eurostat/ns#geo">
		      <rdfs:range rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
		      <qb:codeList>
			<xsl:attribute name="rdf:resource">#<xsl:value-of select="translate(@id, $uppercase, $lowercase)"/></xsl:attribute>
		      </qb:codeList>
		    </rdfs:Property>
		  </qb:attribute>
		</xsl:when>
		<xsl:otherwise>
		  <qb:dimension>
		    <rdfs:Property>
		      <xsl:attribute name="rdf:about">http://ontologycentral.com/2009/01/eurostat/ns#<xsl:value-of select="substring(translate(@id, $uppercase, $lowercase), 4)"/></xsl:attribute>
		      <rdfs:range rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
		      <qb:codeList>
			<xsl:attribute name="rdf:resource">#<xsl:value-of select="translate(@id, $uppercase, $lowercase)"/></xsl:attribute>
		      </qb:codeList>
		    </rdfs:Property>
		  </qb:dimension>
		</xsl:otherwise>
	      </xsl:choose>
	    </rdf:Description>
	  </qb:component>
	</xsl:for-each>
      </qb:DataStructureDefinition>

      <xsl:apply-templates/>
    </rdf:RDF>
  </xsl:template>

  <xsl:template match='sdmx:Header'>
    <rdf:Description rdf:about="#header">
      <xsl:apply-templates/>
    </rdf:Description>
  </xsl:template>

  <xsl:template match='sdmx:ID'>
    <dcterms:identifier><xsl:value-of select="."/></dcterms:identifier>
  </xsl:template>

  <xsl:template match='sdmx:Prepared'>
    <dcterms:date><xsl:value-of select="."/></dcterms:date>
  </xsl:template>

  <xsl:template match='sdmx:CodeLists'>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match='structure:CodeList'>
<!--    <xsl:if test="@id != 'CL_GEO'"> -->
      <skos:ConceptScheme>
	<xsl:attribute name="rdf:about">#<xsl:value-of select="translate(@id, $uppercase, $lowercase)"/></xsl:attribute>
	<skos:notation><xsl:value-of select="translate(@id, $uppercase, $lowercase)"/></skos:notation>
	<xsl:apply-templates/>
      </skos:ConceptScheme>
<!--    </xsl:if> -->
  </xsl:template>

  <xsl:template match='structure:Name|structure:Description'>
    <rdfs:label>
      <xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang"/></xsl:attribute>
      <xsl:value-of select="."/>
    </rdfs:label>
  </xsl:template>

  <xsl:template match='structure:Code'>
    <skos:hasTopConcept>
      <skos:Concept>
	<!--
	    <xsl:choose>
	    <xsl:when test="../@id='CL_geo'">
	    <xsl:attribute name="rdf:about">/dic/geo<xsl:value-of select="@value"/></xsl:attribute>
	    </xsl:when>
	    <xsl:otherwise>
	-->
	<xsl:attribute name="rdf:about">/dic/<xsl:value-of select="translate(substring(../@id, 4), $uppercase, $lowercase)"/>#<xsl:value-of select="@value"/></xsl:attribute>
	<skos:inScheme><xsl:attribute name="rdf:resource">#<xsl:value-of select="translate(../@id, $uppercase, $lowercase)"/></xsl:attribute></skos:inScheme>

	<xsl:apply-templates/>
      </skos:Concept>
    </skos:hasTopConcept>
  </xsl:template>

<!--
<http://example.org/EuroStat/CodeList/geo>	a skos:ConceptScheme;
		rdfs:label "Geopolitical entity (declaring)"@en;
		skos:notation "CL_geo";
		skos:hasTopConcept <http://example.org/EuroStat/CodeList/geo#EU27>;
		skos:hasTopConcept <http://example.org/EuroStat/CodeList/geo#EU25>;
		skos:hasTopConcept <http://example.org/EuroStat/CodeList/geo#EU15>;
<http://example.org/EuroStat/CodeList/geo#EU27>	a skos:Concept;
		skos:prefLabel "European Union (27 countries)"@en;
		skos:inScheme <http://example.org/EuroStat/CodeList/geo>;
		skos:notation "EU27"
-->

  <xsl:template match="*"/>
</xsl:stylesheet>

<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="xml" indent="yes"/>

  <xsl:param name="mirrorsURL"/>
  <xsl:param name="statsURI"/>
  
  <!-- add p2.mirrorsURL property -->
  <xsl:template match="repository/properties">
	<xsl:copy>
	  <xsl:copy-of select="@*"/>
	  <xsl:apply-templates/>
	  <xsl:element name="property">
		<xsl:attribute name="name">p2.mirrorsURL</xsl:attribute>
		<xsl:attribute name="value"><xsl:value-of select="$mirrorsURL" /></xsl:attribute>	
	  </xsl:element>
	  <xsl:text>&#10;</xsl:text>
	  <xsl:element name="property">
		<xsl:attribute name="name">p2.statsURI</xsl:attribute>
		<xsl:attribute name="value"><xsl:value-of select="$statsURI" /></xsl:attribute>	
	  </xsl:element>
	  <xsl:text>&#10;</xsl:text>
	</xsl:copy>
  </xsl:template>

  <!-- add p2.mirrorsURL property -->
  <xsl:template match="repository/artifacts/artifact/properties[../@classifier='org.eclipse.update.feature']">
	<!-- extract artifact id -->
	<xsl:variable name="artifactId">
      <xsl:value-of select="parent::artifact/@id"/>
	</xsl:variable>

	<xsl:copy>
	  <xsl:copy-of select="@*"/>
	  <xsl:apply-templates/>
	  <xsl:element name="property">
		<xsl:attribute name="name">download.stats</xsl:attribute>
		<xsl:attribute name="value"><xsl:value-of select="$artifactId" /></xsl:attribute>	
	  </xsl:element>
	  <xsl:text>&#10;</xsl:text>
	</xsl:copy>
  </xsl:template>

  <!-- copy everything else -->
  <xsl:template match="* | @*">
	<xsl:copy><xsl:copy-of select="@*"/><xsl:apply-templates/></xsl:copy>
  </xsl:template>

</xsl:stylesheet>

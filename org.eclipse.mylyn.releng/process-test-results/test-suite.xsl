<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="xml" indent="yes"/>
  <xsl:param name="append"/>
  
  <!-- annotate test cases with last preceeding suite activation -->
  <xsl:template match="testcase">
	<!-- append text to test name -->
	<xsl:copy>
	  <xsl:copy-of select="@*"/>
	  <xsl:attribute name="name">
		<xsl:value-of select="@name" /><xsl:value-of select="$append" />
	  </xsl:attribute>
	  <xsl:apply-templates/>
	</xsl:copy>
  </xsl:template>

  <!-- copy everything else -->
  <xsl:template match="* | @*">
	<xsl:copy><xsl:copy-of select="@*"/><xsl:apply-templates/></xsl:copy>
  </xsl:template>

</xsl:stylesheet>

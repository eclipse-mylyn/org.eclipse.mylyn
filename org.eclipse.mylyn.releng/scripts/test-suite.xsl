<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html" indent="yes"/>
  
  <!-- filter suite activations -->
  <xsl:template match="testcase[@classname='org.eclipse.mylyn.tests.util.TestFixture$Activation']">
  </xsl:template>
  
  <!-- annotate test cases with last preceeding suite activation -->
  <xsl:template match="testcase">
	<!-- extract text between brackets -->
	<xsl:variable name="fixture">
      <xsl:value-of select="substring-before(substring-after((preceding-sibling::testcase[@classname='org.eclipse.mylyn.tests.util.TestFixture$Activation'])[position() = last()]/@name,'['),']')"/>
	</xsl:variable>

	<!-- append text to test name -->
	<xsl:copy>
	  <xsl:copy-of select="@*"/>
	  <xsl:attribute name="name">
		<xsl:value-of select="@name" /><xsl:value-of select="$fixture" />
	  </xsl:attribute>
	  <xsl:apply-templates/>
	</xsl:copy>
  </xsl:template>

  <!-- copy everything else -->
  <xsl:template match="* | @*">
	<xsl:copy><xsl:copy-of select="@*"/><xsl:apply-templates/></xsl:copy>
  </xsl:template>

</xsl:stylesheet>

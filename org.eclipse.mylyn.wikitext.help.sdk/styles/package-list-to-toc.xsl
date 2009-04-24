<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="2.0">
                                
    <xsl:output method="xml"
    		encoding="UTF-8"
            indent="yes"/>
            

	<xsl:template match="/packages">
<toc label="Reference">
	<topic label="API Reference" href="reference/api/overview-summary.html">
		<xsl:for-each select="package">
		<topic href="reference/api/{translate(text(),'.','/')}/package-summary.html" label="{text()}" />
		</xsl:for-each>
	</topic>
</toc>
	</xsl:template>

</xsl:stylesheet>
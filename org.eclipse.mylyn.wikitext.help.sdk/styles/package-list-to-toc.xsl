<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="2.0">
                                
    <xsl:output method="xml"
    		encoding="UTF-8"
            indent="yes"/>
            

	<xsl:template match="/packages">
<toc label="Reference" link_to="toc.xml#reference">
	<topic label="Reference">
		<topic label="API Reference" href="reference/api/overview-summary.html">
			<xsl:for-each select="package">
			<topic href="reference/api/{translate(text(),'.','/')}/package-summary.html" label="{text()}" />
			</xsl:for-each>
		</topic>
		<topic label="Extension Points Reference">
			<topic href="reference/extension-points/org_eclipse_mylyn_wikitext_core_markupLanguage.html" label="org.eclipse.mylyn.wikitext.core.markupLanguage"/>
			<topic href="reference/extension-points/org_eclipse_mylyn_wikitext_core_markupValidationRule.html" label="org.eclipse.mylyn.wikitext.core.markupValidationRule"/>
			<topic href="reference/extension-points/org_eclipse_mylyn_wikitext_ui_cheatSheet.html" label="org.eclipse.mylyn.wikitext.ui.cheatSheet"/>
			<topic href="reference/extension-points/org_eclipse_mylyn_wikitext_ui_contentAssist.html" label="org.eclipse.mylyn.wikitext.ui.contentAssist"/>
		</topic>
	</topic>
</toc>
	</xsl:template>

</xsl:stylesheet>
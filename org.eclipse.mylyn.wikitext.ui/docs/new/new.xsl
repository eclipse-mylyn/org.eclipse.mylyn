<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:fn="http://www.w3.org/2005/xpath-functions"
                version="2.0">
                                
    <xsl:output method="xml"
            encoding="ISO-8859-1"
            indent="no"/>
            

	<xsl:template match="/new">
			<html>
				<head>
					<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
					<title>New And Noteworthy</title>
			    	<style type="text/css">
img {
	margin: 5px;
}

table.category0 th {
	background-color:  #FAF7FB;
}

table.category1 th {
	background-color:  #E0D0E6;
}
			    	</style>
				</head>
				<body>
					<div class="mainContent">
					<xsl:for-each select="category">
						<h2><a name="{title}"></a><xsl:value-of select="title"/></h2>
						<xsl:call-template name="CategoryContent"/>
						<table cellpadding="10" cellspacing="0" class="category category{position() mod 2}">
							<xsl:for-each select="item">
								<xsl:if test="position() != 1">
									<tr>
										<td colspan="2"><hr/></td>
									</tr>
								</xsl:if>
								<tr class="item item{position() mod 2}">
									<th align="right" valign="top" width="15%">
										<xsl:call-template name="ItemTitle"/>
									</th>
									<td align="left" valign="top" width="70%">
										<xsl:call-template name="ItemContent"/>
									</td>
								</tr>
							</xsl:for-each>
						</table>
					</xsl:for-each>
					</div>
				</body>
			</html>
	</xsl:template>
	
	<xsl:template name="CategoryContent">
		<xsl:for-each select="description">
			<xsl:copy-of select="child::node()"/>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="ItemTitle">
		<xsl:for-each select="title">
			<xsl:copy-of select="child::node()"/>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="ItemContent">
		<xsl:for-each select="description">
			<xsl:copy-of select="child::node()"/>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
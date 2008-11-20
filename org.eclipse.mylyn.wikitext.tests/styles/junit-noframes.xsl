<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html"/>
<!--

This stylesheet is used with junitreport instead of the default since the default stylesheets do a really
bad job of formatting nested test cases.
 
 -->	
	<xsl:template match="/">
		<html>
			<head>
				<title>Unit Test Results</title>
				<style type="text/css">
body {
  font: normal 68% verdana,arial,helvetica;
}			
.error {
	color: red;
}
.failure {
	color: red;
}
.warning {
	color: red;
	font-weight: bold;
	font-size: large;
}

table,td,th {
	border-style: solid;
	border-color: black;
	border-width: thin; 
}
      			</style>
			</head>
			<body>
				<h1>Unit Test Results</h1>
				<h2>Overview</h2>
				<xsl:call-template name="overview"/>
				<h2>Detail</h2>
				<xsl:call-template name="detail"/>
				<xsl:if test="//testcase[error or failure]">
					<h2>Failures and Errors</h2>
					<xsl:call-template name="detail2"/>
				</xsl:if>
			</body>
		</html>
	</xsl:template>
	
	<xsl:template name="overview">
        <xsl:variable name="errors" select="sum(//testsuite/@errors)"/>
        <xsl:variable name="failures" select="sum(//testsuite/@failures)"/>
		<xsl:variable name="tests" select="sum(//testsuite/@tests)"/>
        <xsl:variable name="time" select="sum(//testsuite/@time)"/>
        <xsl:if test="$errors &gt; 0 or $failures &gt; 0">
        	<div class="warning">
        	Build Is Unstable!!
        	</div>
        </xsl:if>
        <table cellspacing="0">
        	<tr>
        		<th>Tests</th>
        		<th>Errors</th>
        		<th>Failures</th>
        		<th>Time</th>
        	</tr>
        	<tr>
        		<td><xsl:value-of select="$tests"/></td>
        		<td>
        			<xsl:if test="$errors &gt; 0">
        				<xsl:attribute name="class">error</xsl:attribute>
        			</xsl:if>
        			<xsl:value-of select="$errors"/>
        		</td>
        		<td>
        			<xsl:if test="$failures &gt; 0">
        				<xsl:attribute name="class">failure</xsl:attribute>
        			</xsl:if>
        			<xsl:value-of select="$failures"/>
        		</td>
        		<td align="right"><xsl:value-of select="$time"/></td>
        	</tr>
        </table>
	</xsl:template>
	
	<xsl:template name="detail">
		<xsl:for-each select="//testsuite">
			<h3><xsl:value-of select="@name"/></h3>
			<table width="100%" cellspacing="0">
				<tr>
					<th>Class</th>
					<th>Name</th>
					<th>Time</th>
					<th>Status</th>
				</tr>
				<xsl:for-each select="testcase">
					<xsl:sort select="@classname"/>
					<xsl:sort select="@name"/>
					<tr>
	        			<xsl:if test="failure">
	        				<xsl:attribute name="class">failure</xsl:attribute>
	        			</xsl:if>
	        			<xsl:if test="error">
	        				<xsl:attribute name="class">error</xsl:attribute>
	        			</xsl:if>
						<td><xsl:value-of select="@classname"/></td>
						<td><xsl:value-of select="@name"/></td>
						<td><xsl:value-of select="format-number(@time,'0.000')"/></td>
						<td>
							<xsl:choose>
					            <xsl:when test="failure">Failure</xsl:when>
					            <xsl:when test="error">Error</xsl:when>
					            <xsl:otherwise>Success</xsl:otherwise>
					        </xsl:choose>
						</td>
					</tr>
				</xsl:for-each>
			</table>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="detail2">
		<xsl:for-each select="//testcase[error or failure]">
			<xsl:sort select="@classname"/>
			<xsl:sort select="@name"/>
			<h3>
				<xsl:choose>
		            <xsl:when test="failure">Failure</xsl:when>
		            <xsl:when test="error">Error</xsl:when>
		            <xsl:otherwise>Success</xsl:otherwise>
		        </xsl:choose>
		        Detail: 
				<xsl:value-of select="@classname"/>.<xsl:value-of select="@name"/>
			</h3>
			<xsl:apply-templates select="error"/>
			<xsl:apply-templates select="failure"/>		
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template match="error">
		<span class="error">Error: <xsl:value-of select="@message"/></span><br/>
		<pre>
			<xsl:value-of select="."/>
		</pre>
	</xsl:template>
	
	<xsl:template match="failure">
		<span class="failure">Failure: <xsl:value-of select="@message"/></span><br/>
		<pre>
			<xsl:value-of select="."/>
		</pre>
	</xsl:template>
</xsl:stylesheet>
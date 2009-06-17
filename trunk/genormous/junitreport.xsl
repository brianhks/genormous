<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output indent="yes" method="xml" standalone="yes" omit-xml-declaration="yes"/>


<xsl:template match="test_results">

<depunit>

<xsl:for-each select="run">

<testsuite errors="0" time="5.0">
	<xsl:attribute name="tests">
		<xsl:value-of select="@total"/>
	</xsl:attribute>
	<xsl:attribute name="failed">
		<xsl:value-of select="@failed"/>
	</xsl:attribute>
	<xsl:attribute name="name">
		<xsl:value-of select="@name"/>
	</xsl:attribute>

<xsl:for-each select="test">
		<testcase classname="{@class}" name="{@method}" time="1.0">
		
		<xsl:if test="@status = 'failed'">
			<error message="{error/message}" type="{error/stack/trace[1]/@className}">
				<xsl:for-each select="error/stack/trace">
					at <xsl:value-of select="@print"/><xsl:text>
</xsl:text>
				</xsl:for-each>
			</error>
		</xsl:if>
		
		</testcase>
</xsl:for-each>
		
</testsuite>

</xsl:for-each>

</depunit>

</xsl:template>

</xsl:stylesheet>

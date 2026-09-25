<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="3.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:p="http://univ.fr/sepa26"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:fn="http://www.w3.org/2005/xpath-functions"
    exclude-result-prefixes="p xs fn">

    <xsl:output method="html" encoding="UTF-8" doctype-system="about:legacy-compat"/>

    <xsl:template match="/">
        <html lang="fr">
            <head>
                <meta charset="UTF-8"/>
                <title>Transactions SEPA</title>
                <link rel="stylesheet" type="text/css" href="/sepa.css"/>
            </head>
            <body>

                <header>
                    <h1>Transactions SEPA</h1>
                    <p class="date-emission">
                    	<xsl:text>Date émission : </xsl:text>
                    	<xsl:call-template name="format-date">
					        <xsl:with-param name="date" select="string(current-date())"/>
					    </xsl:call-template>
                    </p>
                    <ol class="liste-transaction">
                    	<xsl:for-each select="//p:DrctDbtTxInf">
                    		<xsl:sort select="number(p:InstdAmt)" order="descending" data-type="number"/>
                    		<li>
                    			<xsl:value-of select="concat('montant=', p:InstdAmt, '  ', p:InstdAmt/@Ccy,'référence:', p:PmtId)"/>
                    		</li>
                    	</xsl:for-each>
                    </ol>
                </header>

                <main>
                    <xsl:apply-templates select="//p:DrctDbtTxInf"/>
                </main>

                <footer>
                    <p><em>Document émis par Nom Prénom</em></p>
                </footer>

            </body>
        </html>
    </xsl:template>

    <xsl:template match="p:DrctDbtTxInf">
        <section class="transaction">
            <h2>
                <xsl:value-of select="concat('Transaction ', position(), '/', last(), ' : ', p:PmtId)"/>
            </h2>

            <div class="tx-bloc">
                <xsl:call-template name="section-principale"/>
                <xsl:call-template name="section-debiteur"/>
                <xsl:call-template name="section-comment"/>
            </div>
        </section>
    </xsl:template>

    <xsl:template name="section-principale">
        <table class="info-table">
            <tbody>
                <tr>
                    <td class="label">Montant</td>
                    <td>
                    	<span class="montant"><xsl:value-of select="concat(p:InstdAmt, ' ', p:InstdAmt/@Ccy)"/></span>
                    </td>
                </tr>
                <tr>
                    <td class="label">Date</td>
                    <td>
                        <xsl:call-template name="format-date">
                            <xsl:with-param name="date" select="p:DrctDbtTx/p:MndtRltdInf/p:DtOfSgntr"/>
                        </xsl:call-template>
                    </td>
                </tr>
                <tr>
                    <td class="label">Mandat</td>
                    <td>
                        <xsl:value-of select="p:DrctDbtTx/p:MndtRltdInf/p:MndtId"/>
                    </td>
                </tr>
            </tbody>
        </table>
    </xsl:template>

    <xsl:template name="section-debiteur">
        <h3>Débiteur</h3>
        <table class="info-table">
            <tbody>
                <tr>
                    <td class="label">Nom</td>
                    <td><xsl:value-of select="p:Dbtr/p:Nm"/></td>
                </tr>
                <tr>
                    <td class="label">
					    <xsl:choose>
					        <xsl:when test="p:DbtrAgt/p:FinInstnId/p:BIC">BIC</xsl:when>
					        <xsl:otherwise>Agent</xsl:otherwise>
					    </xsl:choose>
					</td>
					<td>
						<xsl:choose>
							<xsl:when test="p:DbtrAgt/p:FinInstnId/p:BIC">
								<xsl:value-of select="p:DbtrAgt/p:FinInstnId/p:BIC"/>
							</xsl:when>
					        <xsl:otherwise>
					            <span class="non-normalise">
					                <xsl:value-of select="p:DbtrAgt/p:FinInstnId/p:Othr/p:Id"/>
					            </span>
					        </xsl:otherwise>
						</xsl:choose>
					</td>
                </tr>
                
                <tr>
                    <td class="label">
					    <xsl:choose>
					    	<xsl:when test="p:DbtrAcct/p:Id/p:IBAN">IBAN</xsl:when>					    	
					    	<xsl:otherwise>Compte</xsl:otherwise>
					    </xsl:choose>
					</td>
					<td>
						<xsl:choose>
					        <xsl:when test="p:DbtrAcct/p:Id/p:IBAN">
					            <xsl:value-of select="p:DbtrAcct/p:Id/p:IBAN"/>
					        </xsl:when>
					        <xsl:otherwise>
					            <span class="non-normalise">
					                <xsl:value-of select="p:DbtrAcct/p:Id/p:Othr/p:Id"/>
					            </span>
					        </xsl:otherwise>
						</xsl:choose>
					</td>
                </tr>
            </tbody>
        </table>
    </xsl:template>

    <xsl:template name="section-comment">
        <xsl:if test="p:RmtInf and normalize-space(p:RmtInf) != ''">
            <h3>Comment</h3>
            <p><xsl:value-of select="p:RmtInf"/></p>
        </xsl:if>
    </xsl:template>

    <xsl:template name="format-date">
	    <xsl:param name="date"/>
	    <xsl:variable name="year"  select="substring($date, 1, 4)"/>
	    <xsl:variable name="month" select="substring($date, 6, 2)"/>
	    <xsl:variable name="day"   select="number(substring($date, 9, 2))"/>
	
	    <xsl:variable name="monthName">
	        <xsl:choose>
	            <xsl:when test="$month = '01'">Jan</xsl:when>
	            <xsl:when test="$month = '02'">Fév</xsl:when>
	            <xsl:when test="$month = '03'">Mars</xsl:when>
	            <xsl:when test="$month = '04'">Avr</xsl:when>
	            <xsl:when test="$month = '05'">Mai</xsl:when>
	            <xsl:when test="$month = '06'">Juin</xsl:when>
	            <xsl:when test="$month = '07'">Juil</xsl:when>
	            <xsl:when test="$month = '08'">Août</xsl:when>
	            <xsl:when test="$month = '09'">Sep</xsl:when>
	            <xsl:when test="$month = '10'">Oct</xsl:when>
	            <xsl:when test="$month = '11'">Nov</xsl:when>
	            <xsl:when test="$month = '12'">Déc</xsl:when>
	            <xsl:otherwise><xsl:value-of select="$month"/></xsl:otherwise>
	        </xsl:choose>
	    </xsl:variable>
	    <xsl:value-of select="concat($day, '-', $monthName, '-', $year)"/>
	</xsl:template>
    </xsl:stylesheet>
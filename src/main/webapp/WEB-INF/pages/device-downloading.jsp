<jsp:root
  xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:s="/struts-tags"
  xmlns:c="http://java.sun.com/jsp/jstl/core"
  version="2.0">

  <jsp:output doctype-root-element="html" omit-xml-declaration="true"
    doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
    doctype-system="http://www.w3c.org/TR/xhtml1/DTD/xhtml1-strict.dtd" />

	<jsp:directive.page contentType="text/html; charset=ISO-8859-15"/>
  <html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=iso-8859-15" />
    <c:set var="link"><s:url action="device-downloading" /></c:set>
    <meta http-equiv="refresh" content="10;url=${link}"/>
    <title>Writing Progress</title>
  </head>
  
  <body>
  	<table>
  		<tbody>
  			<tr>
  				<th>Device:</th>
  				<td><s:property value="device.fullDescription"/></td>
  			</tr>
  			<s:if test="writingListener == null">
  				<th/>
  				<td>Preparing the device for writing.</td>
  			</s:if>
  			<s:else>
	  			<s:push value="writingListener">
	  				<tr>
	  					<th>Files written:</th>
	  					<td><s:property value="filesWrittenCount"/> of <s:property value="totalFiles"/></td>
	  				</tr>
	  				<tr>
	  					<th>Last file written:</th>
	  					<td>
	  						<s:push value="fileNamesWritten">
	  							<s:if test="empty">
	  								N/A
	  							</s:if>
	  							<s:else>
	  								<s:property value="last"/>
	  							</s:else>
	  						</s:push>
	  					</td>
	  				</tr>
	  			</s:push>
	  		</s:else>
  		</tbody>
  	</table>
  </body>
  </html>
</jsp:root>
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
    <title>Connected Devices</title>
  </head>

  <body>
  	<s:if test="connectedDevices.empty">
  		<p>No unused connected devices were found.</p>
  	</s:if>
  	<s:else>
  		<p>Select a device for downloading:</p>
  		<ul>
  			<s:iterator value="connectedDevices">
  				<li>
  					<c:set var="link">
	  					<s:url action="device-download">
	  						<s:param name="device" value="id"/>
	  					</s:url>
	  				</c:set>
	  				<a href="${link}"><s:property value="fullDescription"/></a>
  				</li>
  			</s:iterator>
  		</ul>
  	</s:else>
  </body>
  </html>
</jsp:root>
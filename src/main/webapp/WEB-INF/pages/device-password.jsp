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
    <title>Password Required</title>
  </head>
  
  <body>
  	<p>Please enter a password for access to <s:property value="device.fullDescription"/>:</p>
  	<s:form action="device-downloading" method="post">
  		<s:hidden name="device"/>
  		<s:password name="password"/>
  		<s:submit/>
  	</s:form>
  </body>
  </html>
</jsp:root>
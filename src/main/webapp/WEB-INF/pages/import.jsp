<jsp:root
  xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:ww="/webwork"
  version="2.0">

  <jsp:output doctype-root-element="html" omit-xml-declaration="true"
    doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
    doctype-system="http://www.w3c.org/TR/xhtml1/DTD/xhtml1-strict.dtd" />

  <jsp:directive.page contentType="text/html" />
  <html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Import</title>
  </head>

  <body>
  	<p>Please supply a file to import.</p>
  	<ww:form action="doImport" method="post" enctype="multipart/form-data">
  		<ww:file name="rokta"/>
  		<ww:submit/>
  	</ww:form>
  </body>
  </html>
</jsp:root>
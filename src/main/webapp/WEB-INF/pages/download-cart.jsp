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
    <title>Download</title>
  </head>
  
  <body>
  	<p>
  		Please select an encoding to use for the download:
  		<ul>
  			<s:iterator value="encoders">
  				<li>
  					<c:set var="link">
  						<s:url value="/music.zip">
  							<s:param name="encoder" value="id"/>
  						</s:url>
  					</c:set>
  					<a href="${link}"><s:property value="extension"/></a>
  				</li>
  			</s:iterator>
  		</ul>
  	</p>
  </body>
  </html>
</jsp:root>
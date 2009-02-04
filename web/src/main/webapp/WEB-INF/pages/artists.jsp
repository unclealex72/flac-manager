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
    <title>
    	Artists
    </title>
  </head>
  
  <body>
		<ul>
			<s:iterator value="flacArtistBeans">
				<li>
					<c:set var="link">
						<s:url action="albumsforartist">
							<s:param name="flacArtistBean" value="id"/>
						</s:url>
					</c:set>
					<a href="${link}"><s:property value="name"/></a>
				</li>
			</s:iterator>
		</ul>
  </body>
  </html>
</jsp:root>
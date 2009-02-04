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
    	Albums
    </title>
  </head>
  
  <body>
  	<s:iterator value="albumBeansWithSelectedCoverByArtistBean.entrySet()">
  		<h1><s:property value="key.name"/></h1>
  		<ol>
				<s:iterator value="value">
					<li>
						<s:if test="albumCoverBean != null">
							<s:push value="albumCoverBean">
								<c:set var="img">
									<s:url value="/thumbnail/%{id}.png" includeParams="none"/>
								</c:set>
								<img src="${img}" align="middle"/>
							</s:push>
						</s:if>
						<s:push value="flacAlbumBean">
						  <c:set var="link">
						  	<s:url action="album">
						  		<s:param name="flacAlbumBean" value="id"/>
						  	</s:url>
						  </c:set>
						  <a href="${link}"><s:property value="title"/></a>
					  </s:push>
					</li>
				</s:iterator>
			</ol>
  	</s:iterator>
  </body>
  </html>
</jsp:root>
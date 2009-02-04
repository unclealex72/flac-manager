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
    	<s:property value="flacAlbumBean.flacArtistBean.name"/>, <s:property value="flacAlbumBean.title"/>
    </title>
  </head>
  
  <body>
  	<s:if test="%{!albumCoverBeans.isEmpty()}">
	  	<table>
	  		<tr>
	  			<th>Picture</th>
	  			<th>URL</th>
	  		</tr>
		  	<s:iterator value="albumCoverBeans">
					<tr>
						<td>
							<c:set var="img">
								<s:url value="/cover/%{id}.%{extension}" includeParams="none"/>
							</c:set>
							<c:set var="link">
								<s:url action="select">
									<s:param name="albumCoverBean" value="%{id}"/>
									<s:param name="flacAlbumBean" value="%{flacAlbumBean.id}"/>
								</s:url>
							</c:set>
							<a href="${link}"><img src="${img}"/></a>
						</td>
						<td><s:property value="url"/></td>
					</tr>
		  	</s:iterator>
	  	</table>
	  </s:if>
	  <s:else>
	  	<p>No pictures exist.</p>
	  </s:else>
	  	<s:form action="download" theme="xhtml" method="post">
	  		<s:hidden name="flacAlbumBean" value="%{flacAlbumBean.id}"/>
	  		<s:hidden name="albumCoverBean" value="%{albumCoverBean.id}"/>
	  		<s:textfield name="url" label="Download"/>
	  		<s:submit/>
	  	</s:form>
	  	<s:form action="upload" theme="xhtml" method="post" enctype="multipart/form-data">
	  		<s:hidden name="flacAlbumBean" value="%{flacAlbumBean.id}"/>
	  		<s:hidden name="albumCoverBean" value="%{albumCoverBean.id}"/>
	  		<s:file name="file" label="Upload"/><s:submit/>
	  	</s:form>
  </body>
  </html>
</jsp:root>
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
    	<s:property value="letter"/> : <s:property value="flacAlbum.flacArtistBean.name"/> :
    	<s:property value="flacAlbum.title"/>
    </title>
  </head>
  
  <body>
  	<p>
  		<s:form action="addtracks" method="get">
  			<s:hidden name="letter"/>
  			<s:hidden name="flacAlbum"/>
  			<table border="0">
  				<tr>
  					<td><s:submit value="Add"/></td><td/>
  				</tr>	
  				<s:iterator value="flacTrackBeans">
	  				<tr>
	  					<td>
	  						<c:set var="id"><s:property value="id"/></c:set>
	  						<input type="checkbox" name="items" value="${id}" />
	  					</td>
		  				<td>
		  					<c:set var="link">
			  					<s:url action="addtracks" includeParams="get">
			  						<s:param name="items" value="id"/>
			  					</s:url>
			  				</c:set>
			  				<a href="${link}"><s:property value="title"/></a>
		  				</td>
	  				</tr>
  				</s:iterator>
  				<tr>
  					<td><s:submit value="Add"/></td><td/>
  				</tr>	
  			</table>
  		</s:form>
  	</p>
  </body>
  </html>
</jsp:root>
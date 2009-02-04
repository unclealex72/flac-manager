<jsp:root
  xmlns:jsp="http://java.sun.com/JSP/Page"
  xmlns:decorator="http://www.opensymphony.com/sitemesh/decorator"
  xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:s="/struts-tags"
  version="2.0">

  <jsp:output doctype-root-element="html" omit-xml-declaration="true"
    doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
    doctype-system="http://www.w3c.org/TR/xhtml1/DTD/xhtml1-strict.dtd" />

  <jsp:directive.page contentType="text/html" />
  <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">

<s:head>
  <title>Album Covers - <decorator:title/></title>
  <decorator:head/>
  <meta http-equiv="content-type" content="text/html; charset=iso-8859-1" />

  <!-- **** layout stylesheet **** -->
  <c:url var="link" value="/style/style.css"/>
  <link rel="stylesheet" type="text/css" href="${link}" />

  <!-- **** colour scheme stylesheet **** -->
  <c:url var="link" value="/style/blue.css"/>
  <link rel="stylesheet" type="text/css" href="${link}" />

</s:head>

<body>
  <div id="main">
    <div id="links_container">
      <div id="logo"><h1>Flac</h1><h2><decorator:title/></h2></div>
      <div id="links">
        <!-- **** INSERT LINKS HERE **** -->
        <a href="#">another link</a> | <a href="#">another link</a> | <a href="#">another link</a> | <a href="#">another link</a>
      </div>
    </div>
    <div id="menu">
      <ul>
        <!-- **** INSERT NAVIGATION ITEMS HERE (use id="selected" to identify the page you're on **** -->
        <li>
        	<c:set var="link">
        		<s:url action="missing" namespace="/html" includeParams="none"/>
        	</c:set>
        	<a href="${link}">Missing Covers</a>
        </li>        
        <li>
        	<c:set var="link">
        		<s:url action="artists" namespace="/html" includeParams="none"/>
        	</c:set>
        	<a href="${link}">All Artists</a>
        </li>
				<s:if test="flacAlbumBean != null">
					<li>
						<s:push value="flacAlbumBean.flacArtistBean">
		        	<c:set var="link">
		        		<s:url action="albumsforartist" namespace="/html" includeParams="none">
		        			<s:param name="flacArtistBean" value="%{id}"/>
		        		</s:url>
		        	</c:set>
		        	<a href="${link}"><s:property value="%{name}"/></a>
	        	</s:push>
					</li>
				</s:if>
      </ul>
    </div>
    <div id="content">
      <div id="column1">
      	<p/>
      </div>
      <div id="column2">
        <decorator:body/>
      </div>
    </div>
    <div id="footer">
    	
      copyright
      <c:set var="currentYear"><s:date name="%{new java.util.Date()}" format="yyyy"/></c:set>
      <s:if test="%{#page.currentYear == '2007'}">
      	2007
      </s:if>
      <s:else>
      	2007 - <s:property value="%{#page.currentYear}"/>
      </s:else>
      Alex Jones |
      <a href="mailto:alex.jones@unclealex.co.uk">alex.jones@unclealex.co.uk</a> |
      <a href="http://validator.w3.org/check?uri=referer">XHTML 1.0</a> |
      <a href="http://jigsaw.w3.org/css-validator/check/referer">CSS</a> |
      <a href="http://www.dcarter.co.uk">design by dcarter</a>
    </div>
  </div>
</body>
</html>
</jsp:root>
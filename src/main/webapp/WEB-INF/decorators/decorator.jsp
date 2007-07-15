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

<head>
  <title>Flac - <decorator:title/></title>
  <decorator:head/>
  <meta http-equiv="content-type" content="text/html; charset=iso-8859-1" />

  <!-- **** layout stylesheet **** -->
  <c:url var="link" value="/style/style.css"/>
  <link rel="stylesheet" type="text/css" href="${link}" />

  <!-- **** colour scheme stylesheet **** -->
  <c:url var="link" value="/style/blue.css"/>
  <link rel="stylesheet" type="text/css" href="${link}" />

</head>

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
        	<c:set var="link"><s:url action="devices"/></c:set>
        	<a id="selected" href="${link}">devices</a>
        </li>
        <li><a href="#">page 1</a></li>
        <li><a href="#">page 2</a></li>
        <li><a href="#">page 3</a></li>
        <li><a href="#">contact</a></li>
      </ul>
    </div>
    <div id="content">
      <div id="column1">
        <div class="sidebaritem">
        	<h1>Your download cart</h1>
       		<s:if test="#downloadCart == null">
       		</s:if>
       		<s:else>
       			<ul>
        			<s:iterator value="#downloadCart.entrySet()">
        				<li>
        					<!-- Artist -->
        					<s:property value="key.name"/>
        					<s:if test="value != null">
        						<ul>
        							<s:iterator value="value.entrySet()">
        								<li>
        									<!-- Album -->
        									<s:property value="key.name"/>
        									<s:if test="value != null">
        										<ul>
        											<s:iterator value="value">
        												<li>
        													<!-- Track -->
        													<s:property/>
        												</li>
        											</s:iterator>
        										</ul>
        									</s:if>
        								</li>
        							</s:iterator>
        						</ul>
        					</s:if>
        				</li>
 	      			</s:iterator>
       			</ul>
       		</s:else>
        </div>
      </div>
      <div id="column2">
        <decorator:body/>
      </div>
    </div>
    <div id="footer">
      copyright 2007 your name | <a href="#">email@emailaddress</a> | <a href="http://validator.w3.org/check?uri=referer">XHTML 1.1</a> | <a href="http://jigsaw.w3.org/css-validator/check/referer">CSS</a> | <a href="http://www.dcarter.co.uk">design by dcarter</a>
    </div>
  </div>
</body>
</html>
</jsp:root>
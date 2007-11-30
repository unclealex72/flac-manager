<jsp:root
  xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:ww="/webwork"
  xmlns:decorator="http://www.opensymphony.com/sitemesh/decorator"
  xmlns:c="http://java.sun.com/jsp/jstl/core"
  version="2.0">

  <jsp:output doctype-root-element="html" omit-xml-declaration="true"
    doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
    doctype-system="http://www.w3c.org/TR/xhtml1/DTD/xhtml1-strict.dtd" />

  <jsp:directive.page contentType="text/html" />
  <html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=iso-8859-1" />
    <title>Login</title>
  </head>

  <body>
  	<c:url var="url" value="/process.html"/>
    <ww:form action="%{#attr.url}" method="post" theme="xhtml">
    	<ww:actionerror/>
			<ww:select list="players" listKey="name" listValue="name" label="Player" name="j_username"/>
			<ww:password label="Password" name="j_password"/>
			<ww:submit value="Login"/>
    </ww:form>
  </body>
  </html>
</jsp:root>
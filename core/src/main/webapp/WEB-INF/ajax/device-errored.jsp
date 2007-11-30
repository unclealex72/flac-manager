<jsp:root
  xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:s="/struts-tags"
  xmlns:c="http://java.sun.com/jsp/jstl/core"
  version="2.0">
 	<p>There was an error whilst writing to <s:property value="device.fullDescription"/>.</p>
 	<p><s:property value="errorMessage"/></p>
 	<p>The following stack trace was produced:</p>
 	<pre><s:property value="stackTrace"/></pre>
</jsp:root>
<jsp:root
  xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:s="/struts-tags"
  xmlns:c="http://java.sun.com/jsp/jstl/core"
  version="2.0">

	<script type="text/javascript">
		dojo.event.topic.publish("/finished", {});	
	</script>
 	<p>
 		The application has finished writing to <s:property value="device.fullDescription"/>. It can now be
 		safely removed.
 	</p>
</jsp:root>
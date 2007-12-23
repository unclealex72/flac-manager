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

<s:head theme="ajax">
  <title>Music - <decorator:title/></title>
  <decorator:head/>
  <meta http-equiv="content-type" content="text/html; charset=iso-8859-1" />

  <!-- **** layout stylesheet **** -->
  <c:url var="link" value="/style/style.css"/>
  <link rel="stylesheet" type="text/css" href="${link}" />

  <!-- **** colour scheme stylesheet **** -->
  <c:url var="link" value="/style/blue.css"/>
  <link rel="stylesheet" type="text/css" href="${link}" />

	<script type="text/javascript">
		function removeArtist(id) {
			updateCart("<s:url action='removeartist' namespace='/ajax' includeParams='none'/>", "encodedArtist", id);
		}

		function removeAlbum(id) {
			updateCart("<s:url action='removealbum' namespace='/ajax' includeParams='none'/>", "encodedAlbum", id);
		}

		function removeTrack(id) {
			updateCart("<s:url action='removetrack' namespace='/ajax' includeParams='none'/>", "encodedTrack", id);
		}
	
		function clearCart(id) {
			updateCart("<s:url action='clearcart' namespace='/ajax' includeParams='none'/>", null, null);
		}

	  function updateCart(actionUrl, paramName, paramValue) {
  			var bindArgs = {
	   			url: actionUrl + (paramName==null?"":("?" + paramName + "=" + paramValue)),
	   			error: function(type, data, evt){
	    				alert("An error occurred removing files from the cart: " + data.message);
	   			},
	   			load: function(type, data, evt){
	    				document.getElementById('cartContainer').innerHTML = data;
	   			},
	   			mimetype: "text/html"
	  		};
  			dojo.io.bind(bindArgs);
 		}
		
	</script>
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
        <s:iterator value="startingCharacters">
        <li>
        	<c:set var="link">
        		<s:url action="artists" includeParams="none">
        			<s:param name="letter" value="top"/>
        		</s:url>
        	</c:set>
        	<s:if test="top == letter">
        		<a href="${link}" id="selected"><s:property/></a>
        	</s:if>
        	<s:else>
        		<a href="${link}"><s:property/></a>
        	</s:else>
        </li>        
        </s:iterator>
      </ul>
    </div>
    <div id="content">
      <div id="column1">
        <div id="cartContainer" class="sidebaritem">
        	<jsp:include page="/WEB-INF/ajax/cart.jsp"/>
        </div>
        <div class="sidebaritem">
          <h1>Connected Devices</h1>
			  	<s:if test="connectedDevices.empty">
			  		<p>No unused connected devices were found.</p>
			  	</s:if>
			  	<s:else>
			  		<p>Select a device for downloading:</p>
			  		<ul>
			  			<s:iterator value="connectedDevices">
			  				<li>
			  					<c:set var="link">
				  					<s:url action="device-download" includeParams="none">
				  						<s:param name="devices" value="id"/>
				  					</s:url>
				  				</c:set>
				  				<a href="${link}"><s:property value="fullDescription"/></a>
			  				</li>
			  			</s:iterator>
			  		</ul>
			  		<p>
			  			<c:set var="link">
			  				<s:url action="device-download" includeParams="none"/>
			  			</c:set>
			  			<a href="${link}">Download to all</a>
			  		</p>
			  	</s:else>
			  	<c:set var="link"><s:url action="manage-devices" includeParams="none"/></c:set>
			  	<p><a href="${link}">Manage devices</a></p>
        </div>
        <div class="sidebaritem">
        	<h1>Owners</h1>
        	<s:push value="owners">
	        	<s:if test="owners.empty">
	        		<p>There are no owners.</p>
	        	</s:if>
	        	<s:else>
	        		<ul>
	        			<s:iterator>
	        				<li>
	        					<c:set var="link">
	        						<s:url action="edit-owner" includeParams="none">
	        							<s:param name="owner" value="id"/>
	        						</s:url>
	        					</c:set>
	        					<a href="${link}"><s:property value="name"/></a>
	        				</li>
	        			</s:iterator>
	        		</ul>
	        	</s:else>
        	</s:push>
        	<p>
        		<c:set var="link"><s:url action="manage-owners" includeParams="none"/></c:set>
        		<a href="${link}">Manage owners</a>
        	</p>
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
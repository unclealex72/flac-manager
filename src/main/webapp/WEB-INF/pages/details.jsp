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
    <title>User details</title>
  </head>

  <body>
  
  	<h1>Change Password</h1>
  
    <ww:form action="changepassword" method="post" theme="xhtml">
			<ww:password label="Current password" name="currentPassword"/>
			<ww:password label="New password" name="password"/>
			<ww:password label="Retype your new password" name="retypePassword"/>
			<ww:submit value="Go"/>
    </ww:form>
    
    <h1>Graphing colour</h1>
    
    <ww:form action="changedetails" namespace="/edit" method="post">
      <ww:push value="person">
        <table>
          <tr>
            <td>
              <ww:select
                id="colourSelect" value="colour.htmlName" onchange="setColour()"
                list="allColours" listKey="htmlName" listValue="name" name="colour"/>
            </td>
            <td id="colourBlock"></td>
          </tr>
          <tr>
            <td>
              <ww:submit value="Change colour"/>
            </td>
          </tr>
        </table>
      </ww:push>
    </ww:form>

    <script type="text/javascript">
      function setColour() {
        var colourSelect = document.getElementById('colourSelect');
        var colourBlock = document.getElementById('colourBlock');
        
        colourBlock.style.backgroundColor = colourSelect.value;
      }
      
      setColour();
    </script>
    
    <h1>Game Administration</h1>
    
    <c:set var="link">
    	<ww:url action="replay" namespace="/edit"/>
    </c:set>
    <a href="${link}">Replay last game</a>
  </body>
  </html>
</jsp:root>
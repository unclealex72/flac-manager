<jsp:root
  xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:ww="/webwork"
  xmlns:decorator="http://www.opensymphony.com/sitemesh/decorator"
  version="2.0">

  <jsp:output doctype-root-element="html" omit-xml-declaration="true"
    doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
    doctype-system="http://www.w3c.org/TR/xhtml1/DTD/xhtml1-strict.dtd" />

  <jsp:directive.page contentType="text/html" />
  <html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=iso-8859-1" />
    <title>Get Ready To Play</title>
  </head>

  <body>
    <h1>Select the players</h1>
    <ww:form action="start" method="post" theme="simple">
      <p>
        <ww:if test="exempt != null">
          <ww:property value="exempt"/>
        </ww:if>
        <ww:else>
          No-one
        </ww:else>
        is exempt.
      </p>
      <table>
        <tr>
          <td>
            Instigator
          </td>
          <td>
            <ww:select name="instigator" list="everybody"
                listKey="name" listValue="name" multiple="false" required="false" />
          </td>
        </tr>
        <tr>
          <td>
            Participants
          </td>
          <td>
            <ww:select label="Participants" name="participants" list="availablePlayers"
                listKey="name" listValue="name" multiple="true" required="false" size="%{players.size()}"/>
          </td>
        </tr>
        <tr>
          <td colspan="2">
            <input type="submit" value="Play"/>
          </td>
        </tr>
      </table>
    </ww:form>
  </body>
  </html>
</jsp:root>
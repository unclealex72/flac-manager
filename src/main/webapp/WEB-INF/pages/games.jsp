<jsp:root
  xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:ww="/webwork"
  version="2.0">

  <jsp:output doctype-root-element="html" omit-xml-declaration="true"
    doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
    doctype-system="http://www.w3c.org/TR/xhtml1/DTD/xhtml1-strict.dtd" />

  <jsp:directive.page contentType="text/html" />
  <html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>All Games</title>
  </head>

  <body>
    <ww:iterator id="game" value="gameViews">
      <h1>
        <ww:date format="EEEE, d MMMM yyyy HH:mm" name="datePlayed"/>
      </h1>
      <h2>Instigated by <ww:property value="instigator" /></h2>
      <h2>Lost by <ww:property value="loser" /></h2>
      <table>
        <tr>
          <th>Counter</th>
          <ww:iterator id="participant" value="participants">
            <th><ww:property value="name" /></th>
          </ww:iterator>
        </tr>
        <ww:iterator id="round" value="rounds">
          <tr>
            <td><ww:property value="counter" /></td>
            <ww:iterator id="hand" value="hands">
              <td><ww:property value="hand" /></td>
            </ww:iterator>
          </tr>
        </ww:iterator>
      </table>
    </ww:iterator>
  </body>
  </html>
</jsp:root>
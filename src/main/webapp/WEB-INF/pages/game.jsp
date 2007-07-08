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
    <title>Round <ww:property value="currentRound"/></title>
  </head>

  <body>
    <h1>Round <ww:property value="currentRound"/></h1>
    <ww:form action="round" method="post" theme="simple">
      <ww:hidden name="currentRound" value="%{currentRound}"/>
      <ww:hidden name="gameManager" value="%{gameManager}"/>
      <table>
        <tr>
          <td>
            Counter
          </td>
          <td>
            <ww:select
                  label="Counter"
                  name="counter"
                  value="%{counter.name}"
                  list="everybody"
                  listKey="name"
                  listValue="name"
                  multiple="false"
                  required="false"/>
          </td>
        </tr> 
        <ww:iterator id="participant" value="participants">
          <tr>
            <td>
              <ww:property/>
            </td>
            <td>
              <ww:select
                    name="hands"
                    value="ROCK"
                    list="allHands"
                    listValue="description"
                    multiple="false"
                    required="false"/>
            </td>
          </tr>
        </ww:iterator>
        <tr>
          <td colspan="2"><input type="submit" value="Next"/></td>
        </tr>
      </table>
      <ww:iterator id="participant" value="participants">
        <ww:hidden name="participants" value="%{participant}"/>
      </ww:iterator>
      <ww:if test="replacingGameId != null">
      	<ww:hidden name="replacingGameId"/>
      </ww:if>
    </ww:form>
  </body>
  </html>
</jsp:root>
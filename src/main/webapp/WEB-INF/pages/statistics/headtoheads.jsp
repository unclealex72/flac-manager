<jsp:root
  xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:ww="/webwork"
  xmlns:fmt="http://java.sun.com/jstl/fmt"
  xmlns:c="http://java.sun.com/jstl/core"
  version="2.0">

  <jsp:output doctype-root-element="html" omit-xml-declaration="true"
    doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
    doctype-system="http://www.w3c.org/TR/xhtml1/DTD/xhtml1-strict.dtd" />

  <jsp:directive.page contentType="text/html" />
  <html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=iso-8859-1" />
    <title>
      Head to head results
    </title>
  </head>

  <body>
    <h1>
      Head To head results
    </h1>
    <table class="data">
      <tr>
        <th>Winner/Loser</th>
        <ww:iterator value="players">
          <th><ww:property value="name"/></th>
        </ww:iterator>
      </tr>
      <ww:iterator id="winner" value="players">
        <tr>
          <th><ww:property value="name"/></th>
          <ww:set name="headToHeadResults" value="%{headToHeadResultsByPerson[top]}"/>
          <ww:iterator id="loser" value="players">
            <ww:if test="%{#headToHeadResults[top] != null}">
              <ww:push value="%{#headToHeadResults[top]}">
                <c:set var="tooltip" scope="page">
                	<ww:property value="%{#winner.name + ': ' + winCount + ', ' + #loser.name + ': ' + lossCount}"/>
                </c:set>
                <td title="${tooltip}">
                  <fmt:formatNumber type="percent" minFractionDigits="2" maxFractionDigits="2">
                    <ww:property value="winRatio"/>
                  </fmt:formatNumber>
                </td>
              </ww:push>
            </ww:if>
            <ww:else>
              <td>-</td>
            </ww:else>       
          </ww:iterator>
        </tr>
      </ww:iterator>
    </table>
  </body>
  </html>
</jsp:root>
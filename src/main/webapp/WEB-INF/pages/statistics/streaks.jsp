<jsp:root
  xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:ww="/webwork"
  xmlns:decorator="http://www.opensymphony.com/sitemesh/decorator"
  xmlns:cewolf="http://cewolf.sourceforge.net/taglib/cewolf.tld"
  version="2.0">

  <jsp:output doctype-root-element="html" omit-xml-declaration="true"
    doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
    doctype-system="http://www.w3c.org/TR/xhtml1/DTD/xhtml1-strict.dtd" />

  <jsp:directive.page contentType="text/html" />
  <html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=iso-8859-1" />
    <title>
      <ww:text name="title"/>
    </title>
  </head>

  <body>
    <h1>
      <ww:text name="top">
        <ww:param name="size" value="topStreaks.size"/>
      </ww:text>
    </h1>
    <table class="data">
      <tr>
        <th>Rank</th>
        <th>Player</th>
        <th>Games</th>
        <th>From</th>
        <th>To</th>
      </tr>
      <ww:set name="previousLength" value="0"/>
      <ww:iterator value="topStreaks" status="status">
        <tr>
          <td>
            <ww:if test="#previousLength == length">
              =
            </ww:if>
            <ww:else>
              <ww:property value="#status.count"/>.
            </ww:else>
          </td>
          <td><ww:property value="person.name"/></td>
          <td><ww:property value="length"/></td>
          <td><ww:date name="firstGame.datePlayed" format="dd/MM/yyyy HH:mm"/></td>
          <td><ww:date name="lastGame.datePlayed" format="dd/MM/yyyy HH:mm"/></td>
        </tr>
        <ww:set name="previousLength" value="length"/>
      </ww:iterator>
    </table>

    <ww:if test="!currentStreaks.empty">
      <h1><ww:text name="current"/></h1>    
      <table class="data">
        <tr>
          <th>Player</th>
          <th>Games</th>
          <th>From</th>
          <th>To</th>
        </tr>
        <ww:iterator value="currentStreaks">
          <tr>
            <td><ww:property value="person.name"/></td>
            <td><ww:property value="length"/></td>
            <td><ww:date name="firstGame.datePlayed" format="dd/MM/yyyy HH:mm"/></td>
            <td><ww:date name="lastGame.datePlayed" format="dd/MM/yyyy HH:mm"/></td>
          </tr>
        </ww:iterator>
      </table>
    </ww:if>
  </body>
  </html>
</jsp:root>
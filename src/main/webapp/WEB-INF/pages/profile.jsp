<jsp:root
  xmlns:jsp="http://java.sun.com/JSP/Page"
  xmlns:ww="/webwork"
  xmlns:fmt="http://java.sun.com/jstl/fmt"
  xmlns:c="http://java.sun.com/jstl/core"
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
      <ww:property value="person.name"/>'s profile
    </title>
  </head>

  <body>
    <h1>Head to head record</h1>
    <ww:if test="%{true}">
      <table class="data">
        <tr>
          <th>Opponent</th>
          <th>Won</th>
          <th>Lost</th>
          <th>Percentage</th>
        </tr>
        <ww:iterator id="entry" value="headToHeadRoundWinRate.entrySet()">
           <ww:push value="#entry.value">
             <tr>
               <td>
                 <ww:property value="#entry.key.name"/>
               </td>
               <td>
                 <ww:property value="%{winCount}"/>
               </td>
               <td>
                 <ww:property value="%{lossCount}"/>
               </td>
               <td>
                 <fmt:formatNumber type="percent" minFractionDigits="2" maxFractionDigits="2">
                   <ww:property value="winRatio"/>
                 </fmt:formatNumber>
               </td>
             </tr>
           </ww:push>
        </ww:iterator>
      </table>
    </ww:if>

    <h1>Graphing colour</h1>
    
    <c:set var="htmlName">
    	<ww:property value="person.colour.htmlName"/>
    </c:set>
    <table>
      <tr>
        <td>
        	<ww:property value="person.colour.name"/>
        </td>
        <td style="background-color: ${htmlName}; width: 100px;"></td>
      </tr>
		</table>    

    <h1>Hand choice distribution</h1>
    
    <ww:set name="handChoiceProducer" value="handChoiceDatasetProducer" scope="page"/>
    <cewolf:chart
      id="handChoiceProducer" 
      title="Hand choice distribution" 
      type="pie3d">
      <cewolf:data>
        <cewolf:producer id="handChoiceProducer"/>
      </cewolf:data>
    </cewolf:chart>
    <cewolf:img chartid="handChoiceProducer" renderer="cewolf" width="480" height="300"/>

    <h1>Opening gambits</h1>
    
    <ww:set name="openingGambitsProducer" value="openingGambitDatasetProducer" scope="page"/>
    <cewolf:chart
      id="openingGambits" 
      title="Opening gambits" 
      type="pie3d">
      <cewolf:data>
        <cewolf:producer id="openingGambitsProducer"/>
      </cewolf:data>
    </cewolf:chart>
    <cewolf:img chartid="openingGambits" renderer="cewolf" width="480" height="300"/>
  </body>
  </html>
</jsp:root>
<jsp:root
  xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:ww="/webwork"
  xmlns:fmt="http://java.sun.com/jstl/fmt"
  xmlns:c="http://java.sun.com/jstl/core"
  xmlns:cewolf="http://cewolf.sourceforge.net/taglib/cewolf.tld"
  xmlns:rokta="/rokta"
  version="2.0">

  <jsp:output doctype-root-element="html" omit-xml-declaration="true"
    doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
    doctype-system="http://www.w3c.org/TR/xhtml1/DTD/xhtml1-strict.dtd" />

  <jsp:directive.page contentType="text/html" />
  <html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=iso-8859-1" />
    <title>
      League
    </title>
  </head>

  <body>
    <h1>League</h1>
    <table class="data">
    	<caption>
				Expected losses per game:
		     <fmt:formatNumber type="percent" minFractionDigits="2" maxFractionDigits="2">
		       <ww:property value="league.expectedLossesPerGame"/>
		     </fmt:formatNumber>
    	</caption>
    	<thead>
	      <tr>
	        <th />
	        <th>Player</th>
	        <th>Games</th>
	        <th>Rounds</th>
	        <th>Lost</th>
	        <th>R/WG</th>
	        <th>R/LG</th>
	        <th>L/G</th>
	        <ww:if test="league.current">
	          <th>Gap</th>
	        </ww:if>
	      </tr>
	    </thead>
      <tbody>
	      <ww:iterator id="row" value="league.rows">
	        <ww:if test="exempt">
	          <ww:set name="class" value="%{'exempt'}" scope="page"/>
	        </ww:if>
	        <ww:elseif test="!playingToday">
	          <ww:set name="class" value="%{'notPlaying'}" scope="page"/>
	        </ww:elseif>
	        <ww:else>
	          <ww:set name="class" value="%{''}" scope="page"/>
	        </ww:else>
	        <ww:if test="league.current">
		        <c:set var="title">
		        	Win next:
		        	<fmt:formatNumber type="percent" minFractionDigits="2" maxFractionDigits="2">
		            <ww:property value="%{gamesLost / (gamesPlayed + 1.0)}"/>
		          </fmt:formatNumber>,
		        	Lose next:
		        	<fmt:formatNumber type="percent" minFractionDigits="2" maxFractionDigits="2">
		            <ww:property value="%{(gamesLost + 1) / (gamesPlayed + 1.0)}"/>
		          </fmt:formatNumber>
						</c:set>	        	
	        </ww:if>
	        <ww:else>
	        	<c:set var="title" value=""/>
	        </ww:else>
	        <tr class="${class}" title="${title}">
	          <td><ww:property value="delta" /></td>
	          <td><ww:property value="person" /></td>
	          <td><ww:property value="gamesPlayed" /></td>
	          <td><ww:property value="roundsPlayed" /></td>
	          <td><ww:property value="gamesLost" /></td>
	          <td>
	            <ww:if test="%{gamesWon == 0}">
	              -
	            </ww:if>
	            <ww:else>
	              <fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2">
	                <ww:property value="roundsPerWonGames"/>
	              </fmt:formatNumber>
	            </ww:else>
	          </td>
	          <td>
	            <ww:if test="%{gamesLost == 0}">
	              -
	            </ww:if>
	            <ww:else>
	              <fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2">
	                <ww:property value="roundsPerLostGames"/>
	              </fmt:formatNumber>
	            </ww:else>
	          </td>
	          <td>
	            <fmt:formatNumber type="percent" minFractionDigits="2" maxFractionDigits="2">
	              <ww:property value="lossesPerGame"/>
	            </fmt:formatNumber>
	          </td>
	          <ww:if test="league.current">
	            <td>
	              <ww:if test="gap != null">
	                <ww:push value="gap">
	                  <ww:if test="infinite">
	                    &#8734;
	                  </ww:if>
	                  <ww:else>                
	                    <ww:property value="value"/>
	                  </ww:else>
	                </ww:push>
	              </ww:if>
	            </td>
	          </ww:if>
	        </tr>
	      </ww:iterator>
	    </tbody>
    </table>

    <h1>Graph</h1>
    
    <ww:set name="leagueGraphProducer" scope="page" value="leagueGraphDatasetProducer"/>
    <c:set var="graphTitle">
      <ww:property value="%{granularityPredicate.granularity.description}"/> results
    </c:set>
    
    <cewolf:chart 
      id="leagueGraph"
      title="${graphTitle}" 
      type="line">
      <cewolf:data>
          <cewolf:producer id="leagueGraphProducer"/>
      </cewolf:data>
    </cewolf:chart>

    <rokta:colourise chartid="leagueGraph"/>
    
    <div id="largeChart">
      <a href="javascript:void(0);" onclick="document.getElementById('largeChart').style.visibility = 'hidden';">
        <cewolf:img chartid="leagueGraph" renderer="cewolf" width="900" height="500"/>
      </a>
    </div>
    <a href="javascript:void(0);" onclick="document.getElementById('largeChart').style.visibility = 'visible';">
      <cewolf:img chartid="leagueGraph" renderer="cewolf" width="480" height="300" />
    </a>
    
  </body>
  </html>
</jsp:root>
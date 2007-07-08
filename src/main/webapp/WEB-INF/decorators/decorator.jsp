<jsp:root
  xmlns:jsp="http://java.sun.com/JSP/Page"
  xmlns:ww="/webwork"
  xmlns:decorator="http://www.opensymphony.com/sitemesh/decorator"
  xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:rokta="/rokta"
  version="2.0">

  <jsp:output doctype-root-element="html" omit-xml-declaration="true"
    doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
    doctype-system="http://www.w3c.org/TR/xhtml1/DTD/xhtml1-strict.dtd" />

  <jsp:directive.page contentType="text/html" />
  <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
  
  <head>
    <title>ROKTA - <decorator:title/></title>
    <meta http-equiv="content-type" content="text/html; charset=iso-8859-1" />

    <!-- Epoch calendar -->  
    <c:url var="link" value="/js/epoch_classes.js"/>
    <script type="text/javascript" src="${link}">
    <![CDATA[
      <!-- Empty comment for IE -->
    ]]>
    </script>

    <c:url var="link" value="/style/epoch_styles.css"/>
    <link rel="stylesheet" type="text/css" href="${link}" />
    
    <!-- **** Layout Stylesheet **** -->
    <c:url var="link" value="/style/style104_left.css" />
    <link rel="stylesheet" type="text/css" href="${link}" />
  
    <!-- **** Colour Scheme Stylesheet **** -->
    <rokta:theme parameterName="colour" cookieName="colour" var="colour" defaultValue="2" validValues="[1-8]"/>
    <c:url var="link" value="/style/colour${colour}.css"/>
    <link rel="stylesheet" type="text/css" href="${link}" />
  
    <!-- **** rokta specific Stylesheet **** -->
    <c:url var="link" value="/style/rokta.css" />
    <link rel="stylesheet" type="text/css" href="${link}" />

    <script type="text/javascript">
      var links = new Array();
      links[0] = 'filterlinks';
      links[1] = 'profilelinks';
      links[2] = 'statisticslinks';
    
      function showLinks(divId) {
        var idx;

        for (idx in links) {
          document.getElementById(links[idx]).style.display = (links[idx] == divId)?'block':'none';
        }
        document.getElementById('addlinks').style.visibility = "visible";
        return false;
      }
      
      function showFilterLinks() {
        showLinks('filterlinks');
      }
      
      function showProfileLinks() {
        showLinks('profilelinks');
      }
      
      function showStatisticsLinks() {
        showLinks('statisticslinks');
      }
      
      var cal;
      var registeredElements = new Array();
      var registeredHtml = new Array();
      
      function register(elId) {
        var el = document.getElementById(elId);
        registeredElements[elId] = elId;
        registeredHtml[elId] = el.innerHTML;
      }
      
      function updateFilters() {
        try {
          var filteredDate = document.getElementById('calendar').value;
          var day = filteredDate.substring(0, 2);
          var month = filteredDate.substring(3, 5);
          var year = filteredDate.substring(6, 10);
          var dt = new Date(year, month - 1, day);
          
          if (dt &lt; cal.minDate || dt &gt; cal.maxDate) {
            throw "Invalid Date";
          }
          
          for (elId in registeredElements) {
            var el = document.getElementById(elId);
            el.innerHTML = makeDate(registeredHtml[elId], dt);
          }
        }
        catch (err) {
          for (elId in registeredElements) {
            var el = document.getElementById(elId);
            el.innerHTML = "";
          }
        }
      }
      
      function makeDate(fmt, dt) {
        var re = /-.+?-/g;
        while ((match = fmt.match(re)) != null) {
          var dtFmt = match[0].substring(1, match[0].length - 1);
          fmt = fmt.replace(match[0], dt.dateFormat(dtFmt));
        }
        return fmt;
      }
      /*put the calendar initializations in the window's onload() method*/
      
      window.onload = function() {
        cal = new Epoch('cal','popup',document.getElementById('calendar'),false);
        cal.minDate = <ww:text name="javascript.date"><ww:param value="%{minimumDate}"/></ww:text>;
        cal.maxDate = <ww:text name="javascript.date"><ww:param value="%{maximumDate}"/></ww:text>;
        cal.onchange = updateFilters;
        var selectedDates = new Array();
        selectedDates[0] = <ww:text name="javascript.date"><ww:param value="%{initialDate}"/></ww:text>;
        cal.selectDates(selectedDates, true, true, true);
        cal.goToMonth(selectedDates[0].getYear() + 1900, selectedDates[0].getMonth());
        register('dt_since');
        register('dt_week');
        register('dt_month');
        register('dt_year');
        updateFilters();
      };
    </script>
  </head>
  
  <body>  
    <div id="main">
      <div id="links">
      	<c:set var="link">
      		<ww:url action="details" namespace="/edit"/>
      	</c:set>
      	<a href="${link}">User details</a>
      	<ww:if test="principalProxy.userPrincipal != null">
      		| <c:url var="link" value="/logout.html"/> <a href="${link}">Logout</a>
      	</ww:if>
      </div>
      <div id="logo">
      	<h1>
      		ROKTA -
      		<decorator:title/>
      		<rokta:space/>
      		<ww:property value="%{gameFilterInternal.description}"/>
      	</h1>
      </div>
      <div id="content">
        <div id="column1">
          <div id="menu">
            <h1>Navigate</h1>
            <ul>
              <li>
                <c:set var="link">
                  <ww:url action="initialise" namespace="/edit"/>
                </c:set>
                <a href="${link}">New game</a>
              </li>
              <li>
                <c:set var="link">
                  <ww:url action="league" namespace="/">
                  	<ww:param name="gameFilter" value="%{gameFilter}"/>
                  </ww:url>
                </c:set>
                <a href="${link}">League</a>
              </li>
              <li><a href="javascript:showFilterLinks()">Filters</a></li>
              <li><a href="javascript:showStatisticsLinks()">Statistics</a></li>
              <li><a href="javascript:showProfileLinks()">Profiles</a></li>
            </ul>
          </div>

          <div id="addlinks">
            <h1>Choose</h1>
            <div id="filterlinks">
              <ul>
                <li>
                  <ww:form id="filtered" namespace="/" action="filteredleague" method="post">
                    <ww:textfield name="filteredDate" id="calendar"/>
                  </ww:form>
                </li>
                <li>
                  <c:set var="link">
                    <ww:url value="%{#request.requestURI}"/>
                  </c:set>
                  <a href="${link}">This year</a>
                </li>
                <li>
                  <c:set var="link">
                    <ww:url value="%{#request.requestURI}">
                      <ww:param name="gameFilter" value="%{'a'}"/>
                    </ww:url>
                  </c:set>
                  <a href="${link}">All games</a>
                </li>
                <li>
                  <rokta:date format="ddMMyyyy" field="WEEK_OF_YEAR" value="-4" var="since"/>
                  <c:set var="link">
                    <ww:url value="%{#request.requestURI}">
                      <ww:param name="gameFilter" value="s%{#attr.since}"/>
                    </ww:url>
                  </c:set>
                  <a href="${link}">Last four weeks</a>
                </li>

                <li id="dt_since">
                  <c:set var="link">
                    <ww:url value="%{#request.requestURI}">
                      <ww:param name="gameFilter" value="%{'s-dmY-'}"/>
                    </ww:url>
                  </c:set>
                  <a href="${link}">Since -d M Y-</a>
                </li>
                <li id="dt_week">
                  <c:set var="link">
                    <ww:url value="%{#request.requestURI}">
                      <ww:param name="gameFilter" value="%{'w-WY-'}"/>
                    </ww:url>
                  </c:set>
                  <a href="${link}">Week -w, Y-</a>
                </li>
                <li id="dt_month">
                  <c:set var="link">
                    <ww:url value="%{#request.requestURI}">
                      <ww:param name="gameFilter" value="%{'m-mY-'}"/>
                    </ww:url>
                  </c:set>
                  <a href="${link}">-F, Y-</a>
                </li>
                <li id="dt_year">
                  <c:set var="link">
                    <ww:url value="%{#request.requestURI}">
                      <ww:param name="gameFilter" value="%{'y-Y-'}"/>
                    </ww:url>
                  </c:set>
                  <a href="${link}">-Y-</a>
                </li>
              </ul>
            </div>
            <div id="profilelinks">
              <ul>
                <ww:iterator id="player" value="players">
                  <li>
                    <c:set var="link">
                      <ww:url action="profile" namespace="/">
                        <ww:param name="person" value="name"/>
                        <ww:param name="gameFilter" value="%{gameFilter}"/>
                      </ww:url>
                    </c:set>
                    <a href="${link}"><ww:property value="name"/></a>
                  </li>
                </ww:iterator>
              </ul>
            </div>
            <div id="statisticslinks">
              <ul>
                <li>
                  <c:set var="link">
                    <ww:url action="headtoheads" namespace="/">
                    	<ww:param name="gameFilter" value="%{gameFilter}"/>
                    </ww:url>
                  </c:set>
                  <a href="${link}">Head to heads</a>
                </li>
                <li>
                  <c:set var="link">
                    <ww:url action="winningstreaks">
                    	<ww:param name="gameFilter" value="%{gameFilter}"/>
                    </ww:url>
                  </c:set>
                  <a href="${link}">Winning streaks</a>
                </li>
                <li>
                  <c:set var="link">
                    <ww:url action="losingstreaks" namespace="/">
                    	<ww:param name="gameFilter" value="%{gameFilter}"/>
                    </ww:url>
                  </c:set>
                  <a href="${link}">Losing streaks</a>
                </li>
              </ul>
            </div>
          </div>
        </div>
        <div id="column2">
          <decorator:body/>
        </div>
      </div>
      <div id="footer">
        copyright 2006 Alex Jones | alex.jones at unclealex.co.uk | <a href="http://validator.w3.org/check?uri=referer">XHTML 1.1</a> | <a href="http://jigsaw.w3.org/css-validator/check/referer">CSS</a> | <a href="http://www.dcarter.co.uk">design by dcarter</a>
      </div>
    </div>
    <script type="text/javascript">
      <ww:if test="selectedDate != null">
        showSelectionLeagueLinks();
      </ww:if>
      <ww:elseif test="showLeague != null">
        showLeagueLinks();
      </ww:elseif>
      <ww:elseif test="showProfile != null">
        showProfileLinks();
      </ww:elseif>
      <ww:elseif test="showStatistics != null">
        showStatisticsLinks();
      </ww:elseif>
    </script>
  </body>
  </html>
</jsp:root>
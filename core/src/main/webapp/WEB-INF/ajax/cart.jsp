<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:decorator="http://www.opensymphony.com/sitemesh/decorator"
	xmlns:c="http://java.sun.com/jsp/jstl/core" xmlns:s="/struts-tags"
	version="2.0">

	<jsp:output omit-xml-declaration="true" />

	<jsp:directive.page contentType="text/html" />
	<div>
		<h1>Your download cart</h1>
		<s:if
			test="%{downloadCartContents == null || downloadCartContents.entrySet().empty}">
			<p>Your cart is empty.</p>
		</s:if>
		<s:else>
			<c:set var="link"><s:url action="downloadcart" namespace="html" includeParams="none"/></c:set>
			<p><a href="${link}">Download</a></p>
			<ul>
				<s:iterator value="downloadCartContents.entrySet()">
					<li><!-- Artist --> <c:set var="link">
						<s:url action="albums" includeParams="none" namespace="html">
							<s:param name="letter" value="letter" />
							<s:param name="flacArtist" value="key.id" />
						</s:url>
					</c:set>
					<a href="${link}"><s:property value="key.name" /></a>
					<c:set var="link">javascript:removeArtist(<s:property value="key.id"/>);</c:set>
					<a href="${link}">(x)</a>
					<s:if test="value != null">
						<ul>
							<s:iterator value="value.entrySet()">
								<li>
									<!-- Album -->
									<c:set var="link">
									<s:url action="tracks" includeParams="none" namespace="html">
										<s:param name="letter" value="letter" />
										<s:param name="flacAlbum" value="key.id" />
									</s:url>
								</c:set> <a href="${link}"><s:property value="key.title" /></a>					
								<c:set var="link">javascript:removeAlbum(<s:property value="key.id"/>);</c:set>
								<a href="${link}">(x)</a> <s:if test="value != null">
									<ul>
										<s:iterator value="value">
											<li><!-- Track --> <s:property value="title" />
												<c:set var="link">javascript:removeTrack(<s:property value="id"/>);</c:set>
												<a href="${link}">(x)</a></li>
										</s:iterator>
									</ul>
								</s:if></li>
							</s:iterator>
						</ul>
					</s:if></li>
				</s:iterator>
			</ul>
			<p><a href="javascript:clearCart();">Clear</a></p>
		</s:else>
	</div>
</jsp:root>
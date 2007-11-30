<jsp:root
  xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:s="/struts-tags"
  xmlns:c="http://java.sun.com/jsp/jstl/core"
  version="2.0">

	<p>Yeah!</p>
	<s:if test="progressWritingListenersByDeviceBean.entrySet().isEmpty()">
		<p>WARNING: No listeners</p>
	</s:if>
	<s:iterator value="%{progressWritingListenersByDeviceBean.entrySet()}">
	 	<table>
	 		<tbody>
	 			<tr>
	 				<th>Device:</th>
	 				<td><s:property value="key.fullDescription"/></td>
	 			</tr>
	 			<s:else>
	  			<s:push value="value">
	  				<tr>
	  					<th>Files written:</th>
	  					<td><s:property value="filesWrittenCount"/> of <s:property value="totalFiles"/></td>
	  				</tr>
	  				<tr>
	  					<th>Last file written:</th>
	  					<td>
	  						<s:push value="fileNamesWritten">
	  							<s:if test="empty">
	  								N/A
	  							</s:if>
	  							<s:else>
	  								<s:property value="last"/>
	  							</s:else>
	  						</s:push>
	  					</td>
	  				</tr>
	  			</s:push>
	  		</s:else>
	 		</tbody>
	 	</table>
	</s:iterator>
</jsp:root>
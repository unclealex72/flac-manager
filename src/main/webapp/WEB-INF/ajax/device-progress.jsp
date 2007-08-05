<jsp:root
  xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:s="/struts-tags"
  xmlns:c="http://java.sun.com/jsp/jstl/core"
  version="2.0">

  
 	<table>
 		<tbody>
 			<tr>
 				<th>Device:</th>
 				<td><s:property value="device.fullDescription"/></td>
 			</tr>
 			<s:if test="writingListener == null">
 				<th/>
 				<td>Preparing the device for writing.</td>
 			</s:if>
 			<s:else>
  			<s:push value="writingListener">
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

</jsp:root>
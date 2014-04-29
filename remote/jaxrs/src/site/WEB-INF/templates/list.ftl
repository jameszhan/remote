

<table border="1" align="center" width="800">
	<tr>
		<th>Name</th>
		<th>Company</th>
		<th>University</th>
		<th>Native Place</th>
		<th>Contact</th>			
	</tr>
	<#list it as i>
	<tr>
		<td>${i.name!}</td>
		<td>${i.company!}</td>
		<td>${i.university!}</td>
		<td>${i.nativePlace!}</td>
		<td>		
		<#if i.contact?exists>
			<ol>			
				<li>email: ${i.contact.email!}</li>
				<li>addr: ${i.contact.addr!}</li>
				<li>cellphone: ${i.contact.cellphone!}</li>
				<li>tel: ${i.contact.tel!}</li>
				<#assign keys = i.contact.contacts?keys>    
				<#list keys as o>
				<li>${o}: ${i.contact.contacts[o]}</li>
				</#list>
			</ol>
		<#else>
			<span style="color:#ffffff">Author: James</span>				
		</#if>		
		</td>
	</tr>
	</#list>
</table>	
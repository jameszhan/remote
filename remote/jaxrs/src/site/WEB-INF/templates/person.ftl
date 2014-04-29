<table border="1" align="center" width="500">
		<tr>
			<td>Name</td>
			<td>${it.name!}</td>
		</tr>
		<tr>
			<td>Company</td>
			<td>${it.company!}</td>
		</tr>
		<tr>
			<td>University</td>
			<td>${it.university!}</td>
		</tr>
		<tr>
			<td>Native Place</td>
			<td>${it.nativePlace!}</td>
		</tr>
		<tr>
			<td>Contact</td>
			<td>
			<#if it.contact?exists>
				<ol>
				<li>email: ${it.contact.email!}</li>
				<li>addr: ${it.contact.addr!}</li>
				<li>cellphone: ${it.contact.cellphone!}</li>
				<li>tel: ${it.contact.tel!}</li>
				<#assign keys = it.contact.contacts?keys>    
				<#list keys as o>
				<li>${o}: ${it.contact.contacts[o]}</li>
				</#list>
				</ol>	
			<#else>
				<span style="color:#ffffff">Author: James</span>				
			</#if>
			</td>
		</tr>
	</table>
<%@ taglib prefix="c" uri="/WEB-INF/c.tld"  %>


	<table border="1" align="center" width="300">
		<tr>
			<td>Name</td>
			<td>${it.name}</td>
		</tr>
		<tr>
			<td>Company</td>
			<td>${it.company}</td>
		</tr>
		<tr>
			<td>University</td>
			<td>${it.university}</td>
		</tr>
		<tr>
			<td>Native Place</td>
			<td>${it.nativePlace}</td>
		</tr>
		<tr>
			<td>Contact</td>
			<td>
			<ol>
			<li>email: ${it.contact.email}</li>
			<li>addr: ${it.contact.addr}</li>
			<li>cellphone: ${it.contact.cellphone}</li>
			<li>tel: ${it.contact.tel}</li>
			<c:forEach items="${it.contact.contacts}" var="o">		     	
		      	<li>${o.key}: ${o.value}</li>
		    </c:forEach>
			</ol>
			</td>
		</tr>
	</table>	


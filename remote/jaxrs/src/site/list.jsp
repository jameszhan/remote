<%@ taglib prefix="c" uri="/WEB-INF/c.tld"  %>


	<table border="1" align="center" width="600">
		<tr>
			<th>Name</th>
			<th>Company</th>
			<th>University</th>
			<th>Native Place</th>
			<th>Contact</th>			
		</tr>
		<c:forEach items="${it}" var="i">		     	
		<tr>
			<td>${i.name}</td>
			<td>${i.company}</td>
			<td>${i.university}</td>
			<td>${i.nativePlace}</td>
			<td>
			<ol>
			<li>email: ${i.contact.email}</li>
			<li>addr: ${i.contact.addr}</li>
			<li>cellphone: ${i.contact.cellphone}</li>
			<li>tel: ${i.contact.tel}</li>
			<c:forEach items="${i.contact.contacts}" var="o">		     	
		      	<li>${o.key}: ${o.value}</li>
		    </c:forEach>
			</ol>
			</td>
		</tr>
		</c:forEach>
	</table>	

<%@taglib uri="http://www.apple.com/tags" prefix="my" %>
<html>
<head>
<title>Samples</title>
</head>
<body>
	<my:msg>First.<br /></my:msg>
	<my:loop times="1">		
		<my:loop times="2">
			<my:msg>Hello World.</my:msg>
			<br />
		</my:loop>		
	</my:loop>
	

	<my:lo times="2">
		<my:msg>Hello World.<br /></my:msg>
	</my:lo>		

</body>
</html>
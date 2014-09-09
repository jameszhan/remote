<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<#assign ui = JspTaglibs["/WEB-INF/test.tld"]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Hello Jsp Tag</title>
</head>
<body>
<@ui.hello message="Hello Service." />
<br />

<@ui.loop times=1>		
		<@ui.loop times=2>
			<@ui.msg>Hello World.</@ui.msg><br />
		</@ui.loop>		
</@ui.loop>
	

<@ui.lo times=2>
		<@ui.msg>Hello World.</@ui.msg><br />
</@ui.lo>



</body>
</html>
<%@taglib uri="http://www.mulberry.com/tags" prefix="my" %>
<html>
<head>
    <title>Hello World</title>
</head>
<body>

<fieldset>
    <legend>HelloTag</legend>
    <my:hello />
    <br />
    <my:hello message="James!"/>
</fieldset>

<fieldset>
    <legend>Generic</legend>
    <my:msg />
</fieldset>


<strong>Current time is ${now}</strong>
</body>
</html>
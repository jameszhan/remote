<%@taglib uri="http://www.mulberry.com/tags" prefix="my" %>
<html>
<head>
<title>Life Cycle</title>
</head>
<body>

<textarea cols="120" rows="6">
<my:life startFlag="0">SKIP_BODY</my:life>
</textarea>

<textarea cols="120" rows="6">
<my:life startFlag="1">EVAL_BODY_INCLUDE</my:life>
</textarea>

<textarea cols="120" rows="10">
<my:life startFlag="2">EVAL_BODY_BUFFERED</my:life>
</textarea>

<textarea cols="120" rows="10">
<my:life startFlag="1" afterBodyFlag="2">EVAL_BODY_INCLUDE, EVAL_BODY_AGAIN</my:life>
</textarea>

<textarea cols="120" rows="10">
<my:life startFlag="2" afterBodyFlag="2">EVAL_BODY_BUFFERED, EVAL_BODY_AGAIN</my:life>
</textarea>

</body>
</html>
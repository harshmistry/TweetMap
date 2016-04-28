<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isErrorPage="true" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<h1>Opps...</h1>
	<p>Sorry, an error occurred.</p>
	<p>Here is the exception stack trace: </p>
	<pre>
	<% exception.printStackTrace(response.getWriter()); %>
</pre>
</body>
</html>
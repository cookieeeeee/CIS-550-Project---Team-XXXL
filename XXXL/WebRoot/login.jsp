<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>Login</title>
</head>

<body>
	<form action="${pageContext.request.contextPath }/login" method="post">
		<table>
			<tr>
				<td>User Name:</td>
				<td><input type="text" name="userLogin.name"></td>
			</tr>
			<tr>
				<td>Password:</td>
				<td><input type="password" name="userLogin.pwd"></td>
			</tr>
			<tr>
				<td><span style="color: red;">${requestScope.mismatch}</span></td>
				<td><input type="submit" value="Login"><a
					href="register.jsp">Register</a></td>

			</tr>
		</table>
		<br /> <br />

	</form>
</body>
</html>

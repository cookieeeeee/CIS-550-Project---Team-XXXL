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
<script type="text/javascript">
	function disable() {
		document.getElementById("upload").disabled = true
		document.getElementById("upload").style.opacity = 0.6
		document.getElementById("upload").style.cursor = "not-allowed"
	}
</script>
<style type="text/css">
.container1 {
	position: absolute;
	top: 20%;
	left: 39%;
	width: 400px;
	height: 350px;
	background-color: rgb(175, 173, 179); - -rgb (184, 231, 246);
	opacity: 0.9;
	background-attachment: fixed;
	border-radius: 10px;
}

.usertxt {
	position: absolute;
	top: 13%;
	left: 20%;
	width: 100px;
	height: 30px;
	background-attachment: fixed;
	font: 20px arial, sans-serif;
	font-style: oblique;
	font-weight: bold; -
	-text-shadow: 2px 2px 4px #94C6F7; -
	-text-transform: capitalize;
	color: #5C2E74;
	z-index: 8;
}

.pwdtxt {
	position: absolute;
	top: 43%;
	left: 20%;
	width: 100px;
	height: 30px;
	background-attachment: fixed;
	font: 20px arial, sans-serif;
	font-style: oblique;
	font-weight: bold; -
	-text-shadow: 2px 2px 4px #94C6F7; -
	-text-transform: capitalize;
	color: #5C2E74;
	z-index: 8;
}

a {
	color: rgb(256, 256, 100);
	text-shadow: 2px 2px 4px #94C6F7;
}

.user {
	position: absolute;
	top: 20%;
	left: 20%;
	width: 60%;
	height: 40px;
	background-color: rgb(255, 255, 255);
	background-attachment: fixed;
	font: 24px arial;
}

.password {
	position: absolute;
	top: 50%;
	left: 20%;
	width: 60%;
	height: 40px;
	background-color: rgb(255, 255, 255);
	background-attachment: fixed;
	font: 24px arial;
}

.login {
	border-radius: 50%;
	background-color: rgb(236, 140, 94);
	position: absolute;
	left: 15%;
	top: 250px;
	width: 120px;
	height: 50px;
	font: 20px arial, sans-serif;
	font-style: oblique;
	font-weight: bold;
	text-shadow: 2px 2px 4px #94C6F7;
	text-transform: capitalize;
	color: #FFFFFF;
	z-index: 7;
}

.login:hover {
	cursor: pointer;
	background-color: #F0AB8A;
}

.register:hover {
	background-color: #F0AB8A;
}

a:active {
	color: red;
}

.register {
	border-radius: 50%;
	background-color: rgb(236, 140, 94);
	position: absolute;
	right: 15%;
	top: 250px;
	width: 120px;
	height: 50px;
	font: 20px arial, sans-serif;
	font-style: oblique;
	font-weight: bold;
	text-shadow: 2px 2px 4px #94C6F7;
	text-align: center;
	line-height: 50px;
	text-decoration: none;
	text-transform: capitalize;
	color: #FFFFFF;
	z-index: 7;
}

.ali {
	position: absolute;
	top: 45%;
	left: 70%;
	width: 400px;
	height: 396px;
	background-image: url("ali.png");
	background-repeat: no-repeat;
}

.hello {
	position: absolute;
	top: 0%;
	left: 0%;
	width: 150px;
	height: 30px;
	background-attachment: fixed;
	font: 25px arial, sans-serif;
	font-style: oblique;
	font-weight: bold;
	text-shadow: 2px 2px 4px #FF0000;
	text-transform: capitalize;
	color: #FFFFFF;
	z-index: 9; -
	-display: none;
}
</style>
<title>Login</title>
</head>

<body>
	<div class="container1" id="panel">
		<div class="usertxt">user</div>
		<form action="${pageContext.request.contextPath }/login" method="post">
			<input class="user" id="user" name="userLogin.name" type="text"></input>
			<input class="password" id="password" name="userLogin.pwd"
				type="password"></input>

			<div class="pwdtxt">password</div>

			<div class="loginbutton">
				<button class="login" id="loginb" onclick="disable()" type="submit">login</button>
				<a class="register" href="register.jsp">register</a> <span
					style="color: red;">${requestScope.mismatch}</span>
			</div>
		</form>
	</div>
</body>
</html>

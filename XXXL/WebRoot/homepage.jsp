<%@page import="com.mongodb.client.model.Filters"%>
<%@page import="com.mongodb.client.model.Projections"%>
<%@page import="org.bson.Document"%>
<%@page import="com.mongodb.client.FindIterable"%>
<%@page import="com.mongodb.BasicDBObject"%>
<%@page import="com.xxxl.dao.JSONDAO"%>
<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";

	// Get fileList
	String name = (String) session.getAttribute("name");
	BasicDBObject query = new BasicDBObject("name", name);
	//Iterator<Document> ite = JSONDAO.json.find()
	//.projection(Projections.include("__fileName__")).iterator();
	Iterator<Document> ite = JSONDAO.json.find(new BasicDBObject(
			"value", "__content__")).iterator();
	LinkedList<String> fileNames = new LinkedList<String>();
	while (ite.hasNext()) {
		Document curDoc = ite.next();
		String fileName = (String) curDoc.get("key");
		fileNames.add(fileName);
	}
	session.setAttribute("fileList", fileNames);

	// Get follows
	List<String> follows = (List<String>) JSONDAO.login.find(query)
			.iterator().next().get("follows");
	if (follows == null) {
		follows = new LinkedList<String>();
	}
	session.setAttribute("follows", follows);
%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">

<title>My JSP 'index.jsp' starting page</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="Homepage">
<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
</head>

<body>
	Hello ${name }
	<table>
		<tr>
			<td>Now you have:</td>
		</tr>
		<c:forEach items="${fileList}" var="item">
			<tr></tr>
			<td>${item }</td>
			<tr></tr>
		</c:forEach>
		<tr>
			<td>Follows:</td>
		</tr>
		<c:forEach items="${follows }" var="follow">
			<tr></tr>
			<td>${follow }</td>
			<tr></tr>
		</c:forEach>
	</table>
	<form action="${pageContext.request.contextPath }/searchAction">
		<table>
			<tr>
				<td>First Keyword: <input type="text" name="firstKeyword"></td>
				<td>Second Keyword: <input type="text" name="secondKeyword"></td>
				<td><input type="submit" value="Search"></td>
			</tr>
		</table>
	</form>
	<table>
		<c:forEach items="${searchResult }" var="result">
			<tr>
				<td>${result }</td>
			</tr>
		</c:forEach>
	</table>
	<form action="${pageContext.request.contextPath}/fileUploadAction"
		method="post" enctype="multipart/form-data">
		<table>
			<tr>
				<td>File</td>
				<td><input type="file" name="file1" /></td>
				<td><input type="submit" value="Upload" /></td>
			</tr>
		</table>
		<span>${hint}</span>
	</form>
	<br>
</body>
</html>

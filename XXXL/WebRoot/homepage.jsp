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
	if(name == null){
		response.sendRedirect("/login.jsp");
		return;
	}
	BasicDBObject query = new BasicDBObject("name", name);
	Iterator<Document> ite = JSONDAO.json.find(
	new BasicDBObject("value", "__content__")).iterator();
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

<title>Homepage</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="Homepage">
<style type="text/css">
#container {
	width: 600px;
	height: 600px;
	margin: auto;
	float: left;
	border-style: solid;
	border-width: 1px;
	border-color: black;
}

#content {
	width: auto;
	height: auto;
	float: left;
}

#result {
	float: none;
	width: 1000px;
	height: auto;
}
</style>
</head>

<body>
	<div id="content">
		Hello ${name } <a
			href="${pageContext.request.contextPath }/logoutAction">Log Out</a>
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
		<form action="${pageContext.request.contextPath }/followAction">
			<table>
				<tr>
					<td>Follow Name:</td>
					<td><input type="text" name="followName"></td>
					<td><input type="submit" value="Follow"></td>
				</tr>
			</table>
		</form>
		<form action="${pageContext.request.contextPath }/searchAction">
			<table>
				<tr>
					<td>First Keyword: <input type="text" name="firstKeyword"></td>
					<td>Second Keyword: <input type="text" name="secondKeyword"></td>
					<td><input type="submit" value="Search"></td>
				</tr>
			</table>
		</form>
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
	</div>
	<div id="container"></div>
	<div id="result">
		<table>
			<c:forEach items="${searchResult }" var="result">
				<tr>
					<td>${result }</td>
				</tr>
			</c:forEach>
		</table>
	</div>
	<script src="/js/sigma.min.js"></script>
	<script src="/js/src/renderers/sigma.renderers.canvas.js"></script>
	<script src="/js/src/renderers/canvas/sigma.canvas.hovers.def.js"></script>
	<script src="/js/sigma.parsers.json.min.js"></script>
	<script src="/js/sigma.layout.forceAtlas2/worker.js"></script>
	<script src="/js/sigma.layout.forceAtlas2/supervisor.js"></script>

	<script>
		var data = ${jsonStr};
		var pathList = ${pathList};
		s = new sigma({
			graph : data,
			//container : 'container',
			renderer : {
				container : document.getElementById('container'),
				type : 'canvas'
			},
			settings : {
				defaultNodeColor : '#bbbbbb',
				labelThreshold : 100,
				defaultEdgeColor : '#DDDDDD',
				edgeColor : "default",
			}
		});
		s.startForceAtlas2({
			worker : true,
			barnesHutOptimize : false,
			gravity : 0.5,
			outboundAttractionDistribution : true,
			strongGravityMode : true
		});
		var resultColor = "#ff0000";
		for (var i = 0; i < pathList.length; i++) {
			var nodeList = pathList[i];
			for (var j = 0; j < nodeList.length; j++) {
				node0 = nodeList[j];
				s.graph.nodes().forEach(function(e) {
					if (e.id == node0) {
						e.color = resultColor;
					}
				});
				if(j==nodeList.length-1)
					break;
				node1 = nodeList[j+1];
				s.graph.edges().forEach(function(e) {
					if ((e.source == node0 && e.target == node1)
							|| (e.source == node1 && e.target == node0)) {
						e.color = resultColor;
					}
				});
			}
		}
		setTimeout("s.stopForceAtlas2()", 10000);
	</script>

</body>


</html>

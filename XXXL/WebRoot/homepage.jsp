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

<title>My JSP 'index.jsp' starting page</title>
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
	border-color: lime;
}

#content {
	width: 400px;
	height: auto;
	float: left;
}

#result {
	float: none;
	width: 1000px;
	height: auto;
}
</style>
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
	<script src="/js/sigma.parsers.json.min.js"></script>
	<script src="/js/sigma.layout.forceAtlas2/worker.js"></script>
	<script src="/js/sigma.layout.forceAtlas2/supervisor.js"></script>
	<script src="/js/src/renderers/sigma.renderers.canvas.js"></script>
	<script src="/js/src/renderers/canvas/sigma.canvas.hovers.def.js"></script>

	<!-- START SIGMA IMPORTS -->
	<script src="/js/src/conrad.js"></script>
	<script src="/js/src/utils/sigma.utils.js"></script>
	<script src="/js/src/utils/sigma.polyfills.js"></script>
	<script src="/js/src/sigma.settings.js"></script>
	<script src="/js/src/classes/sigma.classes.dispatcher.js"></script>
	<script src="/js/src/classes/sigma.classes.configurable.js"></script>
	<script src="/js/src/classes/sigma.classes.graph.js"></script>
	<script src="/js/src/classes/sigma.classes.camera.js"></script>
	<script src="/js/src/classes/sigma.classes.quad.js"></script>
	<script src="/js/src/classes/sigma.classes.edgequad.js"></script>
	<script src="/js/src/captors/sigma.captors.mouse.js"></script>
	<script src="/js/src/captors/sigma.captors.touch.js"></script>
	<script src="/js/src/renderers/sigma.renderers.canvas.js"></script>
	<script src="/js/src/renderers/sigma.renderers.webgl.js"></script>
	<script src="/js/src/renderers/sigma.renderers.svg.js"></script>
	<script src="/js/src/renderers/sigma.renderers.def.js"></script>
	<script src="/js/src/renderers/webgl/sigma.webgl.nodes.def.js"></script>
	<script src="/js/src/renderers/webgl/sigma.webgl.nodes.fast.js"></script>
	<script src="/js/src/renderers/webgl/sigma.webgl.edges.def.js"></script>
	<script src="/js/src/renderers/webgl/sigma.webgl.edges.fast.js"></script>
	<script src="/js/src/renderers/webgl/sigma.webgl.edges.arrow.js"></script>
	<script src="/js/src/renderers/canvas/sigma.canvas.labels.def.js"></script>
	<script src="/js/src/renderers/canvas/sigma.canvas.hovers.def.js"></script>
	<script src="/js/src/renderers/canvas/sigma.canvas.nodes.def.js"></script>
	<script src="/js/src/renderers/canvas/sigma.canvas.edges.def.js"></script>
	<script src="/js/src/renderers/canvas/sigma.canvas.edges.curve.js"></script>
	<script src="/js/src/renderers/canvas/sigma.canvas.edges.arrow.js"></script>
	<script
		src="/js/src/renderers/canvas/sigma.canvas.edges.curvedArrow.js"></script>
	<script src="/js/src/renderers/canvas/sigma.canvas.edgehovers.def.js"></script>
	<script src="/js/src/renderers/canvas/sigma.canvas.edgehovers.curve.js"></script>
	<script src="/js/src/renderers/canvas/sigma.canvas.edgehovers.arrow.js"></script>
	<script
		src="/js/src/renderers/canvas/sigma.canvas.edgehovers.curvedArrow.js"></script>
	<script src="/js/src/renderers/canvas/sigma.canvas.extremities.def.js"></script>
	<script src="/js/src/renderers/svg/sigma.svg.utils.js"></script>
	<script src="/js/src/renderers/svg/sigma.svg.nodes.def.js"></script>
	<script src="/js/src/renderers/svg/sigma.svg.edges.def.js"></script>
	<script src="/js/src/renderers/svg/sigma.svg.edges.curve.js"></script>
	<script src="/js/src/renderers/svg/sigma.svg.labels.def.js"></script>
	<script src="/js/src/renderers/svg/sigma.svg.hovers.def.js"></script>
	<script src="/js/src/middlewares/sigma.middlewares.rescale.js"></script>
	<script src="/js/src/middlewares/sigma.middlewares.copy.js"></script>
	<script src="/js/src/misc/sigma.misc.animation.js"></script>
	<script src="/js/src/misc/sigma.misc.bindEvents.js"></script>
	<script src="/js/src/misc/sigma.misc.bindDOMEvents.js"></script>
	<script src="/js/src/misc/sigma.misc.drawHovers.js"></script>
	<!-- END SIGMA IMPORTS -->
	<script>
		var data = ${jsonStr};
		s = new sigma({
			graph : data,
			//container : 'container',
			renderer : {
				container : document.getElementById('container'),
				type : 'canvas'
			},
			settings : {
				defaultNodeColor : '#666',
				labelThreshold : 100,
				defaultEdgeColor : '#ccc',
				edgeHoverColor : '#bbb',
				edgeHoverSizeRatio : 10,
				edgeHoverExtremities : true,
				enableEdgeHovering : true,
				defaultEdgeHoverColor : '#aaa'

			}
		});
		s.startForceAtlas2({
			worker : true,
			barnesHutOptimize : false,
			gravity : 0.5,
			outboundAttractionDistribution : true,
			strongGravityMode : true
		});
		setTimeout("s.stopForceAtlas2()", 10000);
	</script>
</head>
</body>


</html>

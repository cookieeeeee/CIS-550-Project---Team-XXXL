package com.xxxl.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import freemarker.core.ParseException;

public class Json2Graph {

	private static String mapName = "map";
	private static int mapCount = 0;
	private static String listName = "list";
	private static int listCount = -1;
	private static String contentName = "content";
	private static LinkedList<JsonNode> resultList;
	private static String prefix;
	private static int idCount = 0;

	public final static LinkedList<JsonNode> json2Graph(String jsonStr,
			String userName, String fileName) throws ParseException {
		resultList = new LinkedList<JsonNode>();
		prefix = userName + ":" + fileName.replace(".", "_") + ":";
		JSONObject jsonObject = new JSONObject(jsonStr);
		JsonNode root = new JsonNode(null, fileName, "content", prefix);
		JsonNode ancester = new JsonNode(root, "content", "map0", prefix
				+ idCount);
		idCount++;
		resultList.add(ancester);
		resultList.add(root);
		parseMapObj(jsonObject, ancester);
		getAllChildren();
		setLinkedIds();
		return resultList;
	}

	private final static void getAllChildren() {
		for (JsonNode j : resultList) {
			JsonNode parent = j.parent;
			if (parent != null) {
				parent.children.add(j);
			}
		}
	}

	private final static void setLinkedIds() {
		for (JsonNode j : resultList) {
			JsonNode parent = j.parent;
			if (parent != null) {
				j.linkedId.add(parent.id);
			}
			for (JsonNode child : j.children) {
				j.linkedId.add(child.id);
			}
		}
	}

	private final static void parseMapObj(JSONObject jsonObject,
			JsonNode curNode) {
		// Map<String, Object> resultMap = new HashMap<String, Object>();
		Iterator<String> iterator = jsonObject.keys();
		String currentMapName = curNode.value;
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			Object value = jsonObject.get(key);
			if (value instanceof JSONObject) {
				// resultMap.put(key, value);
				mapCount++;
				JsonNode curChild = new JsonNode(curNode, currentMapName + "->"
						+ key, mapName + mapCount, prefix + idCount);
				idCount++;
				// resultMap.put(currentMapName + "->" + key, mapName +
				// mapCount);
				resultList.add(curChild);
				parseMapObj((JSONObject) value, curChild);
			} else if (value instanceof JSONArray) {
				// resultMap.put(key, value);
				listCount++;
				JsonNode curChild = new JsonNode(curNode, currentMapName + "->"
						+ key, listName + listCount, prefix + idCount);
				idCount++;
				// resultMap
				// .put(currentMapName + "->" + key, listName + listCount);
				resultList.add(curChild);
				parseListObj((JSONArray) value, curChild);
			} else {
				// resultMap.put(currentMapName + "->" + key,
				// jsonObject.get(key));
				resultList.add(new JsonNode(curNode, currentMapName + "->"
						+ key, value.toString(), prefix + idCount));
				idCount++;
			}
		}
	}

	private final static void parseListObj(JSONArray jsonArray, JsonNode curNode) {
		String currentListName = curNode.value;
		int contentCount = 0;
		for (int i = 0; i < jsonArray.length(); i++) {
			Object value = jsonArray.get(i);
			if (value instanceof JSONObject) {
				mapCount++;
				JsonNode curChild = new JsonNode(curNode, currentListName
						+ "->" + contentName + contentCount,
						mapName + mapCount, prefix + idCount);
				resultList.add(curChild);
				idCount++;
				// resultMap.put(currentListName + "->" + contentName
				// + contentCount, mapName + mapCount);
				contentCount++;
				parseMapObj((JSONObject) value, curChild);
			} else if (value instanceof JSONArray) {
				// resultMap.put(currentListName + "->" + contentName
				// + contentCount, listName + listCount);
				listCount++;
				JsonNode curChild = new JsonNode(curNode, currentListName
						+ "->" + contentName + contentCount, listName
						+ listCount, prefix + idCount);
				idCount++;
				resultList.add(curChild);
				contentCount++;
				parseListObj((JSONArray) value, curChild);
			} else {
				// resultMap.put(currentListName + "->" + contentName
				// + contentCount, value);
				resultList.add(new JsonNode(curNode, currentListName + "->"
						+ contentName + contentCount, value.toString(), prefix
						+ idCount));
				idCount++;
				contentCount++;
			}
		}
	}
}

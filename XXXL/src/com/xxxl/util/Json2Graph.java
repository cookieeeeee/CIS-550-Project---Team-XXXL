package com.xxxl.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import freemarker.core.ParseException;

/**
 * file root, json map0, json map, json array, json node
 */
public class Json2Graph {
	public static final String FILE_ROOT = "file root";
	public static final String JSON_MAP0 = "json map0";
	public static final String JSON_MAP = "json map";
	public static final String JSON_ARRAY = "json array";
	public static final String JSON_NODE = "json node";

	private static String mapName = "map";
	private static int mapCount;
	private static String listName = "list";
	private static int listCount;
	private static String contentName = "__content__";
	private static LinkedList<JsonNode> resultList;
	private static String prefix;
	private static int idCount;

	public final static LinkedList<JsonNode> json2Graph(String jsonStr,
			String userName, String fileName) throws ParseException {
		idCount = 0;
		listCount = -1;
		mapCount = 0;
		resultList = new LinkedList<JsonNode>();
		prefix = userName + ":" + fileName.replace(".", "_") + ":";
		JSONObject jsonObject = new JSONObject(jsonStr);
		JsonNode root = new JsonNode(null, fileName, "__content__", prefix,
				FILE_ROOT);
		JsonNode ancester = new JsonNode(root, "__content__", "map0", prefix
				+ idCount, JSON_MAP0);
		idCount++;
		resultList.add(root);
		resultList.add(ancester);
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
		Iterator<String> iterator = jsonObject.keys();
		String currentMapName = curNode.value;
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			Object value = jsonObject.get(key);
			if (value instanceof JSONObject) {
				mapCount++;
				JsonNode curChild = new JsonNode(curNode, currentMapName + "->"
						+ key, mapName + mapCount, prefix + idCount, JSON_MAP);
				idCount++;
				resultList.add(curChild);
				parseMapObj((JSONObject) value, curChild);
			} else if (value instanceof JSONArray) {
				listCount++;
				JsonNode curChild = new JsonNode(curNode, currentMapName + "->"
						+ key, listName + listCount, prefix + idCount,
						JSON_ARRAY);
				idCount++;
				resultList.add(curChild);
				parseListObj((JSONArray) value, curChild);
			} else {
				resultList.add(new JsonNode(curNode, currentMapName + "->"
						+ key, value.toString(), prefix + idCount, JSON_NODE));
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
						mapName + mapCount, prefix + idCount, JSON_MAP);
				resultList.add(curChild);
				idCount++;
				contentCount++;
				parseMapObj((JSONObject) value, curChild);
			} else if (value instanceof JSONArray) {
				listCount++;
				JsonNode curChild = new JsonNode(curNode, currentListName
						+ "->" + contentName + contentCount, listName
						+ listCount, prefix + idCount, JSON_ARRAY);
				idCount++;
				resultList.add(curChild);
				contentCount++;
				parseListObj((JSONArray) value, curChild);
			} else {
				resultList.add(new JsonNode(curNode, currentListName + "->"
						+ contentName + contentCount, value.toString(), prefix
						+ idCount, JSON_NODE));
				idCount++;
				contentCount++;
			}
		}
	}
}

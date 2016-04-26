package com.xxxl.util;

import java.util.LinkedList;

public class JsonNode {
	public JsonNode parent;
	public LinkedList<JsonNode> children;
	public String key;
	public String value;
	public String id;
	public LinkedList<String> linkedId;
	
	public JsonNode(JsonNode parent, String key, String value, String id) {
		this.parent = parent;
		this.children = new LinkedList<JsonNode>();
		this.linkedId = new LinkedList<String>();
		this.key = key;
		this.value = value;
		this.id = id;
	}

	@Override
	public String toString() {
		String a = "";
		try{
		 a = "JsonNode [key=" + key + ", value=" + value + ", id=" + id
				+ ", linkedId: " + linkedId + "]";
		}catch(Exception e){
			
		}
		return a;
	}
}

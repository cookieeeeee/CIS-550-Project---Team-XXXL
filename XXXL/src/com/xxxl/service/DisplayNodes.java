package com.xxxl.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bson.Document;
import org.json.JSONObject;

import com.mongodb.client.MongoCursor;
import com.xxxl.dao.UserLoginDAO;

public class DisplayNodes {
	public String displayFiles() {
		Random r = new Random();
		List<JSONObject> nodeList = new ArrayList<JSONObject>();
		List<Document> docNodeList = new ArrayList<Document>();
		List<JSONObject> linkList = new ArrayList<JSONObject>();
		int idCount = 0;
		int x = 3;
		int y = 7;
		int incre = 11;
		int step = 17;
		MongoCursor<Document> mongoCursor = UserLoginDAO.json.find().iterator();
		int count = 0;
		while (mongoCursor.hasNext()) {
//			if (count++ > 100) {
//				break;
//			}
			Document docNode = mongoCursor.next();
			JSONObject jsonNode = new JSONObject();
			String nodeId = (String) docNode.get("id");
			jsonNode.put("id", nodeId);
			jsonNode.put("label",
					docNode.get("key") + ":" + (String) docNode.get("value"));
			jsonNode.put("x", x);
			x = x + 13;
			if (x > 50) {
				x = x - 47 - r.nextInt(5);
				y += step;
			}
			y = y + incre;
			incre = -incre;
			jsonNode.put("y", y);
			jsonNode.put("size", 3);
			nodeList.add(jsonNode);
			List<String> linkedIds = (List<String>) docNode.get("linkedId");
			if(linkedIds.size()>30){
				continue;
			}
			for (String linkedId : linkedIds) {
				JSONObject link = new JSONObject();
				link.put("id", idCount);
				idCount++;
				link.put("source", nodeId);
				link.put("target", linkedId);
				linkList.add(link);
			}
		}
		JSONObject ret = new JSONObject();
		ret.put("nodes", nodeList);
		ret.put("edges", linkList);

		return ret.toString();
	}
}

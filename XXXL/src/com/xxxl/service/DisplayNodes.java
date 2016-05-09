package com.xxxl.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bson.Document;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCursor;
import com.xxxl.dao.JSONDAO;
import com.xxxl.dao.UserLoginDAO;

public class DisplayNodes {
	public String displayFiles(String name) {
		Random r = new Random();
		List<JSONObject> nodeList = new ArrayList<JSONObject>();
		List<JSONObject> linkList = new ArrayList<JSONObject>();
		int idCount = 0;
		int x = 3;
		int y = 7;
		int incre = 11;
		int step = 17;
		// MongoCursor<Document> followersIte = UserLoginDAO.login.find(
		// new BasicDBObject("name", name)).iterator();
		// List<String> allNames = new JSONDAO(name).findAllUserNames();
		JSONDAO jsondao = new JSONDAO(name);
		List<Document> docList = new LinkedList<Document>();
		Set<String> idSet = new HashSet<String>();
		List<String> allNames = jsondao.findUserNHisFollowers(name);
		for (String uName : allNames) {
			UserLoginDAO userLoginDAO = new UserLoginDAO(uName);
			MongoCursor<Document> mongoCursor = userLoginDAO.json.find()
					.iterator();
			while (mongoCursor.hasNext()) {
				Document d = mongoCursor.next();
				docList.add(d);
				idSet.add(d.getString("id"));
			}
		}
		for (Document docNode : docList) {
			JSONObject jsonNode = new JSONObject();
			String nodeId = (String) docNode.get("id");
			jsonNode.put("id", nodeId);
			String[] prefix = docNode.getString("id").split(":");
			jsonNode.put(
					"label",
					prefix[0] + "->" + prefix[1].replace("_", ".") + "::"
							+ docNode.get("key") + "::"
							+ (String) docNode.get("value"));
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
			for (String linkedId : linkedIds) {
				if (!idSet.contains(linkedId))
					continue;
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

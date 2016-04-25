package com.xxxl.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.xxxl.util.Json2Graph;
import com.xxxl.util.JsonNode;

import freemarker.core.ParseException;

public class JSONDAO {
	// public static MongoDatabase accounts;
	public static MongoCollection<Document> json;
	public static MongoCollection<Document> login;
	static {
		MongoClient mongo = new MongoClient("localhost", 27017);
		login = UserLoginDAO.login;
		json = UserLoginDAO.json;
	}

	private Document JSON2Document(JSONObject jObj) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (String key : jObj.keySet()) {
			map.put(key, jObj.get(key));
		}
		return new Document(map);
	}

	public void saveJSON(String jsonStr, String fileName, String userName) {
		LinkedList<JsonNode> jList = null;
		try {
			jList = Json2Graph.json2Graph(jsonStr, userName, fileName);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		LinkedList<Document> allDocs = new LinkedList<Document>();
		for (JsonNode j : jList) {
			Document forInsert = new Document("id", j.id).append("key", j.key)
					.append("value", j.value);
			forInsert.append("linkedId", j.linkedId);
			allDocs.add(forInsert);
		}
		json.insertMany(allDocs);
	}

	public List<Document> findDocs(List<String> userNames) {
		List<Document> ret = new LinkedList<Document>();
		for (String userName : userNames) {
			MongoCollection<Document> curJson = UserLoginDAO.accounts
					.getCollection(userName);
			MongoCursor<Document> mongoCursor = curJson.find().iterator();
			while (mongoCursor.hasNext()) {
				ret.add(mongoCursor.next());
			}
		}
		return ret;
	}

	public void linking(List<String> userNames) {
		// LinkedList<Document> allFileDoc = new LinkedList<Document>();
		// Get all fileDoc
		for (String userName : userNames) {
			MongoCollection<Document> curUserCollection = UserLoginDAO.accounts
					.getCollection(userName);
			MongoCursor<Document> mongoCursor = curUserCollection.find(
					new BasicDBObject("value", "content")).iterator();
			while (mongoCursor.hasNext()) {
				// allFileDoc.add(mongoCursor.next());
				Document docToUpdate = mongoCursor.next();
				updateDocsLink(docToUpdate, curUserCollection, userNames);
			}
		}
	}

	private void updateDocsLink(Document docToUpdate,
			MongoCollection<Document> innerUserCollection,
			List<String> userNames) {
		ArrayList<String> innerUserLinkedIds = (ArrayList<String>) docToUpdate
				.get("linkedId");
		String fileName = docToUpdate.getString("key");
		String fileId = docToUpdate.getString("id");
		for (String userName : userNames) {
			// Iterate through all users' documents
			MongoCollection<Document> outterUserCollection = UserLoginDAO.accounts
					.getCollection(userName);
			MongoCursor<Document> mongoCursor = outterUserCollection.find()
					.iterator();
			while (mongoCursor.hasNext()) {
				Document outUsersDoc = mongoCursor.next();
				// If value is not fileName skip
				if (!outUsersDoc.getString("value").equals(fileName)) {
					continue;
				}
				ArrayList<String> outUserLinkedIds = (ArrayList<String>) outUsersDoc
						.get("linkedId");
				// Already have link
				if (outUserLinkedIds.contains(fileId)) {
					continue;
				}
				String outUserLeafId = outUsersDoc.getString("id");
				outUserLinkedIds.add(fileId);
				innerUserLinkedIds.add(outUserLeafId);
				outterUserCollection.updateOne(
						new Document("id", outUsersDoc.getString("id")),
						new BasicDBObject("$set", new BasicDBObject("linkedId",
								outUserLinkedIds)));
				innerUserCollection.updateOne(new Document("id", fileId),
						new BasicDBObject("$set", new BasicDBObject("linkedId",
								innerUserLinkedIds)));
			}
		}
	}
}

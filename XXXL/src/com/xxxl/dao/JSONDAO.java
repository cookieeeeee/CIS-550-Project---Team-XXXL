package com.xxxl.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.xxxl.util.CSV2Graph;
import com.xxxl.util.Json2Graph;
import com.xxxl.util.JsonNode;
import com.xxxl.util.Tika2Graph;
import com.xxxl.util.XML2Graph;

import freemarker.core.ParseException;

public class JSONDAO {
	// public static MongoDatabase accounts;
	public static MongoCollection<Document> json;
	public static MongoCollection<Document> login;
	public static MongoDatabase accounts;
	public static MongoCollection<Document> indexforsearch;
	static {
		login = UserLoginDAO.login;
		json = UserLoginDAO.json;
		accounts = UserLoginDAO.accounts;
		indexforsearch = accounts.getCollection("indexforsearch");
	}

	public List<String> findAllUserNames() {
		List<String> userNames = new ArrayList<String>();
		MongoCursor<Document> mongoCursor = login.find().iterator();
		while (mongoCursor.hasNext()) {
			Document curUserDoc = mongoCursor.next();
			String curUserName = (String) curUserDoc.get("name");
			userNames.add(curUserName);
		}
		return userNames;
	}

	private void saveToIndexforsearch(List<JsonNode> nodeList) {
		List<Document> allDocs = new LinkedList<Document>();
		for (JsonNode j : nodeList) {
			if (!(j.desc.equals(Json2Graph.JSON_NODE)
					|| j.desc.equals(CSV2Graph.CSV_C)
					|| j.desc.equals(XML2Graph.XML_NODE) || j.desc
						.equals(Tika2Graph.TIKA_WORD))) {
				continue;
			}
			String id = j.id;
			String[] values = j.value.split(" ");
			for (String value : values) {
				allDocs.add(new Document("id", id).append("value", value));
			}
		}
		indexforsearch.insertMany(allDocs);
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
			if (j.desc != null) {
				forInsert.append("desc", j.desc);
			}
			forInsert.append("linkedId", j.linkedId);
			allDocs.add(forInsert);
		}
		json.insertMany(allDocs);
		saveToIndexforsearch(jList);
	}

	public void saveCSV(File csvFile, String fileName, String userName) {
		List<JsonNode> jList = null;
		jList = new CSV2Graph().parseCSV(csvFile, userName, fileName);
		LinkedList<Document> allDocs = new LinkedList<Document>();
		for (JsonNode j : jList) {
			Document forInsert = new Document("id", j.id).append("key", j.key)
					.append("value", j.value);
			if (j.desc != null) {
				forInsert.append("desc", j.desc);
			}
			forInsert.append("linkedId", j.linkedId);
			allDocs.add(forInsert);
		}
		json.insertMany(allDocs);
		saveToIndexforsearch(jList);
	}

	/**
	 * Get all the docs with the name list
	 * 
	 * @param userNames
	 *            All the names of the user
	 * @return All the docs
	 */
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
		List<Document> allDocs = new LinkedList<Document>();
		List<Document> fileRootList = new LinkedList<Document>();
		List<Document> jsonNodeList = new LinkedList<Document>();
		List<Document> jsonMapList = new LinkedList<Document>();
		List<Document> xmlNodeList = new LinkedList<Document>();
		List<Document> csvCList = new LinkedList<Document>();
		List<Document> tikaWordList = new LinkedList<Document>();
		List<Document> nodeList = new LinkedList<Document>();

		// Get all docs
		for (String userName : userNames) {
			MongoCollection<Document> curUserCollec = UserLoginDAO.accounts
					.getCollection(userName);
			MongoCursor<Document> mongoCursor = curUserCollec.find().iterator();
			while (mongoCursor.hasNext()) {
				Document d = mongoCursor.next();
				String desc = d.getString("desc");
				if (desc.equals(Json2Graph.FILE_ROOT)) {
					fileRootList.add(d);
				} else if (desc.equals(Json2Graph.JSON_NODE)) {
					jsonNodeList.add(d);
					nodeList.add(d);
				} else if (desc.equals(XML2Graph.XML_NODE)) {
					xmlNodeList.add(d);
					nodeList.add(d);
				} else if (desc.equals(CSV2Graph.CSV_C)) {
					csvCList.add(d);
					nodeList.add(d);
				} else if (desc.equals(Tika2Graph.TIKA_WORD)) {
					tikaWordList.add(d);
					nodeList.add(d);
				} else if (desc.equals(Json2Graph.JSON_NODE)
						|| desc.equals(Json2Graph.JSON_MAP)) {
					jsonMapList.add(d);
				}
				allDocs.add(d);
			}
		}

		updateFileNameLinks(fileRootList, nodeList);
		updateValueKeyLinks(nodeList, jsonMapList);
		updataBothValueLinks(nodeList);
	}

	private void updataBothValueLinks(List<Document> nodeList) {
		for (int i = 0; i < nodeList.size(); i++) {
			for (int j = i + 1; j < nodeList.size(); j++) {
				Document leftNode = nodeList.get(i);
				Document rightNode = nodeList.get(j);
				String leftValue = leftNode.getString("value");
				String rightValue = rightNode.getString("value");
				if (leftValue.equals(rightValue)) {
					List<String> nodeLinkedIds = (List<String>) leftNode
							.get("linkedId");
					String rightId = rightNode.getString("id");
					if (nodeLinkedIds.contains(rightId))
						continue;
					String leftId = leftNode.getString("id");
					linkTwoNode(leftId, rightId);
				}
			}
		}
	}

	private void updateValueKeyLinks(List<Document> nodeList,
			List<Document> jsonMapList) {
		for (Document jsonMap : jsonMapList) {
			String mapKey = null;
			try {
				mapKey = jsonMap.getString("key").split("->")[1];
			} catch (Exception e) {
				continue;
			}
			// In case in list
			if (mapKey.startsWith("__content__"))
				continue;
			String mapId = jsonMap.getString("id");
			for (Document node : nodeList) {
				String nodeValue = node.getString("value");
				if (nodeValue.equals(mapKey)) {
					List<String> nodeLinkedIds = (List<String>) node
							.get("linkedId");
					if (nodeLinkedIds.contains(mapId))
						continue;
					String nodeId = node.getString("id");
					linkTwoNode(mapId, nodeId);
				}
			}
		}
	}

	private void updateFileNameLinks(List<Document> fileRootList,
			List<Document> nodeList) {
		for (Document fileRoot : fileRootList) {
			String fileName = fileRoot.getString("key");
			String fileId = fileRoot.getString("id");
			for (Document node : nodeList) {
				String nodeValue = node.getString("value");
				if (nodeValue.equals(fileName)) {
					List<String> nodeLinkedIds = (List<String>) node
							.get("linkedId");
					if (nodeLinkedIds.contains(fileId)) {
						continue;
					}
					String nodeId = node.getString("id");
					linkTwoNode(fileId, nodeId);
				}
			}
		}
	}

	private void linkTwoNode(String id1, String id2) {
		String userName1 = id1.split(":")[0];
		String userName2 = id2.split(":")[0];
		MongoCollection<Document> userCollection1 = accounts
				.getCollection(userName1);
		MongoCollection<Document> userCollection2 = accounts
				.getCollection(userName2);
		UpdateOptions updateOptions = new UpdateOptions();
		userCollection1.updateOne(new BasicDBObject("id", id1),
				new BasicDBObject("$push", new BasicDBObject("linkedId", id2)),
				updateOptions.upsert(true));
		userCollection2.updateOne(new BasicDBObject("id", id2),
				new BasicDBObject("$push", new BasicDBObject("linkedId", id1)),
				updateOptions.upsert(true));
	}

	public void saveXML(File file1, String file1FileName, String userName) {
		List<JsonNode> jList = null;
		// jList = new CSV2Graph().parseCSV(csvFile, userName, fileName);
		jList = new XML2Graph().parseXML(file1, userName, file1FileName);
		LinkedList<Document> allDocs = new LinkedList<Document>();
		for (JsonNode j : jList) {
			Document forInsert = new Document("id", j.id).append("key", j.key)
					.append("value", j.value);
			if (j.desc != null) {
				forInsert.append("desc", j.desc);
			}
			forInsert.append("linkedId", j.linkedId);
			allDocs.add(forInsert);
		}
		json.insertMany(allDocs);
		saveToIndexforsearch(jList);
	}

	public void saveOtherFiles(File file1, String file1FileName, String userName) {
		List<JsonNode> jList = null;
		jList = new Tika2Graph().parthFile(file1, userName, file1FileName);
		LinkedList<Document> allDocs = new LinkedList<Document>();
		for (JsonNode j : jList) {
			Document forInsert = new Document("id", j.id).append("key", j.key)
					.append("value", j.value);
			if (j.desc != null) {
				forInsert.append("desc", j.desc);
			}
			forInsert.append("linkedId", j.linkedId);
			allDocs.add(forInsert);
		}
		json.insertMany(allDocs);
		saveToIndexforsearch(jList);
	}

	public List<Document> findNodes(String keyword1) {
		MongoCursor<Document> mongoCursor = indexforsearch.find(
				new BasicDBObject("value", keyword1)).iterator();
		Map<String, Set<String>> collectionNameIdsMap = new HashMap<String, Set<String>>();

		List<Document> ret = new LinkedList<Document>();
		while (mongoCursor.hasNext()) {
			Document d = mongoCursor.next();
			String id = d.getString("id");
			String[] idSplited = id.split(":");
			String userName = idSplited[0];
			if (collectionNameIdsMap.containsKey(userName)) {
				Set<String> set = collectionNameIdsMap.get(userName);
				set.add(id);
			} else {
				// List<String> list = new LinkedList<String>();
				Set<String> set = new HashSet<String>();
				set.add(id);
				collectionNameIdsMap.put(userName, set);
			}
		}

		for (String userName : collectionNameIdsMap.keySet()) {
			Set<String> ids = collectionNameIdsMap.get(userName);
			MongoCollection<Document> userCollection = accounts
					.getCollection(userName);
			for (String id : ids) {
				Document d = userCollection.find(new BasicDBObject("id", id))
						.iterator().next();
				ret.add(d);
			}
		}
		return ret;
	}
}

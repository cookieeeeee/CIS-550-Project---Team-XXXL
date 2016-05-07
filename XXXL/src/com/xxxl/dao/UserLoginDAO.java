package com.xxxl.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.xxxl.bean.UserLogin;

public class UserLoginDAO {
	public static MongoDatabase accounts;
	public static MongoCollection<Document> json;
	public static MongoCollection<Document> login;
	static {
		MongoClient mongo = new MongoClient("localhost", 27017);
		accounts = mongo.getDatabase("accounts");
		login = accounts.getCollection("login");
	}

	public static void init(String userName) {
		json = accounts.getCollection(userName);
	}

	public ArrayList<JSONObject> getAccounts() {
		ArrayList<JSONObject> toReturn = new ArrayList<JSONObject>();
		// MongoClient mongo = new MongoClient("localhost", 27017);
		// MongoDatabase accounts = mongo.getDatabase("accounts");
		FindIterable<Document> account = login.find();
		MongoCursor<Document> accountCursor = account.iterator();
		while (accountCursor.hasNext()) {
			// System.out.println(accountCursor.next());
			String json = accountCursor.next().toJson();
			JSONObject jsonObj = new JSONObject(json);
			toReturn.add(jsonObj);
		}
		// mongo.close();
		return toReturn;
	}

	public void insertAccount(UserLogin userLogin) {
		MongoCollection<Document> login = accounts.getCollection("login");
		Map<String, Object> accountMap = new HashMap<String, Object>();
		accountMap.put("name", userLogin.getName());
		accountMap.put("pwd", userLogin.getPwd());
		Document account = new Document(accountMap);
		login.insertOne(account);
		accounts.createCollection(userLogin.getName());
	}

	public void insertFollows(String userName, String follows) {
		MongoCollection<Document> login = accounts.getCollection("login");
		// Document user = login.find(new Document("name",
		// userName)).iterator().next();
		login.updateOne(new Document("name", userName), new BasicDBObject(
				"$push", new BasicDBObject("follows", follows)),
				new UpdateOptions().upsert(true));
	}
}

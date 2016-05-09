package com.xxxl.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	public MongoCollection<Document> json;
	public static MongoCollection<Document> login;
	static {
		MongoClient mongo = new MongoClient("localhost", 27017);
		accounts = mongo.getDatabase("accounts");
		login = accounts.getCollection("login");
	}

	public UserLoginDAO(String name) {
		json = accounts.getCollection(name);
	}

	public ArrayList<JSONObject> getAccounts() {
		ArrayList<JSONObject> toReturn = new ArrayList<JSONObject>();
		FindIterable<Document> account = login.find();
		MongoCursor<Document> accountCursor = account.iterator();
		while (accountCursor.hasNext()) {
			String json = accountCursor.next().toJson();
			JSONObject jsonObj = new JSONObject(json);
			toReturn.add(jsonObj);
		}
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

	public boolean insertFollows(String userName, String follows) {
		// Insert to {name:follow, follows:[userName]}
		MongoCursor<Document> mongoCursor = login.find(
				new BasicDBObject("name", follows)).iterator();
		if (!mongoCursor.hasNext())
			return false;
		MongoCollection<Document> login = accounts.getCollection("login");
		login.updateOne(new Document("name", follows), new BasicDBObject(
				"$push", new BasicDBObject("follows", userName)),
				new UpdateOptions().upsert(true));
		return true;
	}
}

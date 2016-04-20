package com.xxxl.dao;

import java.util.ArrayList;

import org.bson.Document;
import org.json.JSONObject;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class UserLoginDAO {
	public ArrayList<JSONObject> getAccounts() {
		ArrayList<JSONObject> toReturn = new ArrayList<JSONObject>();
		MongoClient mongo = new MongoClient("localhost", 27017);
		MongoDatabase accounts = mongo.getDatabase("accounts");
		MongoCollection<Document> login = accounts.getCollection("login");
		FindIterable<Document> account = login.find();
		MongoCursor<Document> accountCursor = account.iterator();
		while (accountCursor.hasNext()) {
			// System.out.println(accountCursor.next());
			String json = accountCursor.next().toJson();
			JSONObject jsonObj = new JSONObject(json);
			toReturn.add(jsonObj);
		}
		mongo.close();
		return toReturn;
	}
}

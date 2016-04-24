package com.xxxl.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.json.JSONObject;

import com.xxxl.bean.UserLogin;
import com.xxxl.dao.JSONDAO;
import com.xxxl.dao.UserLoginDAO;
import com.xxxl.util.Tuple;

public class UserService {

	public boolean checkLogin(UserLogin userLogin) {
		UserLoginDAO userLoginDAO = new UserLoginDAO();
		ArrayList<JSONObject> accounts = userLoginDAO.getAccounts();
		for (JSONObject account : accounts) {
			if (account.get("name").equals(userLogin.getName())
					&& account.get("pwd").equals(userLogin.getPwd())) {
				return true;
			}
		}
		return false;
	}

	public boolean checkRegister(UserLogin userLogin) {
		UserLoginDAO userLoginDAO = new UserLoginDAO();
		if (userLogin.getName().equals("login")) {
			return false;
		}
		ArrayList<JSONObject> accounts = userLoginDAO.getAccounts();
		for (JSONObject account : accounts) {
			if (account.get("name").equals(userLogin.getName())) {
				return false;
			}
		}
		return true;
	}

	public void insertAccount(UserLogin userLogin) {
		UserLoginDAO userLoginDAO = new UserLoginDAO();
		userLoginDAO.insertAccount(userLogin);
	}

	public void extractJSON(File jsonFile, String fileName, String name) {
		BufferedReader reader;
		// char[] buffer = new char[4096];
		String output = "";
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(jsonFile), "UTF-8"));
			// reader.read(buffer);
			String line;
			while ((line = reader.readLine()) != null) {
				output += line;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// JSONObject jsonObject = new JSONObject(output);
		JSONDAO jDAO = new JSONDAO();
		jDAO.saveJSON(output, fileName, name);
		// jDAO.saveJSON(jsonObject);
	}

	public List<String> search(String keyword1, String keyword2,
			String userName, List<String> names) {
		LinkedList<String> ret = new LinkedList<String>();
		List<String> allNames = new LinkedList<String>(names);
		allNames.add(userName);
		JSONDAO jDAO = new JSONDAO();
		List<Tuple<String, Document>> namdNDocs = jDAO.findDocs(allNames);
		Map<String, Map<String, LinkedList<String>>> parents = new HashMap<String, Map<String, LinkedList<String>>>();
		// Set following relation

		for (Tuple<String, Document> nameNDoc : namdNDocs) {
			// Map<String, String> parent = new HashMap<String, String>();
			String curUName = nameNDoc.t0;
			Document doc = nameNDoc.t1;
			parents.put(curUName + "_" + doc.get("__fileName__").toString(),
					getParent(doc));
		}
		for (String fileName : parents.keySet()) {
			Map<String, LinkedList<String>> parent = parents.get(fileName);
			if (parent.containsKey(keyword1)) {
				getParentStr(keyword1, keyword1, parent, ret);
			}
			if(parent.containsKey(keyword2)){
				getParentStr(keyword2, keyword2, parent, ret);
			}
		}
		return ret;
	}

	private void getParentStr(String curNode, String forRet,
			Map<String, LinkedList<String>> parent, LinkedList<String> allPaths) {
		if (parent.containsKey(curNode)) {
			for (String s : parent.get(curNode)) {
				forRet = s + ":" + forRet;
				getParentStr(s.split("->")[0], forRet, parent, allPaths);
			}
		} else {
			allPaths.add(forRet);
		}
	}

	private Map<String, LinkedList<String>> getParent(Document doc) {
		Map<String, LinkedList<String>> parent = new HashMap<String, LinkedList<String>>();
		for (String key : doc.keySet()) {
			if (key.equals("__fileName__") || key.equals("_id")) {
				continue;
			}
			// parent.put(doc.get(key).toString(), key.split("->")[0]);
			if (parent.containsKey(doc.get(key).toString())) {
				LinkedList<String> parentList = parent.get(doc.get(key)
						.toString());
				// parentList.add(key.split("->")[0]);
				parentList.add(key);
			} else {
				LinkedList<String> parentList = new LinkedList<String>();
				// parentList.add(key.split("->")[0]);
				parentList.add(key);
				parent.put(doc.get(key).toString(), parentList);
			}
		}
		return parent;
	}

	public void initDAO(String name) {
		UserLoginDAO.init(name);
	}
}

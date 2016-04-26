package com.xxxl.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.json.JSONObject;

import com.xxxl.bean.UserLogin;
import com.xxxl.dao.JSONDAO;
import com.xxxl.dao.UserLoginDAO;
import com.xxxl.util.SearchHelper;

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

	public void extractJSON(File jsonFile, String fileName, String userName) {
		BufferedReader reader;
		String output = "";
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(jsonFile);
			reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
			String line;
			while ((line = reader.readLine()) != null) {
				output += line;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		JSONDAO jDAO = new JSONDAO();
		JSONDAO.json = UserLoginDAO.accounts.getCollection(userName);
		jDAO.saveJSON(output, fileName, userName);
	}

	public void extractCSV(File csvFile, String fileName, String userName) {
		JSONDAO jDAO = new JSONDAO();
		JSONDAO.json = UserLoginDAO.accounts.getCollection(userName);
		jDAO.saveCSV(csvFile, fileName, userName);
	}

	public void linking(String userName, List<String> otherNames) {
		JSONDAO jDAO = new JSONDAO();
		List<String> allUserNames = new LinkedList<String>(otherNames);
		allUserNames.add(userName);
		jDAO.linking(allUserNames);
	}

	public List<String> search(String keyword1, String keyword2,
			String userName, List<String> otherUserNames) {
		LinkedList<String> ret = new LinkedList<String>();
		List<String> allUserNames = new LinkedList<String>(otherUserNames);
		allUserNames.add(userName);
		JSONDAO jDAO = new JSONDAO();
		LinkedList<Document> allDocs = (LinkedList<Document>) jDAO
				.findDocs(allUserNames);
		// Set following relation
		Map<String, Document> docsMap = new HashMap<String, Document>();
		for (Document curDoc : allDocs) {
			docsMap.put(curDoc.getString("id"), curDoc);
		}
		for (Document curDoc : allDocs) {
			if (curDoc.get("value").equals(keyword1)) {
				SearchHelper helper = new SearchHelper();
				String result = helper.findPath(docsMap, curDoc, keyword2, 0,
						new HashSet<String>());
				System.out.println(result);
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
			if (parent.containsKey(doc.get(key).toString())) {
				LinkedList<String> parentList = parent.get(doc.get(key)
						.toString());
				parentList.add(key);
			} else {
				LinkedList<String> parentList = new LinkedList<String>();
				parentList.add(key);
				parent.put(doc.get(key).toString(), parentList);
			}
		}
		return parent;
	}

	public void initDAO(String name) {
		UserLoginDAO.init(name);
	}

	public void extractXML(File file1, String file1FileName, String userName) {
		JSONDAO jDAO = new JSONDAO();
		JSONDAO.json = UserLoginDAO.accounts.getCollection(userName);
		jDAO.saveXML(file1, file1FileName, userName);
	}

	public void extractOtherFiles(File file1, String file1FileName,
			String userName) {
		JSONDAO jDAO = new JSONDAO();
		JSONDAO.json = UserLoginDAO.accounts.getCollection(userName);
		jDAO.saveOtherFiles(file1, file1FileName, userName);
	}
}

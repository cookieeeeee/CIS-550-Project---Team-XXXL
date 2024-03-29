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
import org.json.JSONArray;
import org.json.JSONObject;

import com.xxxl.bean.UserLogin;
import com.xxxl.dao.JSONDAO;
import com.xxxl.dao.UserLoginDAO;
import com.xxxl.util.SearchHelper;

public class UserService {

	public boolean checkLogin(UserLogin userLogin) {
		UserLoginDAO userLoginDAO = new UserLoginDAO(userLogin.getName());
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
		UserLoginDAO userLoginDAO = new UserLoginDAO(userLogin.getName());
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
		UserLoginDAO userLoginDAO = new UserLoginDAO(userLogin.getName());
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

		JSONDAO jDAO = new JSONDAO(userName);
		// JSONDAO.json = UserLoginDAO.accounts.getCollection(userName);
		jDAO.saveJSON(output, fileName, userName);
	}

	public void extractCSV(File csvFile, String fileName, String userName) {
		JSONDAO jDAO = new JSONDAO(userName);
		// JSONDAO.json = UserLoginDAO.accounts.getCollection(userName);
		jDAO.saveCSV(csvFile, fileName, userName);
	}

	public void linking(String name) {
		JSONDAO jDAO = new JSONDAO(name);
		// List<String> allUserNames = new LinkedList<String>(otherNames);
		// allUserNames.add(userName);
		List<String> allUserNames = jDAO.findAllUserNames();
		jDAO.linking(allUserNames);
	}

	public List<List<Document>> search(String keyword1, String keyword2,
			String userName, List<String> otherUserNames) {
		List<List<Document>> ret = new LinkedList<List<Document>>();
		List<String> allUserNames = new LinkedList<String>(otherUserNames);
		allUserNames.add(userName);
		JSONDAO jDAO = new JSONDAO(userName);
		LinkedList<Document> allDocs = (LinkedList<Document>) jDAO
				.findDocs(allUserNames);
		// Set following relation
		Map<String, Document> docsMap = new HashMap<String, Document>();
		for (Document curDoc : allDocs) {
			docsMap.put(curDoc.getString("id"), curDoc);
		}
		List<Document> nodesIncludingKeyword1 = findNodeIncludingKeyword1(
				keyword1, userName, allUserNames);
		for (Document nodeIncludingkey1 : nodesIncludingKeyword1) {
			SearchHelper helper = new SearchHelper();
			List<Document> resultList = helper.findPath(docsMap,
					nodeIncludingkey1, keyword2, 0, new HashSet<String>());
			// ret.addAll(resultList);
			ret.add(resultList);
		}
		return ret;
	}

	private List<Document> findNodeIncludingKeyword1(String keyword1,
			String userName, List<String> allUsers) {
		JSONDAO jsondao = new JSONDAO(userName);
		return jsondao.findNodes(keyword1, allUsers);
	}

	public void extractXML(File file1, String file1FileName, String userName) {
		JSONDAO jDAO = new JSONDAO(userName);
		jDAO.saveXML(file1, file1FileName, userName);
	}

	public void extractOtherFiles(File file1, String file1FileName,
			String userName) {
		JSONDAO jDAO = new JSONDAO(userName);
		jDAO.saveOtherFiles(file1, file1FileName, userName);
	}

	public boolean followUser(String userName, String follows) {
		if (new UserLoginDAO(userName).insertFollows(userName, follows))
			return true;
		else
			return false;
	}

	public String createResultStr(List<Document> docList) {
		StringBuilder ret = new StringBuilder();
		Document firstDoc = docList.get(docList.size() - 1);
		String[] prefix = firstDoc.getString("id").split(":");
		ret.append(prefix[0] + "->" + prefix[1].replace("_", ".") + "::"
				+ firstDoc.getString("key") + "::"
				+ firstDoc.getString("value"));
		for (int i = docList.size() - 2; i >= 0; i--) {
			Document d = docList.get(i);
			prefix = d.getString("id").split(":");
			String path = "||" + prefix[0] + "->" + prefix[1].replace("_", ".")
					+ "::" + d.getString("key") + "::" + d.getString("value");
			ret.append(path);
		}
		return ret.toString();
	}

	public String createPathList(List<List<Document>> docLists) {
		JSONArray ret = new JSONArray();
		for (List<Document> docList : docLists) {
			if (docList == null)
				continue;
			JSONArray list = new JSONArray();
			for (Document d : docList) {
				list.put(d.getString("id"));
			}
			ret.put(list);
		}
		return ret.toString();
	}
}

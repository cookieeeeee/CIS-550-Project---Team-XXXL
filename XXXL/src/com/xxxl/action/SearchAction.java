package com.xxxl.action;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.xxxl.service.UserService;

public class SearchAction extends ActionSupport {
	private String firstKeyword;
	private String secondKeyword;

	public void setFirstKeyword(String firstKeyword) {
		this.firstKeyword = firstKeyword;
	}

	public void setSecondKeyword(String secondKeyword) {
		this.secondKeyword = secondKeyword;
	}

	@Override
	public String execute() throws Exception {
		Map<String, Object> session = ActionContext.getContext().getSession();
		String userName = (String) session.get("name");
		List<String> names = (List<String>) session.get("follows");
		// names.add(userName);
		UserService userService = new UserService();
		List<List<Document>> docLists = userService.search(firstKeyword,
				secondKeyword, userName, names);
		List<String> searchResult = new LinkedList<String>();
		for (List<Document> docList : docLists) {
			String result = userService.createResultStr(docList);
			searchResult.add(result);
		}
		String pathList = userService.createPathList(docLists);
		ActionContext.getContext().put("searchResult", searchResult);
		ActionContext.getContext().put("pathList", pathList);
		return SUCCESS;
	}
}

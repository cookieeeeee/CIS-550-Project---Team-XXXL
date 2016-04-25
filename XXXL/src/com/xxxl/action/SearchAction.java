package com.xxxl.action;

import java.util.List;
import java.util.Map;

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
		List<String> searchResult = new UserService().search(firstKeyword,
				secondKeyword, userName, names);
		ActionContext.getContext().put("searchResult", searchResult);
		return SUCCESS;
	}
}

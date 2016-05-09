package com.xxxl.action;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.xxxl.service.UserService;

public class FollowAction extends ActionSupport {
	String followName;

	public void setFollowName(String followName) {
		this.followName = followName;
	}

	@Override
	public String execute() throws Exception {
		String userName = (String) ActionContext.getContext().getSession()
				.get("name");
		if (new UserService().followUser(userName, followName))
			ActionContext.getContext().put("followsHint",
					"Successfully followed user: " + followName);
		else
			ActionContext.getContext().put("followsHint", "User not exist");
		return SUCCESS;
	}
}

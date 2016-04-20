package com.xxxl.action;

import com.opensymphony.xwork2.ActionSupport;
import com.xxxl.bean.UserLogin;
import com.xxxl.service.UserService;

public class UserAction extends ActionSupport {
	private UserLogin userLogin = new UserLogin();

	public UserLogin getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(UserLogin userLogin) {
		this.userLogin = userLogin;
	}

	public String login() {
		UserService userService = new UserService();
		if (userService.checkLogin(userLogin)) {
			return SUCCESS;
		} else {
			return ERROR;
		}
	}
}

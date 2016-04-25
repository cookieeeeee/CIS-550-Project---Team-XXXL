package com.xxxl.action;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.xxxl.bean.UserLogin;
import com.xxxl.service.UserService;

public class Register extends ActionSupport {

	private UserLogin userLogin = new UserLogin();
	private String pwd;

	public UserLogin getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(UserLogin userLogin) {
		this.userLogin = userLogin;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	@Override
	public String execute() throws Exception {
		UserService userService = new UserService();
		if (pwd.equals(userLogin.getPwd())
				&& userService.checkRegister(userLogin)) {
			userService.insertAccount(userLogin);
			return SUCCESS;
		} else {
			ActionContext.getContext().put("hint", "Password mismatching");
			return ERROR;
		}
	}
}

package com.xxxl.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;
import org.bson.Document;

import com.mongodb.client.MongoCursor;
import com.opensymphony.xwork2.ActionSupport;
import com.xxxl.bean.UserLogin;
import com.xxxl.dao.UserLoginDAO;
import com.xxxl.service.DisplayNodes;
import com.xxxl.service.UserService;

public class UserAction extends ActionSupport implements ServletRequestAware,
		SessionAware {
	private UserLogin userLogin = new UserLogin();
	private HttpServletRequest request;
	private Map<String, Object> session;

	public UserLogin getUserLogin() {
		return userLogin;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void setUserLogin(UserLogin userLogin) {
		this.userLogin = userLogin;
	}

	public String login() {
		UserService userService = new UserService();
		if (userService.checkLogin(userLogin)) {
			session.put("name", userLogin.getName());
			userService.initDAO(userLogin.getName());
			session.put("jsonStr", new DisplayNodes().displayFiles());
			return SUCCESS;
		} else {
			// ActionContext.getContext().put("mismatch",
			// "Password mismatching");
			request.setAttribute("mismatch", "Password mismatching");
			return ERROR;
		}
	}

	public String logout() {
		session.put("name", null);
		return SUCCESS;
	}

	@Override
	public void setSession(Map<String, Object> arg0) {
		this.session = arg0;
	}
}

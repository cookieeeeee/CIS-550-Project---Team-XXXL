package com.xxxl.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.util.ServletContextAware;

import com.opensymphony.xwork2.ActionSupport;
import com.xxxl.bean.UserLogin;
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
			return SUCCESS;
		} else {
			// ActionContext.getContext().put("mismatch",
			// "Password mismatching");
			request.setAttribute("mismatch", "Password mismatching");
			return ERROR;
		}
	}

	@Override
	public void setSession(Map<String, Object> arg0) {
		this.session = arg0;
	}
}

package com.xxxl.config;

import com.opensymphony.xwork2.ActionSupport;
import com.xxxl.bean.User;

public class UserAction extends ActionSupport {
	private User user = new User();

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String login() {
		System.out.println("asdf");
		System.out.println(user.getName());
		System.out.println(user.getPwd());
		return "success";
	}
}

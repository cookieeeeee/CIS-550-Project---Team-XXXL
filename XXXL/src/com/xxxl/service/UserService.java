package com.xxxl.service;

import java.util.ArrayList;

import org.json.JSONObject;

import com.xxxl.bean.UserLogin;
import com.xxxl.dao.UserLoginDAO;

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
}

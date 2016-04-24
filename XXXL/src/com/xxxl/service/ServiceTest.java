package com.xxxl.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import com.xxxl.bean.UserLogin;
import com.xxxl.dao.UserLoginDAO;

public class ServiceTest {
	@Test
	public void testCheck() {
		UserService userService = new UserService();
		UserLogin userLogin = new UserLogin();
		userLogin.setName("zhixu");
		userLogin.setPwd("123456");
		assertTrue(userService.checkLogin(userLogin));
		userLogin.setName("xxx");
		assertFalse(userService.checkLogin(userLogin));
	}

	// @Test
	// public void testExtractJson() {
	// File file = new File("C:/Users/Xu/Desktop/weather");
	// UserService userService = new UserService();
	// userService.extractJSON(file, "asdf", "zzz");
	// }

	// @Test
	public void testJSON() {
		String jStr = new String("{1:[1,2],abc:1}");
		JSONObject j = new JSONObject(jStr);
		JSONArray a = (JSONArray) j.get("1");
		for (int i = 0; i <= a.length(); i++) {

		}
	}

	@Test
	public void testSearch() {
		UserService userService = new UserService();
		UserLoginDAO.init("zhixu");
		List<String> names = new LinkedList<String>();
		names.add("tui");
		userService.search("4", "b", "zhixu", names);
	}
}

package com.xxxl.service;

import static org.junit.Assert.*;

import org.junit.Test;

import com.xxxl.bean.UserLogin;

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
}

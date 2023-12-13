package com.poly.service;

import com.poly.bean.Auth;

public interface AuthService {

	public Auth create(Auth auth);
	
	public Auth getAuth(String username);
}

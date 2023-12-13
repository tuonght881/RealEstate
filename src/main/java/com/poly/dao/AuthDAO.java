package com.poly.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.poly.bean.Auth;

public interface AuthDAO extends JpaRepository<Auth, Integer>{

	@Query(value="select * from auth where users = ?1", nativeQuery = true)
	public Auth getAuth(String username);
}

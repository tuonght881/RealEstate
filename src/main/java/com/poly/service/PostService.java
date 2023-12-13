package com.poly.service;

import java.util.List;

import javax.mail.MessagingException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import com.fasterxml.jackson.databind.JsonNode;
import com.poly.bean.Post;

public interface PostService {
	
	public Page<Post> getPostExpired(String username, Pageable p);
	
	public List<Post> getPostExpiredUser(String username);
	
	public Page<Post> getPostDelete(String username, Pageable p);
	
	public Page<Post> getPostDelete(Pageable p);
	
	public Page<Post> searchPost(String title, String address, String province, Integer type, Pageable p);
	
	public List<Post> getAll();
	
	public List<Post> getAllTop18(int post_id);
	
	public Page<Post> getPageAll(Pageable p);
	
	public List<Post> getAllDiamond();
	
	public List<Post> getAllHotsNew();
	
	public List<Post> getAllOften();
	
	public List<Post> getAllPostsForYou();
	
	public List<Post> getAllByUserId(String username);
	
	public Post getPostDesc();

	public Post getFindByid(Integer id);
	
	public Post getFindIntroducingThePost();

	public Post Create(JsonNode p);
	
	public Post UpdateJson(JsonNode p);

	public Post Update(Post p);

	public void Delete(Integer id);
	
	public void checkPostExpired() throws MessagingException;
	
	public Integer SoftDeletePost(Integer id);
	
	public Integer SoftActivePost(String active, Integer id);
	
	public Page<Post> getPostPageWithType(@Param("type_id") int id, Pageable p);
}

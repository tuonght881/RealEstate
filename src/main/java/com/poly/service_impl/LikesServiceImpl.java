package com.poly.service_impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poly.bean.Likes;
import com.poly.dao.LikesDAO;
import com.poly.service.LikeService;

@Service
public class LikesServiceImpl implements LikeService{

	@Autowired
	LikesDAO dao;
	
	@Override
	public List<Likes> getAll() {
		// TODO Auto-generated method stub
		return dao.findAll();
	}

	@Override
	public List<Likes> getTop4PostLikes(String username) {
		// TODO Auto-generated method stub
		List<Likes> Likes = dao.getTop4PostLikes(username);
		if(Likes != null) {
			return Likes;
		}else {
			return null;
		}
		
	}

	@Override
	public Likes Create(Likes l) {
		// TODO Auto-generated method stub
		return dao.save(l);
	}

	@Override
	public Likes Update(Likes l) {
		// TODO Auto-generated method stub
		return dao.save(l);
	}

	@Override
	public void Delete(Integer id) {
		// TODO Auto-generated method stub
		dao.deleteById(id);
	}

	@Override
	public Likes findById(Integer id) {
		// TODO Auto-generated method stub
		Likes l = dao.findById(id).get();
		if(l != null) {
			return l;
		}else {
			return null;
		}
	}

	@Override
	public Likes findByUserIDAndPostID(String user, Integer post) {
		// TODO Auto-generated method stub
		return dao.getPostLikeByUserIDAndPostID(user, post);
	}

	@Override
	public Page<Likes> getAllPostLikes(String username, Pageable p) {
		// TODO Auto-generated method stub
		return dao.getAllPostLikes(username, p);
	}

	@Override
	public void DeleteByUserIDAndPostID(Integer post) {
		dao.deleteByUserIdAndPostId(post);
	}

	@Override
	public Likes CreateJsonNode(JsonNode likeData) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Likes l = mapper.convertValue(likeData, Likes.class);
		return dao.save(l);
	}

	@Override
	public List<Likes> getPostLikesByUser(String username) {
		// TODO Auto-generated method stub
		return dao.getAllPostLikesByUser(username);
	}

	@Override
	public Page<Likes> getAllPostLikesSortDesc(String username, Pageable p) {
		// TODO Auto-generated method stub
		return dao.getAllPostLikesSortDesc(username, p);
	}

	@Override
	public Page<Likes> getAllPostLikesAsc(String username, Pageable p) {
		// TODO Auto-generated method stub
		return dao.getAllPostLikesSortAsc(username, p);
	}





}

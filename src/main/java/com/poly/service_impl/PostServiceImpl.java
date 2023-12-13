package com.poly.service_impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poly.bean.Post;
import com.poly.dao.PostDAO;
import com.poly.service.PostService;
import com.poly.util.MailerService;

@Service
public class PostServiceImpl implements PostService{

	@Autowired
	PostDAO dao;
	
	@Autowired
	MailerService mailService;
	

	@Override
	public Post getFindByid(Integer id) {
		// TODO Auto-generated method stub
		Post p = dao.findById(id).get();
		if(p != null) {
			return p;
		}else {
			return null;
		}
	}

	@Override
	public Post Create(JsonNode p) {
		// TODO Auto-generated method stub
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Post post = mapper.convertValue(p, Post.class);
		return dao.save(post);
	}

	@Override
	public Post Update(Post p) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Post post = mapper.convertValue(p, Post.class);
		return dao.save(p);
	}
	
	public Page<Post> getPostPageWithType(int id, Pageable p) {
		// TODO Auto-generated method stub
		return dao.getPostPageWithType(id, p);
	}

	@Override
	public void Delete(Integer id) {
		dao.deleteById(id);
	}

	@Override
	public List<Post> getAll() {
		// TODO Auto-generated method stub
		return dao.getPostsAll();
	}

	@Scheduled(cron = "0 57 17 * * *")
	@Override
	public void checkPostExpired() throws MessagingException{
		List<Post> list_post = dao.findAll();
		Date now = new Date();
		SimpleDateFormat x =  new SimpleDateFormat ("dd/MM/yyyy");
		for(Post p : list_post) {
			String formattedEndDate = x.format(p.getEnd_date());
			String formattedNow = x.format(now);
			
			String[] partsEndDate = formattedEndDate.split("/");
			String[] partsNow = formattedNow.split("/");

			int dayEndDate = Integer.parseInt(partsEndDate[0]);
			int monthEndDate = Integer.parseInt(partsEndDate[1]);
			int yearEndDate = Integer.parseInt(partsEndDate[2]);
			
			int dayNow = Integer.parseInt(partsNow[0]);
			int monthNow = Integer.parseInt(partsNow[1]);
			int yearNow = Integer.parseInt(partsNow[2]);
			
			if (dayEndDate <= dayNow && monthEndDate <= monthNow && yearEndDate <= yearNow) {
				p.setActive(false);
				Update(p);
				mailService.sendPostExpired(p.getUsers_id().getEmail(), "Bài viết của quý khách đã hết hạn", p.getUsers_id().getFullname(), p.getPost_title(), formattedEndDate);
			}
		}
		
		
	}

	@Override
	public Page<Post> getPostExpired(String username, Pageable p) {
		// TODO Auto-generated method stub
		return dao.getPostsExpired(username, p);
	}

	@Override
	public Integer SoftDeletePost(Integer id) {
		// TODO Auto-generated method stub
		return dao.softDeletePost(id);
	}

	@Override
	public Page<Post> getPostDelete(String username, Pageable p) {
		// TODO Auto-generated method stub
		return dao.getPostsDelete(username, p);
	}

	@Override
	public Post UpdateJson(JsonNode p) {
		// TODO Auto-generated method stub
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Post post = mapper.convertValue(p, Post.class);
		return dao.save(post);
	}

	@Override
	public Post getPostDesc() {
		// TODO Auto-generated method stub
		return dao.getPostDesc();
	}

	@Override
	public List<Post> getAllByUserId(String username) {
		// TODO Auto-generated method stub
		return dao.getPostsByUserId(username);
	}

	@Override
	public Post getFindIntroducingThePost() {
		// TODO Auto-generated method stub
		return dao.getFindIntroducingThePost();
	}

	@Override
	public List<Post> getAllDiamond() {
		// TODO Auto-generated method stub
		return dao.getPostsDiamond();
	}

	@Override
	public List<Post> getAllPostsForYou() {
		// TODO Auto-generated method stub
		return dao.getPostsForYou();
	}

	@Override
	public Page<Post> searchPost(String title, String address, String province, Integer type, Pageable p) {
		// TODO Auto-generated method stub
		return dao.searchPost(title, address, province, type, p);
	}
//
	@Override
	public Page<Post> getPageAll(Pageable p) {
		// TODO Auto-generated method stub
		return dao.getPostsAll(p);
	}

	@Override
	public Page<Post> getPostDelete(Pageable p) {
		// TODO Auto-generated method stub
		return dao.getHistoryDeletePostsDelete(p);
	}

	@Override
	public List<Post> getAllHotsNew() {
		// TODO Auto-generated method stub
		return dao.getListPostDesc();
	}

	@Override
	public List<Post> getAllOften() {
		// TODO Auto-generated method stub
		return dao.getPostsOften();
	}

	@Override
	public Integer SoftActivePost(String active, Integer id) {
		// TODO Auto-generated method stub
		return dao.softActionPost(active, id);
	}

	@Override
	public List<Post> getAllTop18(int post_id) {
		// TODO Auto-generated method stub
		return dao.getListTop18PostDesc(post_id);
	}

	@Override
	public List<Post> getPostExpiredUser(String username) {
		// TODO Auto-generated method stub
		return dao.getPostsExpiredUser(username);
	}

}

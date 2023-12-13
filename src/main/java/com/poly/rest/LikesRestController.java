package com.poly.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.poly.bean.Likes;
import com.poly.bean.Users;
import com.poly.service.LikeService;
import com.poly.util.SessionService;

@RestController
public class LikesRestController {

	@Autowired
	LikeService likeService;
	
	@Autowired
	SessionService ss;
	
	@GetMapping("/likes")
	public List<Likes> getAll(){
		Users u = ss.getAttribute("user");
		return likeService.getTop4PostLikes(u.getUsername());
	}
	
	@GetMapping("/find-by-post-likes")
	public Page<Likes> getPostLike(@RequestParam(defaultValue = "1") int page) {
		Users u = ss.getAttribute("user");
		Pageable pageable = PageRequest.of(page - 1, 4);
		return likeService.getAllPostLikes(u.getUsername(), pageable);
	}
	
	@GetMapping("/find-by-post-likes-sort-desc")
	public Page<Likes> getPostLikeSortDesc(@RequestParam String user, @RequestParam(defaultValue = "1") int page) {
	
		Pageable pageable = PageRequest.of(page - 1, 4);
		return likeService.getAllPostLikesSortDesc(user, pageable);
	}
	
	@GetMapping("/find-by-post-likes-sort-asc")
	public Page<Likes> getPostLikeSortAsc(@RequestParam String user, @RequestParam(defaultValue = "1") int page) {
	
		Pageable pageable = PageRequest.of(page - 1, 4);
		return likeService.getAllPostLikesAsc(user, pageable);
	}
	
	@GetMapping("/find-by-post-likes-user")
	public List<Likes> getPostLikeUser() {
		Users u = ss.getAttribute("user");
		return likeService.getPostLikesByUser(u.getUsername());
	}
	
	@GetMapping("/find-by-post-likes/{username}/{post_id}")
	public Likes getPostLikeUserAndPost(@PathVariable("username") String username,@PathVariable("post_id") Integer id) {
		return likeService.findByUserIDAndPostID(username, id);
	}
	
	@PostMapping("/likes-add")
	public Likes getCreate(@RequestBody JsonNode likeData) {
		return likeService.CreateJsonNode(likeData);
	}
	
	@DeleteMapping("/likes-delete/{post_id}")
	public void getDelete(@PathVariable("post_id") Integer id) {
		likeService.DeleteByUserIDAndPostID(id);
	}
}

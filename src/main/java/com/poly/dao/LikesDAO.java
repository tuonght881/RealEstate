package com.poly.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.poly.bean.Likes;

public interface LikesDAO extends JpaRepository<Likes, Integer>{

	@Query(value="select top 4 * from likes where users = ?1 order by likes_id desc", nativeQuery = true)
	public List<Likes> getTop4PostLikes(String username);
	
	@Query(value="select * from likes where users = ?1", nativeQuery = true)
	public Page<Likes> getAllPostLikes(String user, Pageable p);
	
	@Query(value="select * from likes where users = ?1 order by likes_id Desc", nativeQuery = true)
	public Page<Likes> getAllPostLikesSortDesc(String user, Pageable p);
	
	@Query(value="select * from likes where users = ?1 order by likes_id Asc", nativeQuery = true)
	public Page<Likes> getAllPostLikesSortAsc(String user, Pageable p);
	
	@Query(value="select * from likes where users = ?1", nativeQuery = true)
	public List<Likes> getAllPostLikesByUser(String user);
	
	@Query(value="select * from likes where users = ?1 and post_id = ?2", nativeQuery = true)
	public Likes getPostLikeByUserIDAndPostID(String user, Integer post);

	@Modifying
	@Transactional
	@Query(value = "delete likes where post_id = ?1", nativeQuery = true)
	public void deleteByUserIdAndPostId(Integer post);
}

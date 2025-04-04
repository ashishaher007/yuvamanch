package com.ymanch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ymanch.entity.PostMention;
import com.ymanch.entity.Posts;

@Repository
public interface PostMentionRepository extends JpaRepository<PostMention, Long> {

	void deleteByPost(Posts postEntity);
	
}

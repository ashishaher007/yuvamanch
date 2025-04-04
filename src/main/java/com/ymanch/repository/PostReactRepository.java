package com.ymanch.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ymanch.entity.PostReact;
import com.ymanch.entity.Posts;
import com.ymanch.entity.User;

@Repository
public interface PostReactRepository extends JpaRepository<PostReact, Long> {

	Optional<PostReact> findByUserAndPosts(User userData, Posts postData);

	

}

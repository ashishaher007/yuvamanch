package com.ymanch.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ymanch.entity.UserSavedPost;

@Repository
public interface UserSavePostRepository extends JpaRepository<UserSavedPost, Long> {

	UserSavedPost findByUserUserIdAndPostPostId(long userId, long postId);

}

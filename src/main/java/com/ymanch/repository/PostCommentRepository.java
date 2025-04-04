package com.ymanch.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ymanch.entity.PostComment;
import com.ymanch.model.PostIndexComments;
import com.ymanch.model.PostIndexPage;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
	
	@Query("SELECT COUNT(c) FROM PostComment c WHERE c.posts.postId = :postId")
    Long getCommentCountByPostId(@Param("postId") Long postId);


	@Query("SELECT new com.ymanch.model.PostIndexComments( " +
		       "u.userProfileImagePath, " +
		       "CAST(c.commentCreatedAt AS string), " +  // Avoid DATE_FORMAT issues
		       "c.comment, " +
		       "CONCAT(u.userFirstName, ' ', u.userLastName), " +
		       "COALESCE(c.parentComment.postCommentId, 0), " + // Ensure it's a Long
		       "c.postCommentId) " +
		       "FROM PostComment c " +
		       "JOIN c.user u " +
		       "WHERE c.posts.postId = :postId " +
		       "ORDER BY c.commentCreatedAt ASC")
		List<PostIndexComments> findAllCommentsByPostId(@Param("postId") Long postId);

	

	

}

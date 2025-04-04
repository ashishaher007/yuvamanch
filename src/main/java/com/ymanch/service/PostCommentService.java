package com.ymanch.service;

import org.springframework.http.ResponseEntity;

import com.ymanch.entity.PostComment;
import com.ymanch.exception.ResourceNotFoundException;

public interface PostCommentService {

	ResponseEntity<Object> storeComment(long userId, long postId, PostComment comment) throws ResourceNotFoundException;

	ResponseEntity<Object> storeCommentOnComment(long userId, long postId, long commentId, PostComment comment)
			throws ResourceNotFoundException;

	ResponseEntity<Object> removeParentComment(long userId, long postCommentId) throws ResourceNotFoundException;

	ResponseEntity<Object> getAllCommentsAndReact(long postId);

}

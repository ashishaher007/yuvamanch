package com.ymanch.service;

import org.springframework.http.ResponseEntity;

import com.ymanch.entity.PostReact;
import com.ymanch.exception.ResourceNotFoundException;
import com.ymanch.model.PostReactUpdate;

import jakarta.validation.Valid;

public interface PostReactService {

	ResponseEntity<Object> storeReactOnPost(long reactId, long postId, @Valid PostReact postReactDetails)
			throws ResourceNotFoundException;

	ResponseEntity<Object> updateReactOnPost(long userId, PostReactUpdate updateReact) throws ResourceNotFoundException;

	ResponseEntity<Object> deleteReactOnPost(long userId, long postId);

}

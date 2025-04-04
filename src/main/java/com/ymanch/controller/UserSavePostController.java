package com.ymanch.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ymanch.exception.ResourceNotFoundException;
import com.ymanch.helper.CheckUserStatus;
import com.ymanch.service.AccessControlService;
import com.ymanch.service.PostService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ymanch/users/savepost")
@Slf4j
public class UserSavePostController {

	private PostService postService;
	private AccessControlService accessControlService;

	public UserSavePostController(PostService postService, AccessControlService accessControlService) {
		super();
		this.postService = postService;
		this.accessControlService = accessControlService;
	}

	@Operation(summary = "Save Post Api", description = "This API is used to save a post created by a user")
	@PostMapping("/savePost/{userId}/{postId}")
	@CheckUserStatus
	public ResponseEntity<Object> savePost(@PathVariable long userId, @PathVariable long postId) {
		log.info("***** Inside UserSavePostController - savePost *****");
		accessControlService.verifyUserAccess(userId);
		return postService.savePost(userId, postId);
	}

	@Operation(summary = "Saved Post Api", description = "This API is used to get saved  post stored by a user")
	@GetMapping("/getSavedPost/{userId}")
	@CheckUserStatus
	public ResponseEntity<Object> getSavedPost(@PathVariable long userId, @RequestParam(defaultValue = "5") int size,
			@RequestParam(defaultValue = "0") int page) {
		log.info("***** Inside UserSavePostController - getSavedPost *****");
		accessControlService.verifyUserAccess(userId);
		return postService.getSavedPost(userId, size, page);
	}

	@Operation(summary = "Delete User Saved Post Api", description = "This API is used to remove a user's saved post")
	@DeleteMapping("/deletSavedPost/{userId}/{postId}")
	ResponseEntity<Object> deletPost(@PathVariable long userId, @PathVariable long postId)
			throws ResourceNotFoundException {
		log.info("***** Inside - AdminPostController - deletSavedPost *****");
		accessControlService.verifyUserAccess(userId);
		return postService.deletSavedPost(userId,postId);
	}

}

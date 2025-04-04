package com.ymanch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ymanch.entity.PostComment;
import com.ymanch.exception.ResourceNotFoundException;
import com.ymanch.helper.CheckUserStatus;
import com.ymanch.service.AccessControlService;
import com.ymanch.service.PostCommentService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ymanch/users/postcomment")
@Slf4j
public class PostCommentController {

	@Autowired
	private PostCommentService commentService;
	
	@Autowired
	private AccessControlService accessControlService;

	@Operation(summary = "Add Parent Comment Api", description = "This API is used to add a parent comment on Post")
	@PostMapping("/addCommentOnPost/{userId}/{postId}")
	@CheckUserStatus // user define annotation
	ResponseEntity<Object> addCommentOnPost(@PathVariable long userId, @PathVariable long postId,
			@RequestBody PostComment comment) throws ResourceNotFoundException {
		log.info("***** Inside PostCommentController - addCommentOnPost");
		accessControlService.verifyUserAccess(userId);
		return commentService.storeComment(userId, postId, comment);
	}

	@Operation(summary = "Add Comment On Comment Api", description = "This API is used to add comment on parent comments Post")
	@PostMapping("/addCommentOnComment/{userId}/{postId}/{commentId}")
	@CheckUserStatus // user define annotation
	ResponseEntity<Object> addCommentOnComment(@PathVariable long userId, @PathVariable long postId,
			@PathVariable long commentId, @RequestBody PostComment comment) throws ResourceNotFoundException {
		log.info("***** Inside PostCommentController - addCommentOnPost");
		accessControlService.verifyUserAccess(userId);
		return commentService.storeCommentOnComment(userId, postId, commentId, comment);
	}

	@Operation(summary = "Delete Parent Comment Api", description = "This API is used delete parent comment")
	@DeleteMapping("/deleteParentComment/{userId}/{postCommentId}")
	@CheckUserStatus // user define annotation
	ResponseEntity<Object> deleteParentComment(@PathVariable long userId, @PathVariable long postCommentId)
			throws ResourceNotFoundException {
		accessControlService.verifyUserAccess(userId);
		return commentService.removeParentComment(userId, postCommentId);
	}

//	ResponseEntity<Object> deleteChildComment(@PathVariable)
	@Operation(summary = "Get Reacts and Comments Api", description = "This API is used get all comments and reacts")
	@GetMapping("/getCommentsAndReacts/{postId}")
	ResponseEntity<Object> getCommentsAndReacts(@PathVariable long postId)
			throws ResourceNotFoundException {
		return commentService.getAllCommentsAndReact(postId);
	}
}

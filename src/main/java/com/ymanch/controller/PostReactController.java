package com.ymanch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ymanch.entity.PostReact;
import com.ymanch.exception.ResourceNotFoundException;
import com.ymanch.helper.CheckUserStatus;
import com.ymanch.model.PostReactUpdate;
import com.ymanch.service.AccessControlService;
import com.ymanch.service.PostReactService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ymanch/users/postreact")
@Slf4j
public class PostReactController {
	@Autowired
	private PostReactService postReactService;

	@Autowired
	private AccessControlService accessControlService;

	@Operation(summary = "Add react On Post Api", description = "This API is used to add a react on Post")
	@PostMapping("/addReactOnPost/{userId}/{postId}")
	@CheckUserStatus // user define annotation
	ResponseEntity<Object> addReactOnPost(@PathVariable long userId, @PathVariable long postId,
			@Valid @RequestBody PostReact postReactDetails) throws ResourceNotFoundException {
		log.info("***** Inside - PostReactController - addReactOnPost *****");
		accessControlService.verifyUserAccess(userId);
		return postReactService.storeReactOnPost(userId, postId, postReactDetails);

	}

	@Operation(summary = "Update React On Post Api", description = "This API is used to upadte a react on existing Post")
	@PutMapping("/updateReactOnPost/{reactId}/{postId}")
	ResponseEntity<Object> updateReactOnPost(@PathVariable long reactId, @RequestBody PostReactUpdate updateReact)
			throws ResourceNotFoundException {
		log.info("***** Inside - PostReactController - updateReactOnPost");
		return postReactService.updateReactOnPost(reactId, updateReact);
	}

	@Operation(summary = "Delete react On Post Api", description = "This API allows users to remove their reaction from a specified post")
	@DeleteMapping("/deleteReactOnPost/{userId}/{postId}")
	@CheckUserStatus // user define annotation
	ResponseEntity<Object> deleteReactOnPost(@PathVariable long userId, @PathVariable long postId)
			throws ResourceNotFoundException {
		log.info("***** Inside - PostReactController - deleteReactOnPost *****");
		accessControlService.verifyUserAccess(userId);
		return postReactService.deleteReactOnPost(userId, postId);

	}
}

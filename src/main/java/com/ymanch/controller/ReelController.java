
package com.ymanch.controller;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ymanch.exception.ResourceNotFoundException;
import com.ymanch.helper.CheckUserStatus;
import com.ymanch.helper.Enums.PostOwnerType;
import com.ymanch.model.PostUploadModel;
import com.ymanch.service.AccessControlService;
import com.ymanch.service.PostService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ymanch/users/reels")
@Slf4j
public class ReelController {
	@Autowired
	private PostService postService;

	@Autowired
	private AccessControlService accessControlService;

//	@GetMapping("/getPosts/{userId}")
//	@CheckUserStatus // user define annotation
//	ResponseEntity<Object> getAllPost(@PathVariable long userId) {
//		log.info("***** Inside PostController - getAllPost");
//		accessControlService.verifyUserAccess(userId);
//		return postService.getAllPostDetails(userId);
//	}
//
	@Operation(summary = "Get reels Details API With Pagination", description = "This API is used to get reels details")
	@GetMapping("/getPosts/v1/{userId}")
	@CheckUserStatus // user define annotation
	ResponseEntity<Object> getAllReelsPage(@PathVariable long userId, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {
		log.info("***** Inside ReelController - getAllReelPage");
		 accessControlService.verifyUserAccess(userId);
		return postService.getAllReelsData(userId, page, size);
	}

	@Operation(summary = "Add Reels API", description = "This API is used to add a new Reel")
	@PostMapping("/addReel/{userId}")
	@CheckUserStatus // user define annotation
	ResponseEntity<Object> addReel(@PathVariable long userId, @ModelAttribute PostUploadModel reel)
			throws ResourceNotFoundException, InterruptedException, IOException {
		log.info("***** Inside - ReelController - addreel *****");
		accessControlService.verifyUserAccess(userId);
		return postService.storePost(userId, reel, "", 0, PostOwnerType.REEL);
	}

	@Operation(summary = "Update Reel Api", description = "This API is used to update an existing Reel of user")
	@PutMapping("/updateReel/{reelId}")
	ResponseEntity<Object> updateReel(@PathVariable long reelId, @RequestParam String reelName)
			throws ResourceNotFoundException {
		log.info("***** Inside - ReelController - updateReel *****");
		return postService.updatePost(reelId, reelName);
	}

	@Operation(summary = "Delete Reel Api", description = "This API is used to delete Reel")
	@DeleteMapping("/deletReel/{reelId}")
	ResponseEntity<Object> deletReel(@PathVariable long reelId) throws ResourceNotFoundException {
		log.info("***** Inside - AdminReelController - deleteReel *****");
		return postService.deletePost(reelId);
	}
	
	@Operation(summary = "Get All Reels", description = "Fetch all reels for the grid layout")
	@GetMapping("/getReelsByuser/{userId}")
	@CheckUserStatus
	ResponseEntity<Object> getAllReelsData(
	    @PathVariable long userId,
	    @RequestParam(defaultValue = "0") int page,
	    @RequestParam(defaultValue = "10") int size
	) {
	    log.info("***** Inside ReelsController - getAllReels *****");
	    accessControlService.verifyUserAccess(userId);
	    return postService.getAllReels(userId, page, size);
	}
	
	@Operation(summary = "Get Reel Feed", description = "Fetch reels for vertical scrolling feed")
	@GetMapping("/getReelFeed/{reelId}/{userId}")
	ResponseEntity<Object> getReelFeed(
	    @PathVariable long reelId,
	    @PathVariable long userId,
	    @RequestParam(defaultValue = "5") int size
	) {
	    log.info("***** Inside ReelsController - getReelFeed *****");
	    accessControlService.verifyUserAccess(userId);
	    return postService.getReelFeed(reelId, userId, size);
	}
	
	@Operation(summary = "Get All Reels", description = "Fetch all reels for the grid layout")
	@GetMapping("/getAllReels")
	@CheckUserStatus
	ResponseEntity<Object> getAllReel(
	    
	    @RequestParam(defaultValue = "0") int page,
	    @RequestParam(defaultValue = "10") int size
	) {
	    log.info("***** Inside ReelsController - getAllReels *****");
	    return postService.getAllReelsAll( page, size);
	}
	
	
	
}
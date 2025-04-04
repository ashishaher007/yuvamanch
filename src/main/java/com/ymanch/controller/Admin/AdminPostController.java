package com.ymanch.controller.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ymanch.entity.Posts;
import com.ymanch.exception.ResourceNotFoundException;
import com.ymanch.helper.CheckUserStatus;
import com.ymanch.helper.Enums.PostOwnerType;
import com.ymanch.model.AdminPostAdvertisementModel;
import com.ymanch.service.AdminService;
import com.ymanch.service.PostService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ymanch/admin/posts")
@Slf4j
public class AdminPostController {
	@Autowired
	private AdminService adminService;

	@Autowired
	private PostService postService;

	@Operation(summary = "Get All Users Posts Api", description = "This api is used to get all details of user's post")
	@GetMapping("/getAllPosts")
	private ResponseEntity<Object> getAllPosts(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		log.info("***** Inside - AdminPostController - getAllPosts *****");
		return adminService.getAllPostDetails(page, size);
	}

	@Operation(summary = "Get User Posts Api", description = "This api is used to retrieve all posts of a single user")
	@GetMapping("/getPosts/{userId}")
	private ResponseEntity<Object> getPosts(@PathVariable long userId, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {
		log.info("***** Inside - AdminPostController - getPosts *****");
		return adminService.fetchPosts(userId, page, size);
	}

	@Operation(summary = "Delete Post Api", description = "This API is used to delete post")
	@DeleteMapping("/deletpost/{postId}")
	ResponseEntity<Object> deletPost(@PathVariable long postId) throws ResourceNotFoundException {
		log.info("***** Inside - AdminPostController - deletePost *****");
		return postService.deletePost(postId);
	}

	@Operation(summary = "Add Advertisement(Event) API", description = "This API is used to add a new advertisement")
	@PostMapping("/event/{districtId}/{adminId}")
	ResponseEntity<Object> addEvent(@ModelAttribute AdminPostAdvertisementModel post, @PathVariable long districtId,
			@PathVariable long adminId) throws ResourceNotFoundException {
		log.info("***** Inside - PostController - addEvent *****");
		return adminService.storeAdminAdvertisementPost(adminId, post, districtId);
	}

	
}

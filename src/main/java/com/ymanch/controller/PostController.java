package com.ymanch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ymanch.entity.Posts;
import com.ymanch.exception.ResourceNotFoundException;
import com.ymanch.helper.CheckUserStatus;
import com.ymanch.helper.RequireUserAccess;
import com.ymanch.helper.Enums.PostOwnerType;
import com.ymanch.model.PostUploadModel;
import com.ymanch.service.AccessControlService;
import com.ymanch.service.PostService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ymanch/users/posts")
@Slf4j
public class PostController {

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

	@Operation(summary = "Get Post Details API With Pagination", description = "This API is used to get post details")
	@GetMapping("/getPosts/v1/{userId}")
	@CheckUserStatus // user define annotation
	ResponseEntity<Object> getAllPostPage(@PathVariable long userId, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {
		log.info("***** Inside PostController - getAllPostPage");
		accessControlService.verifyUserAccess(userId);
		return postService.getAllPostData(userId, page, size);
	}

//	@Operation(summary = "Add Post API", description = "This API is used to add a new Post")
//	@PostMapping("/addPost/{userId}")
//	@CheckUserStatus // user define annotation
//	ResponseEntity<Object> addPost(@PathVariable long userId, @ModelAttribute PostUploadModel post)
//			throws ResourceNotFoundException {
//		log.info("***** Inside - PostController - addPost *****");
//		accessControlService.verifyUserAccess(userId);
//		return postService.storePost(userId, post, "", 0, PostOwnerType.PUBLIC);
//	}

//	@Operation(summary = "Add Post API", description = "This API is used to add a new Post")
//	@PostMapping("/addPost/{userId}")
//	@CheckUserStatus // user-defined annotation
//	public ResponseEntity<Object> addPost(@PathVariable long userId, @ModelAttribute PostUploadModel post)
//	        throws ResourceNotFoundException {
//	    log.info("***** Inside - PostController - addPost *****");
//	    accessControlService.verifyUserAccess(userId);
//	    return postService.storePost(userId, post, "", 0, PostOwnerType.PUBLIC);
//	}

	@Operation(summary = "Add Post API with Hashtags in Post Name", description = "This API is used to add a new Post with hashtags in the post name")
	@PostMapping("/addPost/{userId}")
	@CheckUserStatus // user-defined annotation
	public ResponseEntity<Object> addPost(@PathVariable long userId, @ModelAttribute PostUploadModel post)
			throws ResourceNotFoundException {
		log.info("***** Inside - PostController - addPost *****");
		accessControlService.verifyUserAccess(userId);

		// Save the post along with hashtags found in the post name
		return postService.storePost(userId, post, "", 0, PostOwnerType.PUBLIC);
	}

	@Operation(summary = "Update Post Api", description = "This API is used to update an existing post of user")
	@PutMapping("/updatePost/{postId}")
	ResponseEntity<Object> updatePost(@PathVariable long postId, @ModelAttribute PostUploadModel post)
			throws ResourceNotFoundException {
		log.info("***** Inside - PostController - updatePost *****");
		return postService.updatePostDetails(postId, post);
	}

	@Operation(summary = "Delete Post Api", description = "This API is used to delete post")
	@DeleteMapping("/deletPost/{postId}")
	ResponseEntity<Object> deletPost(@PathVariable long postId) throws ResourceNotFoundException {
		log.info("***** Inside - AdminPostController - deletePost *****");
		return postService.deletePost(postId);
	}

	@Operation(summary = "Get Advertisement By District Api", description = "This API is used to get advertisement by districts")
	@GetMapping("/post/{districtId}")
	ResponseEntity<Object> getPostByDistrict(@PathVariable long districtId) {
		log.info("***** Inside PostController - getAllPost");
		return postService.getAllPostDetailsByDistricts(districtId);
	}

	
	@Operation(summary = "Get Advertisement[MINISTER] API", description = "This API is used to retrieve advertisement for ministers")
	@GetMapping("/getAds/{postCategory}")
	ResponseEntity<Object> getAdvertisements(@PathVariable String postCategory) {
		log.info("***** Inside SuperAdminController - getAdvertisements");
		return postService.retrieveAds(postCategory);
	}
	
}


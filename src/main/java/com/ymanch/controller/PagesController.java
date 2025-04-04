package com.ymanch.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.ymanch.exception.ResourceNotFoundException;
import com.ymanch.helper.CheckUserStatus;
import com.ymanch.helper.Enums.PostOwnerType;
import com.ymanch.model.PageGroupUpdateModel;
import com.ymanch.model.PagesModel;
import com.ymanch.model.PostUploadModel;
import com.ymanch.service.AccessControlService;
import com.ymanch.service.PagesService;
import com.ymanch.service.PostService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ymanch/users/pages")
@Slf4j
public class PagesController {
	private PagesService pagesService;
	private AccessControlService accessControlService;
	private PostService postService;

	public PagesController(PagesService pagesService, AccessControlService accessControlService,
			PostService postService) {
		super();
		this.pagesService = pagesService;
		this.accessControlService = accessControlService;
		this.postService = postService;
	}

	@Operation(summary = "Create Page API", description = "This API is uses to create a page by admin. Admin is nothing but the user")
	@PostMapping("/createPage")
	@CheckUserStatus
	ResponseEntity<Object> createPage(@Valid @RequestBody PagesModel pages, @RequestParam long adminUserId) {
		log.info("***** Inside - PagesController - createPage *****");
		return pagesService.createPage(adminUserId, pages);
	}

	@Operation(summary = "User Page Details API", description = "This API is used to retrieve all pages created by the admin (user) based on the user ID")
	@GetMapping("/getOwnPages/{userId}")
	@CheckUserStatus
	public ResponseEntity<Object> getOwnPages(@PathVariable long userId, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {
		log.info("***** Inside - PagesController - getPages *****");
		accessControlService.verifyUserAccess(userId);
		return pagesService.getPagesByUserId(userId, page, size);
	}

	@Operation(summary = "Following Page Details API", description = "This API returns a list of pages where the user has an active following status")
	@GetMapping("/getPages/{userId}")
	@CheckUserStatus
	public ResponseEntity<Object> getPages(@PathVariable long userId, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {
		log.info("***** Inside - PagesController - getPages *****");
		accessControlService.verifyUserAccess(userId);
		return pagesService.getPages(userId, page, size);
	}

	@Operation(summary = "All Page Details API", description = "This API is used to retrieve all pages")
	@GetMapping("/getAllPages/{userId}")
	@CheckUserStatus
	public ResponseEntity<Object> getAllPages(@PathVariable long userId, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {
		log.info("***** Inside - PagesController - getAllPages *****");
		accessControlService.verifyUserAccess(userId);
		return pagesService.getAllPages(userId, page, size);
	}

	@Operation(summary = "Follow Page API", description = "This API is uses to follow page")
	@PostMapping("/followPage/{userId}/{pageId}")
	@CheckUserStatus
	ResponseEntity<Object> followPage(@PathVariable long userId, @PathVariable long pageId) {
		log.info("***** Inside - PagesController - followPage *****");
		accessControlService.verifyUserAccess(userId);
		return pagesService.followpage(userId, pageId);
	}

	@Operation(summary = "Page Details API", description = "This API is use to get all details of a page by page Id")
	@GetMapping("/getPageDetails/{pageUUID}/{userId}")
	@CheckUserStatus
	public ResponseEntity<Object> getPageDetails(@PathVariable long userId, @PathVariable String pageUUID,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
		log.info("***** Inside - PagesController - getPageDetails *****");
		return pagesService.getPageDetails(userId, pageUUID, page, size);
	}

	@Operation(summary = "Add Post In Page API", description = "This API is used to add a new Post to a page by providing the user and group ID")
	@PostMapping("/addPost/{pageAdminUserId}/{pageId}")
	@CheckUserStatus // user define annotation
	ResponseEntity<Object> addPost(@PathVariable long pageAdminUserId, @PathVariable long pageId,
			@ModelAttribute PostUploadModel post) throws ResourceNotFoundException {
		log.info("***** Inside - PagesController - addPost *****");
		accessControlService.verifyUserAccess(pageAdminUserId);
		return postService.storePost(pageAdminUserId, post, "", pageId, PostOwnerType.PAGE);
	}

	@Operation(summary = "Update Page Details Api", description = "This Api is used to update page details by the admin user Id")
	@PutMapping("/updatePage/{pageAdminUserId}/{pageId}")
	@CheckUserStatus
	ResponseEntity<Object> updatePage(@PathVariable long pageAdminUserId, @PathVariable long pageId,
			@ModelAttribute PageGroupUpdateModel page) {
		log.info("***** Inside PagesController - updatePage *****");
		accessControlService.verifyUserAccess(pageAdminUserId);
		return pagesService.updatePage(pageAdminUserId, pageId, page);
	}

	@Operation(summary = "Exit Or Remove User Page API", description = "The API provides functionality for users to exit page on their own, or for admin to remove users from the page as needed")
	@DeleteMapping("/exitPage/{pageAdminUserId}/{pageId}/{userId}")
	@CheckUserStatus
	public ResponseEntity<Object> exitPage(@PathVariable long pageAdminUserId, @PathVariable long pageId,
			@PathVariable long userId) {
		log.info("***** Inside - PagesController - exitPage *****");
		return pagesService.exitPage(pageAdminUserId, pageId, userId);
	}

	@Operation(summary = "Delete Page API", description = "This API is used to delete page by page admin and group Id")
	@DeleteMapping("/deletePage/{pageAdminUserId}/{pageId}")
	@CheckUserStatus
	public ResponseEntity<Object> deletePage(@PathVariable long pageAdminUserId, @PathVariable long pageId) {
		log.info("***** Inside - PagesController - deletePage *****");
		accessControlService.verifyUserAccess(pageAdminUserId);
		return pagesService.deletePageById(pageAdminUserId, pageId);
	}

}

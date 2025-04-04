package com.ymanch.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ymanch.entity.Story;
import com.ymanch.helper.CheckUserStatus;
import com.ymanch.model.PostUploadModel;
import com.ymanch.service.AccessControlService;
import com.ymanch.service.StoryService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ymanch/users/story")
@Slf4j
public class StoryController {

	private StoryService storyService;
	private AccessControlService accessControlService;

	public StoryController(StoryService storyService, AccessControlService accessControlService) {
		this.storyService = storyService;
		this.accessControlService = accessControlService;
	}

	@Operation(summary = "Create Story Api", description = "This API is used to create story by user Id")
	@PostMapping("/createStory/{userId}")
	@CheckUserStatus
	public ResponseEntity<Object> createStory(@ModelAttribute PostUploadModel story, @PathVariable long userId) {
		log.info("***** Inside - StoryController  - createStory *****");
		accessControlService.verifyUserAccess(userId);
		return storyService.addStory(userId, story);

	}

	@Operation(summary = "Get All Stories Api", description = "This API is used to get all stories of users added before 24 hours")
	@GetMapping("/getStories")
	public ResponseEntity<Object> getStories() {
		log.info("***** Inside - StoryController  - getStories *****");
		return storyService.getAllStories();

	}

	@Operation(summary = "Delete Story Api", description = "This Api is used to delete a story of a user")
	@DeleteMapping("/deleteStory/{storyId}")
	ResponseEntity<Object> deleteStory(@PathVariable long storyId) {
		log.info("***** Inside StoryController - deleteStory *****");
		return storyService.removeStory(storyId);

	}

	@Operation(summary = "Add Story To Viewer Api", description = "This API is used to store the details of users who have viewed the story")
	@PostMapping("/addViewedStory/{userId}/{storyId}")
	@CheckUserStatus
	public ResponseEntity<Object> addViewedStory(@PathVariable long userId, @PathVariable long storyId) {
		log.info("***** Inside - StoryController  - addViewedStory *****");
		accessControlService.verifyUserAccess(userId);
		return storyService.addViewStoryData(userId, storyId);
	}

	@Operation(summary = "Most Viewed Stories Api", description = "This Api is used to check the details who have viewed the user stories")
	@GetMapping("/getViewedStories/{userId}")
	@CheckUserStatus
	ResponseEntity<Object> getViewedStories(@PathVariable long userId) {
		log.info("***** Inside StoryController - getViewedStories *****");
		accessControlService.verifyUserAccess(userId);
		return storyService.getAllViewedStoriesDetails(userId);

	}

}

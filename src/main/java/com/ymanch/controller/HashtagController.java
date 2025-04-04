package com.ymanch.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ymanch.entity.Hashtag;
import com.ymanch.helper.CommonFunctions;
import com.ymanch.service.HashtagService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/ymanch/users/hashtags")
public class HashtagController {

	private HashtagService hashtagService;
	private CommonFunctions commonFunctions;

	public HashtagController(HashtagService hashtagService,CommonFunctions commonFunctions) {
		this.hashtagService = hashtagService;
		this.commonFunctions=commonFunctions;
	}
	
	@Operation(summary = "Create a new hashtag", description = "This endpoint allows the creation of a new hashtag in the system.")
	@PostMapping("/createHashtag")
	public ResponseEntity<Object> createHashtag(@RequestBody Hashtag hashtag) {
	    return hashtagService.createHashtag(hashtag);
	}

	@Operation(summary = "Get all hashtags", description = "This endpoint retrieves a list of all hashtags in the system.")
	@GetMapping("/getHashtags")
	public ResponseEntity<Object> getAllHashtags() {
	    return hashtagService.getAllHashtags();
	}

	@Operation(summary = "Get a hashtag by name", description = "This endpoint retrieves a specific hashtag by its name.")
	@GetMapping("/getHashtags/{name}")
	public ResponseEntity<Object> getHashtagByName(@PathVariable String name) {
	    return hashtagService.getHashtagByName(name);
	}

	@Operation(summary = "Delete a hashtag by ID", description = "This endpoint allows you to delete a hashtag by its ID.")
	@DeleteMapping("/deleteHashtag/{id}")
	public ResponseEntity<Object> deleteHashtag(@PathVariable Long id) {
	    return hashtagService.deleteHashtag(id);
	}
	
	@Operation(summary = "Get a hashtag by name with pagination", description = "This endpoint retrieves a specific hashtag by its id with pagination.")
	@GetMapping("/getHashtags/hashtag/{hashtagName}")
	public ResponseEntity<Object> getHashtagByName(HttpServletRequest request,@PathVariable String hashtagName,@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {
		Long userId = commonFunctions.getUserIdFromRequest(request);
	    return hashtagService.getHashtagById(page,size,hashtagName,userId);
	}
}

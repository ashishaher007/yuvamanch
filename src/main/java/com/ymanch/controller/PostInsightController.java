package com.ymanch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ymanch.service.PostInsightService;

@RestController
@RequestMapping("/ymanch/users/postsInsight")
public class PostInsightController {

	@Autowired
	private PostInsightService postInsightService;

	    @PostMapping("/insightCount")
	    public ResponseEntity<Object> insightCount(@RequestParam long userId, @RequestParam long postId, @RequestParam (required = false) Long reachTimeSpent,
	    		@RequestParam (required = false) Long viewTimeSpent) {
	        return postInsightService.insightCount(userId, postId, reachTimeSpent,viewTimeSpent);
	    }

	    @GetMapping("/post/{postId}/counts")
	    public ResponseEntity<Object> getPostCounts(@PathVariable long postId) {
	        return postInsightService.getPostCounts(postId);
	    }
}

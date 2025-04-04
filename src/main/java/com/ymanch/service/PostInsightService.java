package com.ymanch.service;

import org.springframework.http.ResponseEntity;

public interface PostInsightService {


	ResponseEntity<Object> insightCount(long userId, long postId, Long reachTimeSpent, Long viewTimeSpent);
	ResponseEntity<Object> getPostCounts(long postId);
}

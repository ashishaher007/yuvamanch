package com.ymanch.service;

import org.springframework.http.ResponseEntity;

public interface HomeService {

//	ResponseEntity<Object> getAllHomePageData();

	ResponseEntity<Object> getLastActivityData(long userId);

	ResponseEntity<Object> getAllHomePageDetails(Long cursor, int size, long userId);

	ResponseEntity<Object> fetchBoostPost(int page, int size, Long userId);

}

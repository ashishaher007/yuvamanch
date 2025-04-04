package com.ymanch.service;

import org.springframework.http.ResponseEntity;

import com.ymanch.entity.Hashtag;

public interface HashtagService {

	ResponseEntity<Object> createHashtag(Hashtag hashtag);

	ResponseEntity<Object> getAllHashtags();

	ResponseEntity<Object> getHashtagByName(String name);

	ResponseEntity<Object> deleteHashtag(Long id);
	
	ResponseEntity<Object> getHashtagById(int page, int size, String hashtagName,Long userId);

}

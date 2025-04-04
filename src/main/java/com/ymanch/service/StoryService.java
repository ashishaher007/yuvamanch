package com.ymanch.service;

import org.springframework.http.ResponseEntity;

import com.ymanch.entity.Story;
import com.ymanch.model.PostUploadModel;

public interface StoryService {

	ResponseEntity<Object> addStory(long userId, PostUploadModel story);

	ResponseEntity<Object> getAllStories();

	ResponseEntity<Object> removeStory(long storyId);

	ResponseEntity<Object> addViewStoryData(long userId, long storyId);

	ResponseEntity<Object> getAllViewedStoriesDetails(long userId);

}

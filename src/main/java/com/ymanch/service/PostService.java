package com.ymanch.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.ymanch.exception.ResourceNotFoundException;
import com.ymanch.helper.Enums.PostOwnerType;
import com.ymanch.model.PostUploadModel;

import jakarta.validation.Valid;

public interface PostService {

	ResponseEntity<Object> storePost(long userId, @Valid PostUploadModel post, String district, long groupId,
			PostOwnerType ownerType) throws ResourceNotFoundException;

	ResponseEntity<Object> updatePost(long postId, String postName) throws ResourceNotFoundException;

	ResponseEntity<Object> deletePost(long postId) throws ResourceNotFoundException;

//	ResponseEntity<Object> getAllPostDetails(long userId);

	ResponseEntity<Object> getAllPostData(long userId, int page, int size);

	ResponseEntity<Object> getAllPostDetailsByDistricts(long districtId);

	ResponseEntity<Object> savePost(long userId, long postId);

	ResponseEntity<Object> getSavedPost(long userId, int size, int page);

	ResponseEntity<Object> deletSavedPost(long userId, long postId);

	ResponseEntity<Object> getAllReelsData(long userId, int page, int size);

	ResponseEntity<Object> retrieveAds(String postCategory);

	ResponseEntity<Object> updatePostDetails(long postId, PostUploadModel post);

	ResponseEntity<Object> getAnnouncementAtUserSide(String postCategory, long districtId);

	ResponseEntity<Object> getAllReels(long userId, int page, int size);

	ResponseEntity<Object> getReelFeed(long reelId, long userId, int size);

	ResponseEntity<Object> getAllReelsAll(int page, int size);
	
	

}

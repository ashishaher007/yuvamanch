package com.ymanch.serviceimpl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ymanch.entity.Notification;
import com.ymanch.entity.PostReact;
import com.ymanch.entity.Posts;
import com.ymanch.entity.User;
import com.ymanch.exception.ResourceNotFoundException;
import com.ymanch.helper.CommonMessages;
import com.ymanch.helper.Enums.NotificationStatus;
import com.ymanch.helper.Enums.PostOwnerType;
import com.ymanch.model.PostReactUpdate;
import com.ymanch.repository.NotificationRepository;
import com.ymanch.repository.PostReactRepository;
import com.ymanch.repository.PostRepository;
import com.ymanch.repository.UserRepository;
import com.ymanch.service.PostReactService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PostReactServiceImpl implements PostReactService {

	Map<Object, Object> response;

	@Autowired
	private PostReactRepository postReactRepo;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private PostRepository postRepo;

	@Autowired
	private CommonMessages MSG;

	@Autowired
	private NotificationRepository notificationRepo;

	@Transactional
	@Override
	public ResponseEntity<Object> storeReactOnPost(long userId, long postId, @Valid PostReact postReactDetails)
			throws ResourceNotFoundException {
		log.info("***** Inside - PostReactServiceImpl - storeReactOnPost *****");
		response = new HashMap<>();
		User userData = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User with the Id " + userId + " not found"));
		List<PostOwnerType> allowedTypes = Arrays.asList(PostOwnerType.PUBLIC, PostOwnerType.GROUP, PostOwnerType.PAGE,PostOwnerType.REEL);
		Posts postData = postRepo.findByPostIdAndPostOwnerTypeIn(postId, allowedTypes)
				.orElseThrow(() -> new ResourceNotFoundException("Post with the Id " + postId + " not found"));
		// Check if the user has already reacted to the post
		Optional<PostReact> postReactData = postReactRepo.findByUserAndPosts(userData, postData);
		if (postReactData.isPresent()) {
			response.put("status", MSG.FAILED);
			response.put("message", MSG.POST_REACT_MORE_THAN_ONE_MSG);
			return new ResponseEntity<>(response, HttpStatus.CONFLICT);
		} else {
			postReactDetails.setUser(userData);
			postReactDetails.setPosts(postData);
			postReactRepo.save(postReactDetails);

			// Prepare notification only if the commenter's user ID does not match the post
			// owner's user ID
			if (userId != postData.getUser().getUserId()) {
				// add in notification
				Notification notify = new Notification();
				notify.setNotificationMessage(userData.getUserFirstName() + " " + userData.getUserLastName() + ""
						+ MSG.NOTIFICATION_ADD_REACT_POST);
				notify.setNotificationStatus(NotificationStatus.UNREAD);
				notify.setSender(userData);
				notify.setReceiver(postData.getUser());
				notificationRepo.save(notify);
			}

			response.put("status", CommonMessages.SUCCESS);
			response.put("message", MSG.POST_REACT_ADD_SUCCESSFUL);
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		}
	}

	@Transactional
	@Override
	public ResponseEntity<Object> updateReactOnPost(long reactId, PostReactUpdate updateReact)
			throws ResourceNotFoundException {
		log.info("***** Inside - PostReactServiceImpl - updateReactOnPost *****");
		response = new HashMap<>();
		PostReact postReactData = postReactRepo.findById(reactId)
				.orElseThrow(() -> new ResourceNotFoundException("Post react  Id " + reactId + " not found"));
		postReactData.setPostReactName(updateReact.getPostReactName());
		postReactData.setPostReactImageUrl(updateReact.getPostReactImageUrl());
		postReactRepo.save(postReactData);
		response.put("status", CommonMessages.SUCCESS);
		response.put("message", MSG.POST_REACT_UPDATE_SUCCESSFUL);
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	@Transactional
	@Override
	public ResponseEntity<Object> deleteReactOnPost(long userId, long postId) {
		log.info("***** Inside - PostReactServiceImpl - deleteReactOnPost *****");
		response = new HashMap<>();
		User userData = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User with the Id " + userId + " not found"));
		Posts postData = postRepo.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post with the Id" + postId + " not found"));

		// Check if the user has already reacted to the post
		Optional<PostReact> postReactData = postReactRepo.findByUserAndPosts(userData, postData);
		if (postReactData.isPresent()) {
			postReactRepo.delete(postReactData.get());
			response.put("status", CommonMessages.SUCCESS);
			response.put("message", MSG.POST_REACT_REMOVED_SUCCESSFULL);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			response.put("status", MSG.FAILED);
			response.put("message", MSG.POST_REACT_NOT_FOUND);
			return new ResponseEntity<>(response, HttpStatus.CONFLICT);
		}

	}

}

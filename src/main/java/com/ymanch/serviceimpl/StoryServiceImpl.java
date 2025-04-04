package com.ymanch.serviceimpl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ymanch.entity.Story;
import com.ymanch.entity.User;
import com.ymanch.entity.ViewStory;
import com.ymanch.exception.ResourceNotFoundException;
import com.ymanch.helper.CommonFunctions;
import com.ymanch.helper.CommonMessages;
import com.ymanch.helper.DateTimeUtil;
import com.ymanch.model.PostUploadModel;
import com.ymanch.model.StoryModel;
import com.ymanch.model.StoryUserModel;
import com.ymanch.repository.StoryRepository;
import com.ymanch.repository.UserRepository;
import com.ymanch.repository.ViewStoryRepository;
import com.ymanch.service.StoryService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StoryServiceImpl implements StoryService {

	private Map<Object, Object> response;
	private StoryRepository storyRepo;
	private UserRepository userRepo;
	private CommonMessages MSG;
	private ViewStoryRepository viewStoryRepo;
	private CommonFunctions commonFunctions;

	public StoryServiceImpl(StoryRepository storyRepo, UserRepository userRepo, CommonMessages mSG,
			ViewStoryRepository viewStoryRepo, CommonFunctions commonFunctions) {

		this.storyRepo = storyRepo;
		this.userRepo = userRepo;
		this.viewStoryRepo = viewStoryRepo;
		MSG = mSG;
		this.commonFunctions = commonFunctions;
	}

	@Override
	public ResponseEntity<Object> addStory(long userId, PostUploadModel story) {
		log.info("Inside - StoryServiceImpl - addStory");
		response = new HashMap<>();
		User userdata = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User with the Id " + userId + " not found"));
		Story data = new Story();
		data.setDescription(story.getPostName());
		data.setVideoThumbnailUrl(story.getVideoThumbnailUrl());
		data.setUser(userdata);
		data.setIsActive(true);
		data.setStoryType(story.getPostType());
		try {
			String imageUrl = commonFunctions.saveImageToServer(story.getPostImage());
			data.setStoryUrl(imageUrl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		storyRepo.save(data);
		response.put("status", MSG.SUCCESS);
		response.put("message", MSG.S_STORY_ADD_SUCCESSFULL);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Override
	public ResponseEntity<Object> getAllStories() {
		log.info("Inside - StoryServiceImpl - getAllStories");
		response = new HashMap<>();
		LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
		List<Story> stories = storyRepo.findByStoryCreatedAtAfterOrderByStoryCreatedAtDesc(cutoff);
		if (stories.isEmpty() || stories == null) {
			response.put("storiesDetails", stories);
			return ResponseEntity.status(HttpStatus.OK).body(response);
		}
		// group stories by user
		Map<User, List<Story>> storiesByUser = stories.stream().collect(Collectors.groupingBy(Story::getUser));
		if (storiesByUser == null || storiesByUser.isEmpty()) {
			response.put("storiesDetails", storiesByUser);
			return ResponseEntity.status(HttpStatus.OK).body(response);
		}
//		Add index No
		AtomicInteger indexCounter = new AtomicInteger(1);
		List<StoryUserModel> storyDtos = storiesByUser.entrySet().stream().map(entry -> {
			User user = entry.getKey();
			List<Story> userStories = entry.getValue();

			// Create StoryUserModel
			StoryUserModel dto = new StoryUserModel();
			dto.setUserId(user.getUserId());
			dto.setUserFirstName(user.getUserFirstName());
			dto.setUserLastName(user.getUserLastName());
			dto.setUserProfileImagePath(user.getUserProfileImagePath());
			dto.setStoriesCount(userStories.size());
			// Set index number
			dto.setIndexNumber(indexCounter.getAndIncrement());
			// Create list of StoryModel
			List<StoryModel> storyModels = userStories.stream().map(story -> {
				String timeAgo = DateTimeUtil.convertToTimeAgo(story.getStoryCreatedAt());
				StoryModel storyModel = new StoryModel();
				storyModel.setStoryId(story.getStoryId());
				storyModel.setStoryUrl(story.getStoryUrl());
				storyModel.setStoryCreatedAt(timeAgo);
				storyModel.setDescription(story.getDescription());
				storyModel.setStoryType(story.getStoryType());
				storyModel.setVideoThumbnailUrl(story.getVideoThumbnailUrl());
				return storyModel;
			}).collect(Collectors.toList());

			dto.setStoryDetails(storyModels);
			return dto;
		}).collect(Collectors.toList());

		response.put("storiesDetails", storyDtos);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Override
	public ResponseEntity<Object> removeStory(long storyId) {
		log.info("Inside - StoryServiceImpl - removeStory");
		response = new HashMap<>();
		Optional<Story> data = storyRepo.findById(storyId);
		if (data.isPresent()) {
			boolean success = false;
			success = commonFunctions.deleteObject(data.get().getStoryUrl());
			if (success) {
				storyRepo.delete(data.get());
				response.put("status", MSG.SUCCESS);
				response.put("message", MSG.S_STORY_DELETE_SUCCESSFUL);
				return ResponseEntity.status(HttpStatus.OK).body(response);
			} else {
				response.put("status", MSG.FAILED);
				response.put("message", MSG.S_STORY_DELETE_FAILED);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}
		} else {
			response.put("status", MSG.FAILED);
			response.put("message", MSG.S_STORY_NOT_FOUND);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
	}

	@Override
	public ResponseEntity<Object> addViewStoryData(long userId, long storyId) {
		log.info("Inside - StoryServiceImpl - addViewStoryData");
		response = new HashMap<>();
		int count = viewStoryRepo.countUserStoryViews(userId, storyId);
		if (count > 0) {
			response.put(MSG.STATUS, MSG.FAILED);
			response.put(MSG.MESSAGE, MSG.S_STORY_VIEWED_ALREADY);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		User userdata = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User with the Id " + userId + " not found"));
		Story storyData = storyRepo.findById(storyId)
				.orElseThrow(() -> new ResourceNotFoundException("Story with the Id " + storyId + " not found"));

		ViewStory ob = new ViewStory();
		ob.setUser(userdata);
		ob.setStory(storyData);
		viewStoryRepo.save(ob);
		response.put(MSG.STATUS, MSG.SUCCESS);
		response.put(MSG.MESSAGE, MSG.S_STORY_VIEWED_SUCCESSFUL);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Override
	public ResponseEntity<Object> getAllViewedStoriesDetails(long userId) {
		log.info("Inside - StoryServiceImpl - getAllViewedStoriesDetails");
		Map<String, Object> response = new HashMap<>();
		LocalDateTime cutoff = LocalDateTime.now().minusHours(24).withNano(0);
		// Fetch stories by userId

		List<Story> stories = storyRepo.findByUserUserIdAndStoryCreatedAtAfter(userId, cutoff);
		// Create the response
		List<Map<String, Object>> data = stories.stream().map(story -> {
			// Fetch users who viewed the story
			List<Map<String, Object>> usersWhoViewed = viewStoryRepo.findByStoryAndViewCreatedAtAfter(story, cutoff)
					.stream().map(viewStory -> {
						String timeAgo = DateTimeUtil.convertToTimeAgo(viewStory.getViewCreatedAt());
						Map<String, Object> userMap = new HashMap<>();
						userMap.put("userId", viewStory.getUser().getUserId());
						userMap.put("userFirstName", viewStory.getUser().getUserFirstName());
						userMap.put("userLastName", viewStory.getUser().getUserLastName());
						userMap.put("userLastName", viewStory.getUser().getUserLastName());
						userMap.put("userProfileImagePath", viewStory.getUser().getUserProfileImagePath());
						userMap.put("viewedTime", timeAgo);
						return userMap;
					}).collect(Collectors.toList());
			String timeAgo = DateTimeUtil.convertToTimeAgo(story.getStoryCreatedAt());
			// Map story details with users who viewed
			Map<String, Object> storyMap = new HashMap<>();
			storyMap.put("storyId", story.getStoryId());
			storyMap.put("storyUrl", story.getStoryUrl());
			storyMap.put("storyType", story.getStoryType());
			storyMap.put("userFirstName", story.getUser().getUserFirstName());
			storyMap.put("userLastName", story.getUser().getUserLastName());
			storyMap.put("usersWhoViewed", usersWhoViewed);
			storyMap.put("userFirstName", story.getUser().getUserFirstName());
			storyMap.put("userLastName", story.getUser().getUserLastName());
			storyMap.put("storyType", story.getStoryType());
			storyMap.put("videoThumbnailUrl", story.getVideoThumbnailUrl());
			storyMap.put("storyCreatedAt", timeAgo);

			return storyMap;
		}).collect(Collectors.toList());

		response.put("viewedStories", data);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

}

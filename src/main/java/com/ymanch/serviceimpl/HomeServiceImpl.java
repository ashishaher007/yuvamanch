package com.ymanch.serviceimpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ymanch.dao.HomeServiceDao;
import com.ymanch.entity.Boost;
import com.ymanch.helper.CommonMessages;
import com.ymanch.helper.DateTimeUtil;
import com.ymanch.model.PostIndexPage;
import com.ymanch.repository.BoostRepository;
import com.ymanch.repository.PostRepository;
import com.ymanch.service.BoostService;
import com.ymanch.service.HomeService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class HomeServiceImpl implements HomeService {

	private HomeServiceDao homeServiceDao;

	private CommonMessages MSG;

	private PostRepository postRepo;

	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	BoostRepository boostRepository;

	@Autowired
	private BoostService boostservice;

	public HomeServiceImpl(HomeServiceDao homeServiceDao, CommonMessages mSG, PostRepository postRepo,
			RedisTemplate<String, Object> redisTemplate) {
		this.homeServiceDao = homeServiceDao;
		MSG = mSG;
		this.postRepo = postRepo;
		this.redisTemplate = redisTemplate;
	}

//	@Override
//	public ResponseEntity<Object> getAllHomePageData() {
//		log.info("***** Inside - HomeServiceImpl - getAllHomePageData *****");
//		Map<Object, Object> response = new HashMap<>();
//		List<PostIndex> indexData = homeServiceDao.getPostIndexData();
//		if (indexData == null || indexData.isEmpty()) {
//			response.put("status", MSG.FAILED);
//			response.put("message", MSG.USER_POST_TIMELINE);
//			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
//		} else {
//			for (int i = 0; i < indexData.size(); i++) {
//				// set all comments on particular post
//				indexData.get(i)
//						.setCommentsAndReacts(homeServiceDao.getPostIndexDataComments(indexData.get(i).getPostId()));
//			}
//			response.put("homePostData", indexData);
//			return new ResponseEntity<>(response, HttpStatus.OK);
//		}
//
//	}

	@Transactional
	@Override
	public ResponseEntity<Object> getLastActivityData(long userId) {
		log.info("***** Inside - HomeServiceImpl - getLastActivityData *****");
		Map<Object, Object> response = new HashMap<>();
		List<Object[]> activityData = homeServiceDao.retrievelastActivityData(userId);
		if (activityData == null || activityData.isEmpty()) {
			response.put("status", CommonMessages.FAILED);
			response.put("message", MSG.LACTIVITY_NOT_FOUND);
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		} else {
			List<Map<String, Object>> activityList = activityData.stream().map(row -> {
				Map<String, Object> activity = new HashMap<>();
				activity.put("userProfileImagePath", row[0]);
				activity.put("userFirstName", row[1]);
				activity.put("userLastName", row[2]);
				activity.put("postName", row[3]);
				activity.put("createdAt", row[4]);
				activity.put("activityType", row[5]);
				activity.put("userId", row[6]); // Add userId to the map
				return activity;
			}).collect(Collectors.toList()); // Collect data while processing
			response.put("activityListData", activityList);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}

	}

//	@Transactional
//	@Override
//	public ResponseEntity<Object> getAllHomePageDetails(long cursor, int size, long userId) {
//		log.info("***** Inside - HomeServiceImpl - getAllHomePageDetails *****");
//		Map<Object, Object> response = new HashMap<>();
////		if (cursor == 0) {
//////			String redisKey = "homePage:" + userId + ":cursor:" + cursor;
////			String redisKey = "homePage:" + userId + ":*";
//////			redisTemplate.delete(redisKey); // Remove the cache if cursor is 0
////			redisTemplate.delete(redisTemplate.keys(redisKey));
////		}
//		if (cursor <= 0) {
//			cursor = Long.MAX_VALUE; // For first request, start from the latest post
//		}
//
////		// Generate Redis key based only on userId and cursor
////		String redisKey = "homePage:" + userId + ":cursor:" + cursor;
////		// Check if the data is already in cache
////		Object cachedData = redisTemplate.opsForValue().get(redisKey);
////		if (cachedData != null) {
////			return new ResponseEntity<>(cachedData, HttpStatus.OK); // Return cached data if available
////		}
//
//		Pageable pageable = PageRequest.of(0, size + 1);
//
//		List<PostIndexPage> indexData = postRepo.findPostsByCursorAndBoostStatus(cursor, pageable, userId);
//
//		if (indexData.isEmpty()) {
//			response.put(CommonMessages.STATUS, "Failed");
//			response.put(CommonMessages.MESSAGE, "You're all caught up! There are no more posts to show at the moment");
//			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
//		}
//
//		// extra items
//
//		boolean hasNextPage = indexData.size() > size;
//
//		if (hasNextPage) {
//			indexData = indexData.subList(0, size); // Only return the requested number of posts
//		}
//		// Get the postId of the last item in the current page to set as nextCursor
//		long nextCursor = indexData.get(indexData.size() - 1).getPostId();
//
//		// Convert time format for each post in the page
//		indexData = indexData.stream().map(postIndex -> {
//			String timeAgo = DateTimeUtil.convertToTimeAgo(postIndex.getPostCreatedAt());
//			postIndex.setPostUploadedAt(timeAgo);
//			return postIndex;
//		}).collect(Collectors.toList());
//
//		response.put("homePostData", indexData);
//		response.put(MSG.TOTAL_ELEMENTS, indexData.size());
//		response.put("hasNextPage", hasNextPage); // Checks if there's another page
//		response.put("nextCursor", nextCursor); // Sets the next cursor to continue from here
//
//		// Cache the data in Redis
////			redisTemplate.opsForValue().set(redisKey, response, 10, TimeUnit.MINUTES); // Cache for 10 minute
//
//		return new ResponseEntity<>(response, HttpStatus.OK);
//
//	}

//	@Transactional
//	public List<PostIndexPage> getPostsForUser(long cursor, Pageable pageable, Long userId) {
//		System.out.println("cursor is" + cursor);
//		// Fetch all posts from different sources
//		List<PostIndexPage> boostedPosts = postRepo.findByBoostedPost(cursor, pageable, userId);
//		List<PostIndexPage> nonBoostedPosts = postRepo.findByDistrictFirst(cursor, pageable, userId);
//		List<PostIndexPage> allPosts = postRepo.findByIdLessThan(cursor, pageable, userId);
//		System.out.println(boostedPosts);
//		// Loop through boosted posts and update their boost status
//		for (PostIndexPage postIndex : boostedPosts) {
//			System.out.println(postIndex.getPostId() + "**********************************************");
//			Optional<Boost> boost = boostRepository.findByPostId(postIndex.getPostId());
//			boost.ifPresent(b -> boostservice.checkAndUpdateBoostStatus(b.getBoostId()));
//		}
//
//		// Use a LinkedHashMap to maintain insertion order and ensure unique postIds
//		Map<Long, PostIndexPage> postMap = new LinkedHashMap<>();
//
//		// Add all posts while ensuring no duplicates based on postId
//		addPostsToMap(boostedPosts, postMap);
//		addPostsToMap(nonBoostedPosts, postMap);
//		addPostsToMap(allPosts, postMap);
//
//		// Return the combined list
//		return new ArrayList<>(postMap.values());
//	}
//
//	// Helper method to add posts to Map while ensuring no duplicate postId
//	private void addPostsToMap(List<PostIndexPage> posts, Map<Long, PostIndexPage> postMap) {
//		for (PostIndexPage post : posts) {
//			postMap.putIfAbsent(post.getPostId(), post); // This ensures no duplicates
//		}
//	}

	@Transactional
	@Override
	public ResponseEntity<Object> getAllHomePageDetails(Long cursor, int size, long userId) {
		log.info("***** Inside - HomeServiceImpl - getAllHomePageDetails *****");
		Map<Object, Object> response = new HashMap<>();
		if (cursor == 0) {
//			String redisKey = "homePage:" + userId + ":cursor:" + cursor;
			String redisKey = "homePage:" + userId + ":*";
//			redisTemplate.delete(redisKey); // Remove the cache if cursor is 0
			redisTemplate.delete(redisTemplate.keys(redisKey));
		}
		if (cursor <= 0) {
			cursor = Long.MAX_VALUE; // For first request, start from the latest post
		}
//		 Generate Redis key based only on userId and cursor
		String redisKey = "homePage:" + userId + ":cursor:" + cursor;
		// Check if the data is already in cache
		Object cachedData = redisTemplate.opsForValue().get(redisKey);
		if (cachedData != null) {
			return new ResponseEntity<>(cachedData, HttpStatus.OK); // Return cached data if available
		}
		Pageable pageable = PageRequest.of(0, size + 1);
		List<PostIndexPage> indexData = postRepo.findByIdLessThan(cursor, pageable, userId);

		if (indexData.isEmpty()) {
			response.put(CommonMessages.STATUS, "Failed");
			response.put(CommonMessages.MESSAGE, "You're all caught up! There are no more posts to show at the moment");
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		} else {
			// extra items
			boolean hasNextPage = indexData.size() > size;
			if (hasNextPage) {
				indexData = indexData.subList(0, size); // Only return the requested number of posts
			}
			// Get the postId of the last item in the current page to set as nextCursor
			long nextCursor = indexData.get(indexData.size() - 1).getPostId();
			// Convert time format for each post in the page
			indexData = indexData.stream().map(postIndex -> {
				String timeAgo = DateTimeUtil.convertToTimeAgo(postIndex.getPostCreatedAt());
				postIndex.setPostUploadedAt(timeAgo);
				return postIndex;
			}).collect(Collectors.toList());
			response.put("homePostData", indexData);
			response.put(MSG.TOTAL_ELEMENTS, indexData.size());
			response.put("hasNextPage", hasNextPage); // Checks if there's another page
			response.put("nextCursor", nextCursor); // Sets the next cursor to continue from here
			// Cache the data in Redis
			redisTemplate.opsForValue().set(redisKey, response, 10, TimeUnit.MINUTES); // Cache for 1 minute
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@Transactional
	@Override
	public ResponseEntity<Object> fetchBoostPost(int page, int size, Long userId) {
		log.info("***** Inside - HomeServiceImpl - getAllHomePageDetails *****");
		Map<Object, Object> response = new HashMap<>();
		System.out.println(userId);

		if (page == 0) {
			log.info("Clearing cache for boosted posts for user: {}", userId);
			String redisKey = "boostedPosts:" + userId + ":*";
			redisTemplate.delete(redisKey); // Remove the cache for this key if page is 0
		}

		// Generate Redis key based on page and userId
		String redisKey = "boostedPosts:" + userId + ":page:" + page + ":size:" + size;

		// Check if the data is already in cache
		Object cachedData = redisTemplate.opsForValue().get(redisKey);
		if (cachedData != null) {
			log.info("Cache hit for boosted posts for user: {}", userId);
			return new ResponseEntity<>(cachedData, HttpStatus.OK); // Return cached data if available
		}

		// Fetch boosted posts

		Pageable boostPageable = PageRequest.of(page, size);
		Page<PostIndexPage> boostedPosts = postRepo.findBoostPosts(boostPageable, userId);

		if (boostedPosts.isEmpty()) {
			response.put(CommonMessages.STATUS, CommonMessages.FAILED);
			response.put(CommonMessages.MESSAGE, CommonMessages.BOOST_POST_NOT_FOUND);
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}

		// Call checkAndUpdateBoostStatus for each boosted post
		boostedPosts.getContent().forEach(post -> {
			Long postId = post.getPostId(); // Assuming each post has a boostId field
			Optional<Boost> boostId = boostRepository.findByPostId(postId);
			if (boostId != null) {
				boostservice.checkAndUpdateBoostStatus(boostId.get().getBoostId()); // Call the method for each post
			}
		});

		Page<PostIndexPage> boostedPostss = postRepo.findBoostPosts(boostPageable, userId);
		// Prepare the response
		response.put("homePostData", boostedPostss.getContent());
		response.put(MSG.TOTAL_ELEMENTS, boostedPostss.getTotalElements());
		response.put("hasNextPage", boostedPostss.hasNext()); // Check if there's a next page
		response.put("hasNextBoostPost", boostedPostss.hasNext()); // for frontend
		response.put("nextCursor", page + 1); // Increment page for next request

		// Cache the data in Redis for future requests
		redisTemplate.opsForValue().set(redisKey, response, 10, TimeUnit.MINUTES); // Cache for 10 minutes

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
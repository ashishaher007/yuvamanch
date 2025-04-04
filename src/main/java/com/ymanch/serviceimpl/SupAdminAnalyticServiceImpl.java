package com.ymanch.serviceimpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ymanch.exception.ResourceNotFoundException;
import com.ymanch.model.CustomResponseModel;
import com.ymanch.model.PostModel;
import com.ymanch.repository.PostRepository;
import com.ymanch.service.SupAdminAnalyticService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SupAdminAnalyticServiceImpl implements SupAdminAnalyticService {

	private PostRepository postRepo;
	private RedisTemplate<String, Object> redisTemplate;

	public SupAdminAnalyticServiceImpl(PostRepository postRepo, RedisTemplate<String, Object> redisTemplate) {
		super();
		this.postRepo = postRepo;
		this.redisTemplate = redisTemplate;
	}

	@Transactional
	@Override
	public ResponseEntity<Map<String, Object>> getTodaysPostsByDistrict() {
		if (log.isInfoEnabled()) {
			log.info("***** Inside SupAdminAnalyticServiceImpl - getTodaysPostsByDistrict *****");
		}
		Map<String, Object> resp = new HashMap<>();
		List<Map<String, Object>> districtDetails = new ArrayList<>();
		Integer total = 0;
		// cache
		String key = "todaysPostsByDistrictCache";
		// fetch from cache
		Map<String, Object> cacheResponse = (Map<String, Object>) redisTemplate.opsForValue().get(key);
		if (cacheResponse != null) {
			return new ResponseEntity<>(cacheResponse, HttpStatus.OK);
		}

		List<Object[]> postResponse = postRepo.fetchAllTodyaPost();
		for (Object[] objects : postResponse) {
			String districtName = (String) objects[0];
			Integer count = ((Number) objects[1]).intValue();
			Long districtId = ((Number) objects[2]).longValue();
			// Create a map for the district with name, count, and ID
			Map<String, Object> districtMap = new HashMap<>();
			districtMap.put("districtName", districtName);
			districtMap.put("postCount", count);
			districtMap.put("districtId", districtId);
			// Add the district map to the list
			districtDetails.add(districtMap);
			total += count;
		}
		resp.put("details", districtDetails);
		resp.put("todaysTotalPostCount", total);

		// add response to cache
		redisTemplate.opsForValue().set(key, resp, 1, TimeUnit.MINUTES);
		// Return the response
		return new ResponseEntity<>(resp, HttpStatus.OK);
	}

	@Transactional
	@Override
	public ResponseEntity<CustomResponseModel<PostModel>> getTodaysPostsDetailsByDistrict(long districtId, int page,
			int size) {
		if (log.isInfoEnabled()) {
			log.info("***** Inside SupAdminAnalyticServiceImpl - getTodaysPostsDetailsByDistrict *****");
		}

		// Clear cache if page is 0
		if (page == 0) {

			String redisKey = "todaysPost:" + ":page" + ":*";
//			redisTemplate.delete(redisKey); // Remove the cache if cursor is 0
			redisTemplate.delete(redisTemplate.keys(redisKey));
		}
		// cache
		String key = "todaysPost:" + ":page:" + page + ":size:" + size;
		// Retrieve data from cache
		Object cachedData = redisTemplate.opsForValue().get(key);
		if (cachedData != null) {
			try {
				ObjectMapper objectMapper = new ObjectMapper();
				CustomResponseModel<PostModel> cacheData = objectMapper.convertValue(cachedData,
						CustomResponseModel.class);
				return ResponseEntity.ok(cacheData);
			} catch (Exception e) {
				// TODO: handle exception
			}

		}

		// to get current day data
		LocalDateTime startOfDay = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS); // Today 00:00:00
		LocalDateTime endOfDay = startOfDay.plusDays(1); // Tomorrow 00:00:00

		Pageable pageable = PageRequest.of(page, size);
		Page<PostModel> posts = postRepo.findPostsByDistrictId(startOfDay, endOfDay, districtId, pageable);
		if (posts.isEmpty()) {
			throw new ResourceNotFoundException("Post no found");
		} else {
			CustomResponseModel<PostModel> response = new CustomResponseModel<>(posts.getContent(),
					posts.getTotalPages(), posts.getNumber(), posts.getTotalElements(), posts.getSize(),
					posts.hasNext(), posts.getNumber() + 1);

			// add data in cache
			redisTemplate.opsForValue().set(key, response, 10, TimeUnit.MINUTES); // Cache for 1 minute
			return ResponseEntity.status(HttpStatus.OK).body(response);
		}
	}
}

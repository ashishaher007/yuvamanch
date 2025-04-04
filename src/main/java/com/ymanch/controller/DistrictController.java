package com.ymanch.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ymanch.entity.District;
import com.ymanch.repository.DistrictRepository;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ymanch/users/district")
@Slf4j
public class DistrictController {

	private DistrictRepository districtRepo;
	private RedisTemplate<String, Object> redisTemplate;

	public DistrictController(DistrictRepository districtRepo, RedisTemplate<String, Object> redisTemplate) {
		super();
		this.districtRepo = districtRepo;
		this.redisTemplate = redisTemplate;
	}

	@Transactional
	@Operation(summary = "Get Districts API", description = "This API is used to get various districts")
	@GetMapping("/getAllDistricts")
	ResponseEntity<Object> getAllDistricts() {
		log.info("***** Inside - DistrictController - getAllDistricts *****");
		Map<Object, Object> response = new HashMap<>();
		String redisKey = "districtData";
		// Check if the data is already in cache
		Object cachedData = redisTemplate.opsForValue().get(redisKey);
		if (cachedData != null) {
			return new ResponseEntity<>(cachedData, HttpStatus.OK); // Return cached data if available
		}
		List<District> data = districtRepo.findAll();
		response.put("getAllDistrictData", data);
		// Cache the data in Redis
		redisTemplate.opsForValue().set(redisKey, response, 10, TimeUnit.DAYS); // Cache for 1 minute
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}

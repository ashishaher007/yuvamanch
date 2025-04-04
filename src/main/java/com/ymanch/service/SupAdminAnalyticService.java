package com.ymanch.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.ymanch.model.CustomResponseModel;
import com.ymanch.model.PostModel;

public interface SupAdminAnalyticService {

	ResponseEntity<Map<String, Object>> getTodaysPostsByDistrict();

	ResponseEntity<CustomResponseModel<PostModel>> getTodaysPostsDetailsByDistrict(long districtId, int page, int size);

}

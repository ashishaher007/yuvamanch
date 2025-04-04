package com.ymanch.controller.SuperAdmin;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ymanch.model.CustomResponseModel;
import com.ymanch.model.PostModel;
import com.ymanch.service.PostService;
import com.ymanch.service.SupAdminAnalyticService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ymanch/supadmin/analytics")
@Slf4j
public class SuperAdminAnalyticsController {

	private SupAdminAnalyticService supAdminAnalyticService;

	public SuperAdminAnalyticsController(SupAdminAnalyticService supAdminAnalyticService) {
		this.supAdminAnalyticService = supAdminAnalyticService;
	}

	@Operation(summary = "Today's Post Count API", description = "This API is to retrieve today's post count")
	@GetMapping("/posts/count")
	public ResponseEntity<Map<String, Object>> getTodaysPost() {
		return supAdminAnalyticService.getTodaysPostsByDistrict();
	}

	@Operation(summary = "Today's Post API", description = "This API is to retrieve today's post details by district")
	@GetMapping("/posts/details/{districtId}")
	private ResponseEntity<CustomResponseModel<PostModel>> getPosts(@PathVariable long districtId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
		return supAdminAnalyticService.getTodaysPostsDetailsByDistrict(districtId, page, size);
	}
	
	

}

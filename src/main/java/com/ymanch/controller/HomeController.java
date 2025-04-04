package com.ymanch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ymanch.helper.CheckUserStatus;
import com.ymanch.helper.CommonFunctions;
import com.ymanch.helper.RequireUserAccess;
import com.ymanch.service.AccessControlService;
import com.ymanch.service.HomeService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ymanch/users/home")
@Slf4j
public class HomeController {

	private HomeService homeService;
	private AccessControlService accessControlService;

	@Autowired
	private CommonFunctions commonFunctions;

	public HomeController(HomeService homeService, AccessControlService accessControlService,
			CommonFunctions commonFunctions) {
		super();
		this.homeService = homeService;
		this.accessControlService = accessControlService;
		this.commonFunctions = commonFunctions;
	}

//	@Operation(summary = "Home Api", description = "This API is used to get all information on home page")
//	@GetMapping("/home")
//	@CheckUserStatus // user define annotation
//	ResponseEntity<Object> home() {
//		log.info("***** Inside - HomeController - home");
//		return homeService.getAllHomePageData();
//	}

	@Operation(summary = "Last Activity Api", description = "This API is used to get last activity details on home page")
	@GetMapping("/lastActivity")
	@CheckUserStatus // user define annotation
	ResponseEntity<Object> lastActivity(HttpServletRequest request) {
		log.info("***** Inside - HomeController - lastActivity");
		Long userId = commonFunctions.getUserIdFromRequest(request);
		return homeService.getLastActivityData(userId);
	}

	@Operation(summary = "Boost Api With Pagination", description = "This API is used to get all boosted post information on home page")
	@GetMapping("/boostPost")
	ResponseEntity<Object> boostPost(HttpServletRequest request, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {
		log.info("***** Inside - HomeController - boostPost");
		Long userId = commonFunctions.getUserIdFromRequest(request);
		accessControlService.verifyUserAccess(userId);
		return homeService.fetchBoostPost(page, size, userId);
	}

	@Operation(summary = "Home Api With Pagination", description = "This API is used to get all information on home page")
	@GetMapping("/home/v1/{userId}")
	ResponseEntity<Object> homePage(@RequestParam(defaultValue = "0") Long cursor,
			@RequestParam(defaultValue = "5") int size, @PathVariable long userId) {
		log.info("***** Inside - HomeController - homePage");
		accessControlService.verifyUserAccess(userId);
		return homeService.getAllHomePageDetails(cursor, size, userId);
	}

}

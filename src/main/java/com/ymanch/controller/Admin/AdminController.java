package com.ymanch.controller.Admin;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ymanch.helper.CommonMessages;
import com.ymanch.model.UserRegisterModel;
import com.ymanch.service.AdminService;
import com.ymanch.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ymanch/admin")
@Slf4j
public class AdminController {
	private Map<Object, Object> response;
	private CommonMessages msg;

	private AdminService adminService;
	private UserService userService;

	public AdminController(AdminService adminService, UserService userService) {
		super();
		this.adminService = adminService;
		this.userService = userService;
	}

	@Operation(summary = "Admin Register API", description = "This API is used to register an admin")
	@PostMapping("/adminRegister")
	ResponseEntity<Object> adminRegister(@Valid @RequestBody UserRegisterModel admin) {
		log.info("***** Inside - AdminController - adminRegister *****");
		return userService.addUser(admin);
	}

	@GetMapping("/adminTest")
	ResponseEntity<Object> adminTest() {
		log.info("***** Inside - UserController - test *****");
		response.put("status", CommonMessages.SUCCESS);
		response.put("message", msg.TEST_MESSAGE);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Operation(summary = "User Activity ", description = "This API is used to get total counts")
	@GetMapping("/userActivity")
	ResponseEntity<Object> userActivity() {
		log.info("***** Inside - AdminController - userActivity *****");
		return adminService.getUserActivityCounts();
	}

	@Operation(summary = "User Line Chart ", description = "This API is used to user related details")
	@GetMapping("/userLineChart")
	ResponseEntity<Object> userLineChart() {
		log.info("***** Inside - AdminController - userLineChart *****");
		return adminService.getUserLineChartDetails();
	}

	@Operation(summary = "Get Logged In Details Api", description = "This Api is used to get the logged in user details for admin panel by token")
	@GetMapping("/loggedInData")
	public ResponseEntity<Object> loggedInData(HttpServletRequest request) {
		log.info("***** Inside UserController - refreshToken *****");
		String requestHeaderToken = request.getHeader("Authorization");
		return adminService.logInData(requestHeaderToken);
	}

	@Operation(summary = "Application Statistics", description = "This API is used to retrieve all statistics of the application")
	@GetMapping("/stats")
	ResponseEntity<Object> stats(HttpServletRequest request) {
		log.info("***** Inside - AdminController - stats *****");
		return adminService.stats(request);
	}

}
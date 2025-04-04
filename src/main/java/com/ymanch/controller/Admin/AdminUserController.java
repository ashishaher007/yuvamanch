package com.ymanch.controller.Admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ymanch.helper.CommonFunctions;
import com.ymanch.helper.RequireUserAccess;
import com.ymanch.service.AdminService;
import com.ymanch.service.GroupService;
import com.ymanch.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ymanch/admin/user")
@Slf4j
public class AdminUserController {

	private AdminService adminService;
	private UserService userService;
	private GroupService groupService;
	private CommonFunctions commonFunctions;

	public AdminUserController(AdminService adminService, UserService userService, GroupService groupService,
			CommonFunctions commonFunctions) {
		super();
		this.adminService = adminService;
		this.userService = userService;
		this.groupService = groupService;
		this.commonFunctions = commonFunctions;
	}

	/*
	 * @Operation(summary = "Get All User Api", description =
	 * "This api is used to get all details of user")
	 * 
	 * @GetMapping("/getAllUser") ResponseEntity<Object> getAllUser() throws
	 * Exception {
	 * log.info("***** Inside - AdminUserController - getAllUser *****"); return
	 * adminService.getAllUserDetails(); }
	 */

	@Operation(summary = "Delete User Api", description = "This Api is used to delete a user by userId")
	@DeleteMapping("/deleteUser/{userId}")
	ResponseEntity<Object> deleteUser(@PathVariable long userId) {
		if (log.isInfoEnabled()) {
			log.info("***** Inside AdminUserController - deleteUser *****");
		}
		return userService.removeUser(userId);

	}

	@Operation(summary = "Get All User Api", description = "This api is used to get all details of user")
	@GetMapping("/getAllUserWithPagination")
	ResponseEntity<Object> getAllUserWithPagination(@RequestParam(required = false) String firstName,
			@RequestParam(defaultValue = "37") long districtId, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) throws Exception {
		if (log.isInfoEnabled()) {
			log.info("***** Inside AdminUserController - getAllUser *****");
		}
		return adminService.getAllUserDetailsWithPagination(firstName, districtId, page, size);
	}

	@Operation(summary = "Get All Groups Detail API", description = "This API is used to retrieve all groups created by the admin (user) based on the user ID")
	@GetMapping("/getGroups")
	@RequireUserAccess
	public ResponseEntity<Object> getGroups(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		if (log.isInfoEnabled()) {
			log.info("***** Inside AdminUserController - getGroups *****");
		}
//		Long compHostUserId = commonFunctions.getUserIdFromRequest(request);
//		System.out.println(compHostUserId);
		return groupService.retrieveGrpDetails(page, size);
	}

}

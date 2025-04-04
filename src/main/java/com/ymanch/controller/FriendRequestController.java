package com.ymanch.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ymanch.helper.CheckUserStatus;
import com.ymanch.helper.CommonFunctions;
import com.ymanch.helper.RequireUserAccess;
import com.ymanch.service.AccessControlService;
import com.ymanch.service.FriendRequestService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ymanch/users/friendrequest")
@Slf4j
public class FriendRequestController {

	private FriendRequestService friendRequestService;
	private AccessControlService accessControlService;
	private CommonFunctions commonFunctions;

	public FriendRequestController(FriendRequestService friendRequestService, AccessControlService accessControlService,
			CommonFunctions commonFunctions) {
		super();
		this.friendRequestService = friendRequestService;
		this.accessControlService = accessControlService;
		this.commonFunctions = commonFunctions;
	}

	@Operation(summary = "Friend Request Api", description = "This Api is used to send a friend request")
	@PostMapping("/sendFriendRequest/{senderId}/{receiverId}")
	@CheckUserStatus
	ResponseEntity<Object> sendFriendRequest(@PathVariable long senderId, @PathVariable long receiverId) {
		log.info("***** Inside FriendRequestController - sendFriendRequest *****");
//		accessControlService.verifyUserAccess(senderId);
		return friendRequestService.sendRequest(senderId, receiverId);
	}

	@Operation(summary = "Approve Friend Request Api", description = "This Api is used to approve a friend request")
	@PutMapping("/approveFriendRequest/{friendRequestId}")
	ResponseEntity<Object> approveFriendRequest(@PathVariable long friendRequestId) {
		log.info("***** Inside FriendRequestController - approveFriendRequest *****");
		return friendRequestService.approveRequest(friendRequestId);
	}

	@Operation(summary = "Reject Friend Request Api", description = "This Api is used to reject a friend request")
	@PutMapping("/rejectFriendRequest/{friendRequestId}")
	ResponseEntity<Object> rejectFriendRequest(@PathVariable long friendRequestId) {
		log.info("***** Inside FriendRequestController - rejectFriendRequest *****");
		return friendRequestService.rejectRequest(friendRequestId);
	}

	@Operation(summary = "Get Friend Request Api", description = "This Api is used to get all friend requests by the receiver's ID")
	@GetMapping("/getFriendRequest/{receiverId}")
	@CheckUserStatus
	ResponseEntity<Object> getFriendRequest(@PathVariable long receiverId) {
		log.info("***** Inside FriendRequestController - getFriendRequest *****");
		accessControlService.verifyUserAccess(receiverId);
		return friendRequestService.getAllFriendRequest(receiverId);

	}

	@Operation(summary = "Get All Friends List Api", description = "This Api is used to get all friends by user's Id")
	@GetMapping("/getFriendsList/{userId}")
	@CheckUserStatus
	ResponseEntity<Object> getFriendsList(@PathVariable long userId) {
		log.info("***** Inside FriendRequestController - getFriendsList *****");
//		accessControlService.verifyUserAccess(userId);
		return friendRequestService.getAllFriendsList(userId);

	}

	@Operation(summary = "Reject Friend Request Api", description = "This Api is used to reject a friend request by current user and target user ID")
	@PutMapping("/rejectFriendRequest/v1/{currentUserId}/{targetUserId}")
	@CheckUserStatus
	ResponseEntity<Object> rejectFriendRequest(@PathVariable long currentUserId, @PathVariable long targetUserId) {
		log.info("***** Inside FriendRequestController - rejectFriendRequest *****");
		accessControlService.verifyUserAccess(currentUserId);
		return friendRequestService.rejectRequest(currentUserId, targetUserId);
	}

	@Operation(summary = "Search Friends API", description = "This Api is used to search for a specific friend by name")
	@GetMapping("/searchFriends/{userName}")
	@RequireUserAccess
	ResponseEntity<Object> searchFriends(HttpServletRequest request, @PathVariable String userName) {
		log.info("***** Inside UserController - searchUser *****");
		Long userId = commonFunctions.getUserIdFromRequest(request);
		return friendRequestService.findUserByName(userId, userName);
	}

}

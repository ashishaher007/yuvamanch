package com.ymanch.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ymanch.exception.ResourceNotFoundException;
import com.ymanch.helper.CheckUserStatus;
import com.ymanch.service.AccessControlService;
import com.ymanch.service.NotifyService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ymanch/users/notification")
@Slf4j
public class NotificationController {

	
	@Autowired
	private NotifyService notifyService;
	
	@Autowired
	private AccessControlService accessControlService;
	
	@Operation(summary = "Get All Notification Api", description = "This api is used to get all notification details by receiver Id")
	@GetMapping("/getAllNotification/{userId}")
	@CheckUserStatus // user define annotations
	 ResponseEntity<Object> getAllNotification(@PathVariable long userId) throws Exception {
		log.info("***** Inside - NotificationController - getAllNotification *****");
		accessControlService.verifyUserAccess(userId);
		ResponseEntity<Object> data = notifyService.getAllNotifyDetails(userId);
		return data;
	}

	@Operation(summary = "Update Notification Status Api", description = "This API is used to upadte notification by receiver Id")
	@PutMapping("/updateNotificationStatus/{receiverId}")
	@CheckUserStatus // user define annotations
	ResponseEntity<Object> updateNotificationStatus(@PathVariable long receiverId) throws ResourceNotFoundException {
		log.info("***** Inside - NotificationController - updateNotificationStatus");
		accessControlService.verifyUserAccess(receiverId);
		return notifyService.updateNotificationStatus(receiverId);
	}

}

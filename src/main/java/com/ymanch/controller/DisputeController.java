package com.ymanch.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ymanch.entity.DisputeTitles;
import com.ymanch.entity.Disputes;
import com.ymanch.helper.CheckUserStatus;
import com.ymanch.service.AccessControlService;
import com.ymanch.service.DisputeService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ymanch/users/dispute")
@Slf4j
public class DisputeController {

	private DisputeService disputeService;
	private AccessControlService accessControlService;

	public DisputeController(DisputeService disputeService, AccessControlService accessControlService) {
		super();
		this.disputeService = disputeService;
		this.accessControlService = accessControlService;
	}

	@Operation(summary = "Raise Dispute API", description = "This API is used to raise a dispute by user Id and post Id")
	@PostMapping("/raiseDispute/{userId}/{postId}/{disputeTitleId}")
	@CheckUserStatus
	public ResponseEntity<Object> raiseDispute(@PathVariable long userId, @PathVariable long postId,@PathVariable long disputeTitleId,@RequestBody Disputes dispute)  {
		log.info("***** Inside DisputeController - raiseDispute *****");
		accessControlService.verifyUserAccess(userId);
		return disputeService.raiseDispute(userId, postId,disputeTitleId,dispute);
	}
}
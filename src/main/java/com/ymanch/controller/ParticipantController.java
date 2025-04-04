package com.ymanch.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ymanch.helper.CommonFunctions;
import com.ymanch.helper.RequireUserAccess;
import com.ymanch.service.EventService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/ymanch/users/participant")
@Slf4j
public class ParticipantController {

	private EventService eventService;
    private CommonFunctions commonFunctions;

	public ParticipantController(EventService eventService, CommonFunctions commonFunctions) {
		super();
		this.eventService = eventService;
		this.commonFunctions = commonFunctions;
	}

	@Operation(summary = "Add Participant Api", description = "This API is used to participant any event")
	@PostMapping("/add/{eventId}")
	@RequireUserAccess
	public ResponseEntity<Object> add(HttpServletRequest request, @PathVariable Long eventId) {
		if (log.isInfoEnabled()) {
			log.info("***** Inside ParticipantController - add *****");
		}
		Long userId = commonFunctions.getUserIdFromRequest(request);
		return eventService.addParticipant(userId, eventId);
	}

	@Operation(summary = "Get Participants Api", description = "This API retrieves the list of participants for an event")
	@GetMapping("/getParticipants/{eventUUID}")
	@RequireUserAccess
	public ResponseEntity<Object> getParticipants(HttpServletRequest request, @PathVariable String eventUUID,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
		if (log.isInfoEnabled()) {
			log.info("***** Inside ParticipantController - getParticipants *****");
		}
		long hostUserId = commonFunctions.getUserIdFromRequest(request);
		return eventService.getParticipants(hostUserId, eventUUID, page, size);
	}

	@Operation(summary = "Exit Or Remove Participant API", description = "The API provides functionality for users to exit events on their own, or for host admin to remove users from the events as needed")
	@DeleteMapping("/exitEvent/{userId}/{eventId}")
	@RequireUserAccess
	public ResponseEntity<Object> exitEvent(HttpServletRequest request, @PathVariable long userId,
			@PathVariable long eventId) {
		if (log.isInfoEnabled()) {
			log.info("***** Inside ParticipantController - exitEvent *****");
		}
		Long eventHostUserId = commonFunctions.getUserIdFromRequest(request);
		return eventService.exitEvent(eventHostUserId, userId, eventId);
	}

}

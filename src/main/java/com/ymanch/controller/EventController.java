package com.ymanch.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ymanch.helper.CheckUserStatus;
import com.ymanch.model.EventModel;
import com.ymanch.service.AccessControlService;
import com.ymanch.service.EventService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ymanch/users/event")
@Slf4j
public class EventController {

	private EventService eventService;
	private AccessControlService accessControlService;

	public EventController(EventService eventService, AccessControlService accessControlService) {
		super();
		this.eventService = eventService;
		this.accessControlService = accessControlService;
	}

	@Operation(summary = "Create New Event Api", description = "This API is used to create an event by user ID")
	@PostMapping("/create/{hostUserId}/{districtId}")
	@CheckUserStatus
	public ResponseEntity<Object> create(@PathVariable long hostUserId, @PathVariable long districtId,
			@RequestParam long eventCatgId, @Valid @ModelAttribute EventModel event) {
		log.info("***** Inside EventController - create *****");
		accessControlService.verifyUserAccess(hostUserId);
		return eventService.create(hostUserId, eventCatgId, districtId, event);
	}

	@Operation(summary = "Delete Event Api", description = "This API is used to delete an event by user and event ID ")
	@DeleteMapping("/delete/{hostUserId}/{eventId}")
	@CheckUserStatus
	public ResponseEntity<Object> delete(@PathVariable long hostUserId, @PathVariable long eventId) {
		log.info("***** Inside EventController - delete *****");
//		accessControlService.verifyUserAccess(hostUserId);
		return eventService.delete(hostUserId, eventId);
	}

	@Operation(summary = "All Event Details API", description = "This API is used to retrieve all event details")
	@GetMapping("/getAllEvents/{userId}/{districtId}")
	@CheckUserStatus
	public ResponseEntity<Object> getAllEvents(@PathVariable long userId, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size, @PathVariable long districtId) {
		log.info("***** Inside - EventController - getAllEvents *****");
		accessControlService.verifyUserAccess(userId);
		return eventService.getAllEvents(page, size, districtId);
	}

	@Operation(summary = "Get Event Details Api", description = "This API is used to retrieve event details by using the event ID")
	@GetMapping("/getDetails/{userId}/{eventUUID}")
	@CheckUserStatus
	public ResponseEntity<Object> getDetails(@PathVariable long userId, @PathVariable String eventUUID) {
		log.info("***** Inside EventController - getDetails *****");
		accessControlService.verifyUserAccess(userId);
		return eventService.getEventDetails(eventUUID,userId);
	}

	@Operation(summary = "Get Host Event Details Api", description = "This API fetches event details hosted by a specific user, identified by their host ID")
	@GetMapping("/getHostEvents/{hostUserId}")
	@CheckUserStatus
	public ResponseEntity<Object> getHostEvents(@PathVariable long hostUserId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
		log.info("***** Inside EventController - getHostEvents *****");
		accessControlService.verifyUserAccess(hostUserId);
		return eventService.getHostEvents(hostUserId, page, size);
	}

	@Operation(summary = "Edit Event Api", description = "This API is used to edit the event by user and event ID")
	@PutMapping("/edit/{hostUserId}/{eventId}")
	@CheckUserStatus
	public ResponseEntity<Object> edit(@PathVariable long hostUserId, @PathVariable long eventId,
			@ModelAttribute EventModel event) {
		log.info("***** Inside EventController - edit *****");
		accessControlService.verifyUserAccess(hostUserId);
		return eventService.edit(hostUserId, eventId, event);
	}

}

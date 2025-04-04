package com.ymanch.service;

import org.springframework.http.ResponseEntity;

import com.ymanch.entity.EventCategory;
import com.ymanch.model.EventModel;

import jakarta.validation.Valid;

public interface EventService {

	ResponseEntity<Object> add(EventCategory evCatgeory);

	ResponseEntity<Object> get();

	ResponseEntity<Object> create(long hostUserId, long eventCatgId, long districtId, @Valid EventModel event);

	ResponseEntity<Object> delete(long hostUserId, long eventId);

	ResponseEntity<Object> getAllEvents(int page, int size, long districtId);

	ResponseEntity<Object> getEventDetails(String eventUUID, long userId);

	ResponseEntity<Object> getHostEvents(long hostUserId, int page, int size);

	ResponseEntity<Object> edit(long hostUserId, long eventId, EventModel event);

	ResponseEntity<Object> addParticipant(Long userId, Long eventId);

	ResponseEntity<Object> getParticipants(long hostUserId, String eventUUID, int page, int size);

	ResponseEntity<Object> exitEvent(Long compHostUserId, long userId, long eventId);

}

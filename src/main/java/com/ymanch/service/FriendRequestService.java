package com.ymanch.service;

import org.springframework.http.ResponseEntity;

public interface FriendRequestService {

	ResponseEntity<Object> sendRequest(long senderId, long receiverId);

	ResponseEntity<Object> approveRequest(long friendRequestId);

	ResponseEntity<Object> rejectRequest(long friendRequestId);

	ResponseEntity<Object> getAllFriendRequest(long receiverId);

	ResponseEntity<Object> getAllFriendsList(long userId);

	ResponseEntity<Object> rejectRequest(long currentUserId, long targetUserId);

	ResponseEntity<Object> findUserByName(Long userId, String userName);

}

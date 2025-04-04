package com.ymanch.service;

import org.springframework.http.ResponseEntity;

public interface NotifyService {

	ResponseEntity<Object> getAllNotifyDetails(long userId) throws Exception;

	ResponseEntity<Object> updateNotificationStatus(long receiverId);

}

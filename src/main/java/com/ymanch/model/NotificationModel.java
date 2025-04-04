package com.ymanch.model;

import java.time.LocalDateTime;

import com.ymanch.helper.Enums.NotificationStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NotificationModel {

	private String notificationMessage;

	private String notificationStatus;

	private LocalDateTime notificationCreatedAt;

	private int countUnreadNotification;
}

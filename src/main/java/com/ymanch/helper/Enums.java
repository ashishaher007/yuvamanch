package com.ymanch.helper;

public class Enums {
	public enum NotificationStatus {
		READ, UNREAD
	}

	public enum NotificationType {
		BIRTHDAY, MENTION
	}

	public enum DisputeStatus {
		PENDING, IN_PROGRESS, RESOLVED
	}

	public enum PostOwnerType {
		PUBLIC, GROUP, PAGE, ADMIN, PRIVATE, PUBLIC_EVENT, PRIVATE_EVENT, REEL, SUP_ADMIN,SUP_ADMIN_ANNOUNCEMENT
	}

	public enum UserRole {
		ROLE_USER, ROLE_ADMIN, ROLE_SUPER_ADMIN
	}

	public enum Status {
		ACTIVE, ENDED
	}
}

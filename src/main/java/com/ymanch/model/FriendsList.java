package com.ymanch.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FriendsList {
	private long userId;
	private String userFirstName;
	private String userLastName;
	private String userProfileImagePath;
	private long friendRequestId;
	private String status;
	private String userUUID;
}

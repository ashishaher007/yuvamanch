package com.ymanch.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FriendRequestList {

	private long userId;
	private String userFirstName;
	private String userLastName;
	private String userProfileImagePath;
	private String friendRequestSentDate;
	private long friendRequestId;

}

package com.ymanch.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AdminDisputeDetails {
	private long disputeId;
	private long userId;
	private long postId;
	private String userFirstName;
	private String userLastName;
	private String userEmail;
	private String userProfileImagePath;
	private String postName;
	private String postImageUrl;
	private long disputeCount;

	public AdminDisputeDetails(long disputeId, long userId, long postId, String userFirstName, String userLastName,
			String userEmail, String userProfileImagePath, String postName, String postImageUrl, long disputeCount) {
		super();
		this.disputeId = disputeId;
		this.userId = userId;
		this.postId = postId;
		this.userFirstName = userFirstName;
		this.userLastName = userLastName;
		this.userEmail = userEmail;
		this.userProfileImagePath = userProfileImagePath;
		this.postName = postName;
		this.postImageUrl = postImageUrl;
		this.disputeCount = disputeCount;
	}

}

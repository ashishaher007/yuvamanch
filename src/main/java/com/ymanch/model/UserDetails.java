package com.ymanch.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetails {
	private long userId;
	private String userFirstName;
	private String userLastName;
	private String userProfileImagePath;
	private String userEmail;
	private LocalDateTime userCreatedAt;
	private String uuid;

	public UserDetails(long userId, String userFirstName, String userLastName, String userProfileImagePath,
			String userEmail, LocalDateTime userCreatedAt, String uuid) {
		super();
		this.userId = userId;
		this.userFirstName = userFirstName;
		this.userLastName = userLastName;
		this.userProfileImagePath = userProfileImagePath;
		this.userEmail = userEmail;
		this.userCreatedAt = userCreatedAt;
		this.uuid = uuid;
	}

}

package com.ymanch.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserDetails {
	private long userId;
	private String userFirstName;
	private String userLastName;
	private String userEmail;
	private String userProfileImagePath;
	private LocalDateTime userCreatedAt;
	private int totalPostCount;
	private String userDateOfBirth;
	private String userAddress;
}

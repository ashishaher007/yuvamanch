package com.ymanch.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchModel {
	private long userId;
	private String userFirstName;
	private String userLastName;
	private String userProfileImagePath;
	private String userUUID;
}

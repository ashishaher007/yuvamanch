package com.ymanch.model;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateModel {
	@NotEmpty(message = "User date of birth should not be Empty")
	private String userDateOfBirth;
	@NotEmpty(message = "User address should not be Empty")
	private String userAddress;
	@NotEmpty(message = "User profile image path should not be Empty")
	private MultipartFile userProfileImagePath;
	@NotEmpty(message = "User cover profile image path should not be Empty")
	private MultipartFile userCoverProfileImagePath;
//	@NotEmpty(message = "User password  should not be empty")
	private String userPassword;

	private String userFirstName;

	private String userLastName;

	private long orgId;
}

package com.ymanch.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginModel {

	private String userEmailOrMobileNumber;

	@NotEmpty(message = "User password should not be empty")
	private String userPassword;

	private String loginType;

}

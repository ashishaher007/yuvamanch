package com.ymanch.model;

import com.ymanch.helper.Enums.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterModel {

	@NotEmpty(message = "User first name should not be Empty")
	private String userFirstName;

	@NotEmpty(message = "User last name should not be Empty")
	private String userLastName;

//	@Email
	private String userEmail;

//	@Pattern(regexp = "FEMALE", message = "Gender must be FEMALE")
	private String userGender;

	@NotEmpty(message = "User date of birth should not be Empty")
	private String userDateOfBirth;

	@NotEmpty(message = "User address should not be Empty")
	private String userAddress;

	private String userProfileImagePath;

	private String userCoverProfileImagePath;

	@NotEmpty(message = "User password should not be empty")
	private String userPassword;

//	@NotEmpty(message = "User district should not be empty")
	private long districtId;

	@NotEmpty(message = "User mobile no should not be empty")
	private String userMobileNumber;

	@Pattern(regexp = "ROLE_USER|ROLE_ADMIN|ROLE_SUPER_ADMIN", message = "Role must be either ROLE_USER || ROLE_ADMIN || ROLE_SUPER_ADMIN")
	private String userRole;

	private boolean isTermsAndConditionsAccepted;

}

package com.ymanch.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostModel {

	private long postId;
	private String postName;
	private String postImageUrl;
	private String userProfileImagePath;
	private String userFirstName;
	private String userLastName;
	private String userEmail;
	private String postType;

}

package com.ymanch.model;

import java.util.ArrayList;
import java.util.List;

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
public class StoryUserModel {

	private long userId;
	private String userFirstName;
	private String userLastName;
	private String userProfileImagePath;
	private int storiesCount;
	private int indexNumber;
	private List<StoryModel> storyDetails = new ArrayList<>();

}

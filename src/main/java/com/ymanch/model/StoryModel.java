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
public class StoryModel {
	private long storyId;
	private String storyUrl;
	private String storyCreatedAt;
	private String description;
	private String storyType;
	private String videoThumbnailUrl;

}

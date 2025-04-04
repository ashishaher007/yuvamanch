package com.ymanch.model;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupModel {
	private long groupId;
	private String groupName;
	private String groupDescription;
	private String groupCoverProfileImagePath;
	private long adminId;
	private String adminUserFirstName;
	private String adminUserLastName;
	private String adminUserProfileImagePath;
	private LocalDateTime groupCreatedAt;
	private String groupUUID;
}

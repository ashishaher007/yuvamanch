package com.ymanch.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class PagesModelV1 {
	private long pagesId;
	private String pageName;
	private String pageDescription;
	private String pageCoverProfileImagePath;
	private long adminId;
	private String adminUserFirstName;
	private String adminUserLastName;
	private String adminUserProfileImagePath;
	private LocalDateTime pageCreatedAt;
	private boolean isPageFollowed;// used in getAllPages and getPages api. Rest by default value.
	private String puuid;
	private String linkUrlName;
	private String linkUrl;

	public PagesModelV1(long pagesId, String pageName, String pageDescription, String pageCoverProfileImagePath,
			long adminId, String adminUserFirstName, String adminUserLastName, String adminUserProfileImagePath,
			LocalDateTime pageCreatedAt, String puuid, String linkUrlName, String linkUrl) {
		super();
		this.pagesId = pagesId;
		this.pageName = pageName;
		this.pageDescription = pageDescription;
		this.pageCoverProfileImagePath = pageCoverProfileImagePath;
		this.adminId = adminId;
		this.adminUserFirstName = adminUserFirstName;
		this.adminUserLastName = adminUserLastName;
		this.adminUserProfileImagePath = adminUserProfileImagePath;
		this.pageCreatedAt = pageCreatedAt;
		this.puuid = puuid;
		this.linkUrlName = linkUrlName;
		this.linkUrl = linkUrl;
	}

	public PagesModelV1() {
		super();
	}

}

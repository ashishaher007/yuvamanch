package com.ymanch.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostIndexPage {
	private long postId;
	private long userId;
	private String userProfileImageUrl;
	private String userName;
	private String postImageURl;
	private String postType;
	private String videoThumbnailUrl;
	private LocalDateTime postCreatedAt;
	private String postName;
	private long totalCountOFReact;
	private Long totalComments; // Total comments count
	private boolean userReactStatus;
	private boolean isPostSaved;
	private String postUploadedAt;
	private String userUUID;
	private String description;
	private List<PostIndexComments> commentsAndReacts = new ArrayList<>();

	public PostIndexPage(long postId, long userId, String userProfileImageUrl, String userName, String postImageURl,
			String postType, String videoThumbnailUrl, LocalDateTime postCreatedAt, String postName,
			long totalCountOFReact, boolean userReactStatus, boolean isPostSaved, String userUUID, String description) {
		this.postId = postId;
		this.userId = userId;
		this.userProfileImageUrl = userProfileImageUrl;
		this.userName = userName;
		this.postImageURl = postImageURl;
		this.postType = postType;
		this.videoThumbnailUrl = videoThumbnailUrl;
		this.postCreatedAt = postCreatedAt;
		this.postName = postName;
		this.totalCountOFReact = totalCountOFReact;
		this.userReactStatus = userReactStatus;
		this.isPostSaved = isPostSaved;
		this.userUUID = userUUID;
		this.description = description;
	}

	public PostIndexPage(Object[] post) {
		// TODO Auto-generated constructor stub
	}

	public PostIndexPage(long postId, long userId, String userProfileImageUrl, String userName, String postImageURl,
			String postType, String videoThumbnailUrl, LocalDateTime postCreatedAt, String postName,
			String postUploadedAt) {
		this.postId = postId;
		this.userId = userId;
		this.userProfileImageUrl = userProfileImageUrl;
		this.userName = userName;
		this.postImageURl = postImageURl;
		this.postType = postType;
		this.videoThumbnailUrl = videoThumbnailUrl;
		this.postCreatedAt = postCreatedAt;
		this.postName = postName;
		this.postUploadedAt = postUploadedAt;
	}
	
	public PostIndexPage(long postId, long userId, String userProfileImageUrl, String userName, 
	        String postImageURl, String postType, String videoThumbnailUrl, LocalDateTime postCreatedAt, 
	        String postName, long totalCountOFReact, boolean userReactStatus, boolean isPostSaved, String userUUID) {
	    this.postId = postId;
	    this.userId = userId;
	    this.userProfileImageUrl = userProfileImageUrl;
	    this.userName = userName;
	    this.postImageURl = postImageURl;
	    this.postType = postType;
	    this.videoThumbnailUrl = videoThumbnailUrl;
	    this.postCreatedAt = postCreatedAt;
	    this.postName = postName;
	    this.totalCountOFReact = totalCountOFReact;
	    this.userReactStatus = userReactStatus;
	    this.isPostSaved = isPostSaved;
	    this.userUUID = userUUID;
	    }

}

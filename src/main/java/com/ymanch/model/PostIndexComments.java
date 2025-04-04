package com.ymanch.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostIndexComments {
	private String userProfileImageUrl;
	private String commentTime;
	private String comment;
	private String userName;
	private long parentCommentId;
	private long childCommentId;
	private List<PostIndexComments> children;
	
		public PostIndexComments(String userProfileImageUrl, String commentTime, String comment, String userName,
	            long parentCommentId, long childCommentId) {
	this.userProfileImageUrl = userProfileImageUrl;
	this.commentTime = commentTime;
	this.comment = comment;
	this.userName = userName;
	this.parentCommentId = parentCommentId;
	this.childCommentId = childCommentId;
	}

	
}

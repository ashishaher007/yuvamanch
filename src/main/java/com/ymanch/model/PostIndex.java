package com.ymanch.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class PostIndex {

	private long postId;
	private long userId;
	private String userProfileImageUrl;
	private String userName;
	private String postImageURl;
	private String postType;
	private String videoThumbnailUrl;
	private LocalDateTime postCreatedAt;
	private String postName;
	private String postLastReactedBy;
	private long totalCountOFReact;
	private long totalCountOfComments;
	private String description ;

	private List<PostIndexComments> commentsAndReacts = new ArrayList<>();

	public PostIndex(long postId, long userId, String userProfileImageUrl, String userName, String postImageURl,
	        String postType, String videoThumbnailUrl, LocalDateTime postCreatedAt, String postName,
	        String postLastReactedBy, long totalCountOFReact, long totalCountOfComments, String description,
	        List<PostIndexComments> commentsAndReacts) {
	    this.postId = postId;
	    this.userId = userId;
	    this.userProfileImageUrl = userProfileImageUrl;
	    this.userName = userName;
	    this.postImageURl = postImageURl;
	    this.postType = postType;
	    this.videoThumbnailUrl = videoThumbnailUrl;
	    this.postCreatedAt = postCreatedAt;
	    this.postName = postName;
	    this.postLastReactedBy = postLastReactedBy;
	    this.totalCountOFReact = totalCountOFReact;
	    this.totalCountOfComments = totalCountOfComments;
	    this.commentsAndReacts = commentsAndReacts;
	    this.description = description;
	}
	
	 @Override
	    public boolean equals(Object o) {
	        if (this == o) return true;
	        if (o == null || getClass() != o.getClass()) return false;
	        PostIndexPage that = (PostIndexPage) o;
	        return Objects.equals(postId, that.getPostId());  // Compare only by postId
	    }

	    @Override
	    public int hashCode() {
	        return Objects.hash(postId);  // Generate hash code based on postId
	    }
}

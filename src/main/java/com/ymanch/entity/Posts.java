package com.ymanch.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ymanch.helper.Enums.PostOwnerType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(indexes = {
//	    @Index(name = "idx_userId", columnList = "user_id"),
		@Index(name = "idx_isPostDeleted", columnList = "is_post_deleted"),
		@Index(name = "idx_posts_status_owner_created", columnList = "is_post_deleted, post_owner_type, post_created_at"),
		@Index(name = "idx_posts_post_id_user_id_owner_type_deleted", columnList = "post_id, user_id, post_owner_type, is_post_deleted"),
//	   
		@Index(name = "custom_post_user_idx", columnList = " user_id,is_post_deleted,post_owner_type"),
		@Index(name = "idx_optimized_posts", columnList = "is_post_deleted, post_owner_type, post_created_at, user_id"),
		@Index(name = "idx_postOwnerType", columnList = "post_owner_type"),
		@Index(name = "idx_postCreatedAt", columnList = "post_created_at"), })
//need to add index in mysql because java does not DESC at the end
//CREATE INDEX idx_posts_postId_createdAt ON posts(post_id, post_created_at DESC);
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Posts {

	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long postId;

	@Column(columnDefinition = "LONGTEXT")
	private String postName;

	@Column(columnDefinition = "LONGTEXT")
	private String postImageUrl;

	@Column(columnDefinition = "VARCHAR(255) DEFAULT 'postType'")
	private String postType;

	@Column(columnDefinition = "VARCHAR(255) DEFAULT 'videoThumbnailUrl'")
	private String videoThumbnailUrl;

	@JsonIgnore
	private Boolean isPostDeleted;

	@JsonIgnore
	private String advertisementDescription;// admin

//	  @Column(length = 1000)
//	    private String description;  // Add the description field here

	@JsonIgnore
	@CreationTimestamp
	private LocalDateTime postCreatedAt;

	@Column(name = "reach_count")
	private Integer reachCount; // Count of reach (views for >= 3 seconds)

	@Column(name = "view_count")
	private Integer viewCount;

	@Enumerated(EnumType.STRING)
	@JsonIgnore
	private PostOwnerType postOwnerType;

	@JsonIgnore
	@UpdateTimestamp
	private LocalDateTime postUpdateAt;
	
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<PostInsight> postInsights;

	@PrePersist
	@PreUpdate
	private void setDefaults() {
		if (isPostDeleted == null) {
			isPostDeleted = false; // Default value
		}
		if (videoThumbnailUrl == null || videoThumbnailUrl.isEmpty()) {
			videoThumbnailUrl = "emptyVideoThumbnailUrl";
		}
		if (postImageUrl == null || postImageUrl.isEmpty()) {
			postImageUrl = "emptyPostImageUrl";
		}
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")
	@JsonIgnore
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "groupId")
	@JsonIgnore
	private Group group;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pageId")
	@JsonIgnore
	private Pages pages;

	@JsonIgnore
	@OneToMany(mappedBy = "posts", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PostReact> postReact;

	@JsonIgnore
	@OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<PostMention> postMentioned;

	@JsonIgnore
	@OneToMany(mappedBy = "posts", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PostComment> postComment;

	@JsonIgnore
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<UserSavedPost> userSavePost;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "districtId")
	private District district;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "eventId")
	private Events events;

	public Long getId() {
		return postId; // Map 'id' to 'postId'
	}

//	  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
//	    private List<PostHashtag> postHashtags = new ArrayList<>();

	@ManyToMany
	@JoinTable(name = "post_hashtags", // The join table
			joinColumns = @JoinColumn(name = "post_id"), // Column in the join table for Post ID
			inverseJoinColumns = @JoinColumn(name = "hashtag_id") // Column in the join table for Hashtag ID
	)
	@JsonIgnore // Prevent recursion in Post -> Hashtag -> Post chain
	private List<Hashtag> hashtags;

	@JsonIgnore
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Disputes> dispute;

}

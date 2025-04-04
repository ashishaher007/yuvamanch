package com.ymanch.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
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
@Table(indexes = { @Index(name = "idx_storyId", columnList = "story_id"),
		@Index(name = "idx_storyCreatedAt", columnList = "story_created_at"),
		@Index(name = "idx_userId", columnList = "user_id") })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Story {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private long storyId;

	private String storyUrl;

	@Lob
	private String description;

	@Column(columnDefinition = "VARCHAR(255) DEFAULT 'storyType'")
	private String storyType;

	@Column(columnDefinition = "VARCHAR(255) DEFAULT 'videoThumbnailUrl'")
	private String videoThumbnailUrl;

	@CreationTimestamp
	@JsonIgnore
	private LocalDateTime storyCreatedAt;

	@UpdateTimestamp
	@JsonIgnore
	private LocalDateTime storyUpdatedAt;

	@JsonIgnore
	private Boolean isActive;

	@PrePersist
	@PreUpdate
	private void setDefaults() {
		if (storyUrl == null || storyUrl.isEmpty()) {
			storyUrl = "https://streesakti.s3.ap-south-1.amazonaws.com/posts/noStoryUrlImage.jpg";
		}
		if (description == null || description.isEmpty()) {
			description = "https://streesakti.s3.ap-south-1.amazonaws.com/posts/noStoryUrlImage.jpg";
		}
		if (videoThumbnailUrl == null || videoThumbnailUrl.isEmpty()) {
			videoThumbnailUrl = "emptyVideoThumbnailUrl";
		}
		if (storyUrl == null || storyUrl.isEmpty()) {
			storyUrl = "emptyStoryUrl";
		}
	}

	@JsonIgnore
	@OneToMany(mappedBy = "story", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<ViewStory> viewStory;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")
	@JsonIgnore
	private User user;

}

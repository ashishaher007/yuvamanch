package com.ymanch.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(indexes = {
		@Index(name = "idx_postId",columnList = "post_id"),
		@Index(name = "idx_userId",columnList = "user_id"),
		@Index(name = "idx_isPostReactDeleted",columnList = "is_post_react_deleted"),
		@Index(name = "idx_post_react_post_id_user_id",columnList = "post_id,user_id"),
		@Index(name = "idx_post_react_post_user",columnList = "post_id,user_id,is_post_react_deleted,post_react_id"),
		@Index(name = "idx_post_react_post_user_deleted",columnList = "post_id,user_id,is_post_react_deleted"),
				
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostReact {

	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long postReactId;

	@Pattern(regexp = "Like|Dislike|Angry|Funny|Happy|Love|Wow", message = "Post react name must be Like Or Dislike Or Angry Or Funny Or Happy Or Love Or Wow")
	private String postReactName;

	@NotEmpty(message = "Post react image url should not be empty")
	private String postReactImageUrl;

	@JsonIgnore
	private Boolean isPostReactDeleted;

	@JsonIgnore
	@CreationTimestamp
	private LocalDateTime postReactCreatedAt;

	@JsonIgnore
	@UpdateTimestamp
	private LocalDateTime postReactUpdatedAt;

	@PrePersist
	@PreUpdate
	private void setDefaults() {
		if (isPostReactDeleted == null) {
			isPostReactDeleted = false;
		}
	}

	@JsonIgnore
	@JoinColumn(name = "userId")
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	@JsonIgnore
	@JoinColumn(name = "postId")
	@ManyToOne(fetch = FetchType.LAZY)
	private Posts posts;

}

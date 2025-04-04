package com.ymanch.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(indexes = { @Index(name = "idx_userId", columnList = "user_id"),
		@Index(name = "idx_postId", columnList = "post_id"),@Index(name = "idx_user_saved_post", columnList = "post_id, user_id") })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSavedPost {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	
	private long userSavePostId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "postId")
	@JsonIgnore
	private Posts post;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")
	@JsonIgnore
	private User user;

}

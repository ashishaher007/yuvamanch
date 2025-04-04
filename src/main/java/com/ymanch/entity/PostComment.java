package com.ymanch.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(indexes = {
		@Index(name="idx_postId",columnList = "post_id"),
		@Index(name = "idx_userId",columnList = "user_id"),
		@Index(name = "idx_parent_comment_id",columnList = "parent_comment_id"),
		})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostComment {

	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long postCommentId;

	@NotEmpty(message = "Comment should not be empty")
	@Size(max = 500, message = "Comment must be less than 500 characters")
	private String comment;

	@JsonIgnore
	@CreationTimestamp
	private LocalDateTime commentCreatedAt;

	@JsonIgnore
	@UpdateTimestamp
	private LocalDateTime commentUpdateAt;

	@JsonIgnore
	@JoinColumn(name = "userId")
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	@JsonIgnore
	@JoinColumn(name = "postId")
	@ManyToOne(fetch = FetchType.LAZY)
	private Posts posts;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_comment_id")
	private PostComment parentComment;

	@JsonIgnore
	@OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PostComment> replies;

}

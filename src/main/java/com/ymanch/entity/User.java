

package com.ymanch.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(indexes = { @Index(name = "idx_users_user_id_district_role", columnList = "user_id,district_id,user_role"),
		@Index(name = "idx_users_userid_districtid", columnList = "user_id,district_id"),
		@Index(name = "idx_users_districtid", columnList = "district_id"),

})
@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long userId;

	private String userFirstName;

	private String userLastName;

	private String userEmail;

	private String userGender;

	private String userDateOfBirth;

	private String userAddress;

	private String userStatus;

	private String userpassword;

	private String userProfileImagePath;

	private String userRole;

	private String userCoverProfileImagePath;

	@Column(length = 512)
	private String fullName;

	@CreationTimestamp
	private LocalDateTime userCreatedAt;

	@UpdateTimestamp
	private LocalDateTime userUpdateAt;

	private String userMobileNumber;

	private boolean isTermsAndConditionsAccepted;

	@PrePersist
	@PreUpdate
	private void setDefaults() {
		if (userProfileImagePath == null || userProfileImagePath.isEmpty() || userProfileImagePath.equals("string")) {
			userProfileImagePath = "https://cdn.pixabay.com/photo/2020/07/01/12/58/icon-5359554_1280.png";
		}
		if (userCoverProfileImagePath == null || userCoverProfileImagePath.isEmpty()
				|| userCoverProfileImagePath.equals("string")) {
			userCoverProfileImagePath = "https://images.unsplash.com/photo-1528459584353-5297db1a9c01?q=80&w=1799&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D";
		}
		if (this.uuid == null) {
			this.uuid = UUID.randomUUID().toString(); // Ensure UUID for new records
		}
	}

	@JsonIgnore
	@Column(unique = true, nullable = false, updatable = false)
	private String uuid;

	public User() {
		if (this.uuid == null) {
			this.uuid = UUID.randomUUID().toString(); // Auto-generate for new records
		}
	}

	@JsonIgnore
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Posts> posts;

	@JsonIgnore
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PostReact> postReact;

	@JsonIgnore
	@OneToMany(mappedBy = "mentionedUser", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<PostMention> postMentioned;

	@JsonIgnore
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PostComment> postComment;

	@JsonIgnore
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<UserSavedPost> userSavePost;

	@JsonIgnore
	@OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Notification> senderNotify;

	@JsonIgnore
	@OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Notification> receiverNotify;

	@JsonIgnore
	@OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ChatMessage> senderChat;

	@JsonIgnore
	@OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ChatMessage> receiverChat;

	@JsonIgnore
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Story> story;

	@JsonIgnore
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ViewStory> viewStory;

	@JsonIgnore
	@OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<FriendRequest> senderFRequest;

	@JsonIgnore
	@OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<FriendRequest> receiverFRequest;

	@JsonIgnore
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Group> group;

	@JsonIgnore
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Pages> pages;

	@JsonIgnore
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Disputes> dispute;

	@JsonIgnore
	@ManyToMany(mappedBy = "members")
	private List<Group> memberGroups;

	@JsonIgnore
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Events> event;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "districtId")
	private District district;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "orgId")
	private Organisation org;

}

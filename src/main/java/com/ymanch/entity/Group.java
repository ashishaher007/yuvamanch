package com.ymanch.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.aspectj.weaver.tools.Trace;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "`groups`")
@Getter
@Setter
@AllArgsConstructor
public class Group {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long groupId;

	@NotEmpty(message = "Group name should not be empty")
	private String groupName;

	@NotEmpty(message = "Group description should not be empty")
	private String groupDescription;

	private String groupCoverProfileImagePath;

	@CreationTimestamp
	@JsonIgnore
	private LocalDateTime groupCreatedAt;

	@JsonIgnore
	@Column(unique = true, nullable = false)
	private String guuid;

	@PrePersist
	@PreUpdate
	private void setDefaults() {
		if (groupCoverProfileImagePath == null || groupCoverProfileImagePath.isEmpty()) {
			groupCoverProfileImagePath = "https://dev.strishakti.org/uploads/strishakti/posts/staticImageFolder/groups-default-cover-photo-2x.png";
		}
		if (this.guuid == null) {
			this.guuid = UUID.randomUUID().toString(); // Ensure UUID for new records
		}
	}

	public Group() {
		if (this.guuid == null) {
			this.guuid = UUID.randomUUID().toString();
		}
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "adminUserId")
	private User user; // The user who creates the group

	@JsonIgnore
	@ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
	@JoinTable(name = "group_members", joinColumns = @JoinColumn(name = "groupId"), inverseJoinColumns = @JoinColumn(name = "userId"))
	private List<User> members;

	@JsonIgnore
	@OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Posts> posts;

	
	@JsonIgnore
	@OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ChatMessage> groupChat;
}

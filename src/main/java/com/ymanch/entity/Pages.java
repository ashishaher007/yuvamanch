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
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(indexes = { @Index(name = "idx_pageName", columnList = "page_name") })
@Getter
@Setter
@AllArgsConstructor
public class Pages {
	@Id
	@JsonIgnore
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long pageId;

	@NotEmpty(message = "Page name should not be empty")
	private String pageName;

	@Lob
	@NotEmpty(message = "Page description should not be empty")
	private String pageDescription;

	private String pageCoverProfileImagePath;

	private String linkUrlName;

	private String linkUrl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "adminUserId")
	private User user; // The user who creates the page

	@JsonIgnore
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "page_members", joinColumns = @JoinColumn(name = "pageId"), inverseJoinColumns = @JoinColumn(name = "userId"))
	@org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
	private List<User> members;

	@CreationTimestamp
	@JsonIgnore
	private LocalDateTime pageCreatedAt;

	@UpdateTimestamp
	@JsonIgnore
	private LocalDateTime pageUpdatedAt;

	@PrePersist
	@PreUpdate
	private void setDefaults() {
		if (pageCoverProfileImagePath == null || pageCoverProfileImagePath.isEmpty()) {
			pageCoverProfileImagePath = "https://dev.strishakti.org/uploads/strishakti/posts/staticImageFolder/groups-default-cover-photo-2x.png";
		}
		if (this.puuid == null) {
			this.puuid = UUID.randomUUID().toString();
		}
	}

	@JsonIgnore
	@OneToMany(mappedBy = "pages", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Posts> posts;

	@JsonIgnore
	@Column(unique = true, nullable = false)
	private String puuid;

	public Pages() {
		if (this.puuid == null) {
			this.puuid = UUID.randomUUID().toString();
		}
	}

}

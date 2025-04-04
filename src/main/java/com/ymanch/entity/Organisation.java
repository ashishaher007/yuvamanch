package com.ymanch.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.ymanch.helper.Views;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(indexes = { @Index(name = "idx_orgName", columnList = "org_name") })
@Getter
@Setter
public class Organisation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView(Views.Internal.class)
	private long orgId;

	@JsonView(Views.Public.class)
	private String orgName;

	@JsonIgnore
	@CreationTimestamp
	private LocalDateTime orgCreatedAt;

	@JsonIgnore
	@UpdateTimestamp
	private LocalDateTime orgUpdatedAt;
}

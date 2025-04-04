package com.ymanch.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ymanch.helper.Enums.DisputeStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(indexes = { @Index(name = "idx_disputes", columnList = "post_id, raised_by_user_id") })
@Getter
@Setter
public class Disputes {

	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long disputeId;

	@NotEmpty(message = "Dispute type should not be empty")
	private String disputeType;

	@NotEmpty(message = "Dispute decription should not be empty")
	private String disputeDescription;

	@JsonIgnore
	@Enumerated(EnumType.STRING)
	private DisputeStatus disputeStatus;

	@JsonIgnore
	@CreationTimestamp
	private LocalDateTime disputeCreatedAt;

	@JsonIgnore
	@UpdateTimestamp
	private LocalDateTime disputeUpdatedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "postId")
	@JsonIgnore
	private Posts post;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "raisedByUserId")
	@JsonIgnore
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "disputeTitleId")
	@JsonIgnore
	private DisputeTitles disputeTitle;

}

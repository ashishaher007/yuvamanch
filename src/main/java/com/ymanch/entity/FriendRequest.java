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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(indexes = { @Index(name = "idx_sender_receiver", columnList = "sender_id, receiver_id"),
		@Index(name = "idx_receiver_sender", columnList = "receiver_id, sender_id"),
		@Index(name = "idx_receiver_sender_status", columnList = "receiver_id, sender_id, status"),
		@Index(name = "idx_sender_receiver_status", columnList = "sender_id, receiver_id, status") })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long userFriendRequestId;

	@JsonIgnore
	private String status;

	@JsonIgnore
	@CreationTimestamp
	private LocalDateTime friendRequestCreatedAt;

	@JsonIgnore
	@UpdateTimestamp
	private LocalDateTime friendRequestUpdateAt;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sender_id")
	private User sender;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receiver_id")
	private User receiver;
}

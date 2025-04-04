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
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "add_participant", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "participantUser_id", "eventId" }) }, indexes = {
				@Index(name = "idx_participant_event", columnList = "participant_user_id, event_id") })
@Getter
@Setter
public class AddParticipant {
	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "participantUser_id", nullable = false)
	private User participantUser;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "eventId", nullable = false)
	private Events event;

	@JsonIgnore
	@CreationTimestamp
	private LocalDateTime createdAt;

	@JsonIgnore
	@UpdateTimestamp
	private LocalDateTime updatedAt;
}

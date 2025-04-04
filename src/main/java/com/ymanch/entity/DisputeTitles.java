package com.ymanch.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
public class DisputeTitles {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private long disputeTitleId;

	@NotEmpty(message = "Dispute title should not be empty")
	private String disputeTitle;

	@CreationTimestamp
	@JsonIgnore
	private LocalDateTime disputeTitleCreatedAt;

	@UpdateTimestamp
	@JsonIgnore
	private LocalDateTime disputeTitleUpdatedAt;
	
}

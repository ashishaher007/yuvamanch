package com.ymanch.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonView;
import com.ymanch.helper.Views;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventCategory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView(Views.Internal.class)
	private long catId;

	@NotEmpty(message = "category name should not be empty")
	@JsonView(Views.Public.class)
	private String catName;

	@CreationTimestamp
	@JsonView(Views.Internal.class)
	private LocalDateTime catCreatedAt;

	@UpdateTimestamp
	@JsonView(Views.Internal.class)
	private LocalDateTime catUpdatedAt;

}

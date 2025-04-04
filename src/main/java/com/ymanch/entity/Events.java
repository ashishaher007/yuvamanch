package com.ymanch.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ymanch.helper.Enums.Status;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(indexes = { @Index(name = "idx_eventName", columnList = "event_name"),
		@Index(name = "idx_userId", columnList = "user_id"),
		@Index(name = "idx_districtId", columnList = "district_id"),
		@Index(name = "idx_endDate", columnList = "end_date"), 
		@Index(name = "idx_endTime", columnList = "end_time"),
		@Index(name = "idx_eventStatus", columnList = "event_status"),
		@Index(name = "idx_district_status", columnList = "district_id, event_status"),
		@Index(name = "idx_user_status", columnList = "user_id, event_status")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Events {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private Long eventId;

	@NotEmpty(message = "Event name should not be empty")
	private String eventName;

	private String eventDescription;

	@NotEmpty(message = "Event start date should not be empty")
	private String startDate;

	@NotEmpty(message = "Event start time should not be empty")
	private String startTime;

	@NotEmpty(message = "Event end date should not be empty")
	private String endDate;

	@NotEmpty(message = "Event start time should not be empty")
	private String endTime;

	private String eventAddress;

	@JsonIgnore
	private int totalInterest;

	@JsonIgnore
	@CreationTimestamp
	private LocalDateTime eventCreatedAt;

	@JsonIgnore
	@UpdateTimestamp
	private LocalDateTime eventUpdatedAt;

	@JsonIgnore
	@Enumerated(EnumType.STRING)
	private Status eventStatus;

	@Column(nullable = false, unique = true, updatable = false)
	private String uuid;

	private String eventMode;

	private String eventNotify;

	private String virtualEventLink;

	@PrePersist
	public void prePersist() {
		if (this.uuid == null) {
			this.uuid = UUID.randomUUID().toString();
		}
	}

	@JsonIgnore
	@OneToMany(mappedBy = "events", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Posts> posts;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")
	private User user;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "catId")
	private EventCategory eventCatg;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "districtId")
	private District district;
	
	public long getEventId() {
	    return eventId;
	}

	public void setEventId(long eventId) {
	    this.eventId = eventId;
	}

}

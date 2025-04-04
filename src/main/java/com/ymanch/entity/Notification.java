package com.ymanch.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ymanch.helper.Enums.NotificationStatus;
import com.ymanch.helper.Enums.NotificationType;

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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(indexes = { 
	    @Index(name = "idx_notification_type", columnList = "notification_type"),
	    
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long notificationId;

	private String notificationMessage;
	
	@Enumerated(EnumType.STRING)
	private NotificationType notificationType;

	@Enumerated(EnumType.STRING)
	private NotificationStatus notificationStatus;

	@CreationTimestamp
	private LocalDateTime notificationCreatedAt;

	@UpdateTimestamp
	private LocalDateTime notificationUpdatedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "senderId")
	@JsonIgnore
	private User sender;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receiverId")
	@JsonIgnore
	private User receiver;
}

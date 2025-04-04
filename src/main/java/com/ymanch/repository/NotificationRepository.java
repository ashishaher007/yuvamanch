package com.ymanch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ymanch.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

	List<Notification> findByReceiverUserIdOrderByNotificationIdDesc(Long receiverId);

	List<Notification> findByReceiverUserId(long receiverId);
	
	@Modifying
	@Query(value = "DELETE FROM notification WHERE notification_id = :notificationId", nativeQuery = true)
	void deleteNotification(@Param("notificationId") long notificationId);		
	
	@Query(value = "SELECT * FROM notification WHERE notification_type = 'BIRTHDAY'", nativeQuery = true)
	List<Notification> findByTypeBirthDay();

}

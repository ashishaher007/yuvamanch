package com.ymanch.dao;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ymanch.model.FriendRequestList;
import com.ymanch.model.NotificationModel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Repository
@Slf4j
public class NotificationDao {
	@PersistenceContext
	private EntityManager entitymanager;
	private StringBuilder query;
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

	@SuppressWarnings("unchecked")
	public List<NotificationModel> getNotificationDetails(long userId) throws Exception {
		log.info("***** Inside - NotificationDao - getNotificationDetails *****");
		query = new StringBuilder();
		ArrayList<NotificationModel> data = new ArrayList<>();
		try {
			query.append(
					"SELECT  n.notification_message, n.notification_status,   n.notification_created_at, count(n.notification_id) AS count_unread_notification FROM  notification n WHERE  n.receiver_id =:userId and n.notification_status='UNREAD' ORDER BY n.notification_id DESC;");
			Query createNativeQuery = entitymanager.createNativeQuery(query.toString());
			createNativeQuery.setParameter("userId", userId);
			createNativeQuery.getResultList().stream().forEach(obj -> {
				Object resultArray[] = (Object[]) obj;
				LocalDateTime userCreatedAt = LocalDateTime.parse(resultArray[2].toString(), formatter);
				data.add(NotificationModel.builder().notificationMessage(resultArray[0].toString())
						.notificationStatus(resultArray[1].toString()).notificationCreatedAt(userCreatedAt)
						.countUnreadNotification(Integer.valueOf(resultArray[3].toString())).build());
			});
		} catch (Exception e) {
			log.error("Error occurred while fetching admin user details: ", e);
			throw new Exception("Failed to fetch user details", e);
		}
		return data;
	}
}

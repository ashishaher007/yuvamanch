package com.ymanch.serviceimpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ymanch.entity.Notification;
import com.ymanch.entity.User;
import com.ymanch.exception.ResourceNotFoundException;
import com.ymanch.helper.CommonMessages;
import com.ymanch.helper.Enums.NotificationStatus;
import com.ymanch.helper.Enums.NotificationType;
import com.ymanch.repository.NotificationRepository;
import com.ymanch.repository.UserRepository;
import com.ymanch.service.NotifyService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NotificationServiceImpl implements NotifyService {
	private Map<Object, Object> response;

	@Autowired
	private CommonMessages MSG;

	@Autowired
	private NotificationRepository notificationRepo;

	@Autowired
	private UserRepository userRepo;

//	@Autowired
//	private NotificationDao notificationDao;

	@Transactional
	@Override
	public ResponseEntity<Object> updateNotificationStatus(long receiverId) {
		log.info("***** Inside - NotificationServiceImpl - getAllNotificationDetails *****");
		response = new HashMap<>();
		User userData = userRepo.findById(receiverId)
				.orElseThrow(() -> new ResourceNotFoundException("User with the Id " + receiverId + " not found"));
		List<Notification> data = notificationRepo.findByReceiverUserId(userData.getUserId());
		for (Notification notification : data) {
			notification.setNotificationStatus(NotificationStatus.READ);
		}
		notificationRepo.saveAll(data);
		response.put("status", MSG.SUCCESS);
		response.put("message", MSG.NOTIFICATION_STATUS_UPDATE);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Transactional
	@Override
	public ResponseEntity<Object> getAllNotifyDetails(long userId) throws Exception {
		log.info("***** Inside - NotificationServiceImpl - getAllNotificationDetails *****");
		response = new HashMap<>();
		User userData = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User with the Id " + userId + " not found"));
		try {
			List<Notification> notify = notificationRepo
					.findByReceiverUserIdOrderByNotificationIdDesc(userData.getUserId());
			int countUnreadNotification = (int) notify.stream()
					.filter(notification -> notification.getNotificationStatus() == NotificationStatus.UNREAD).count();
//		List<NotificationModel> notify = notificationDao.getNotificationDetails(userId);

			if (notify.isEmpty() || notify == null) {
				// response.put("status", MSG.FAILED);
				// response.put("message", MSG.NOTIFICATION_NOT_FOUND);
				// return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
				response.put("notificationDetails", notify);
				response.put("unreadNotificationCount", countUnreadNotification);
				return ResponseEntity.status(HttpStatus.OK).body(response);
			} else {
				response.put("notificationDetails", notify);
				response.put("unreadNotificationCount", countUnreadNotification);
				return ResponseEntity.status(HttpStatus.OK).body(response);
			}
		} catch (Exception e) {
			response.put("status", MSG.FAILED);
			response.put("message", "Somwthing went wrong");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}

	}

}

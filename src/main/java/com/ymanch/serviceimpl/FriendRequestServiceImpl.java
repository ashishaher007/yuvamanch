package com.ymanch.serviceimpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.ymanch.dao.FriendRequestServiceDao;
import com.ymanch.entity.FriendRequest;
import com.ymanch.entity.Notification;
import com.ymanch.entity.User;
import com.ymanch.exception.ResourceNotFoundException;
import com.ymanch.helper.CommonMessages;
import com.ymanch.helper.Enums.NotificationStatus;
import com.ymanch.model.FriendRequestList;
import com.ymanch.model.FriendsList;
import com.ymanch.model.UserSearchModel;
import com.ymanch.repository.FriendRequestRepository;
import com.ymanch.repository.NotificationRepository;
import com.ymanch.repository.UserRepository;
import com.ymanch.service.FriendRequestService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FriendRequestServiceImpl implements FriendRequestService {
	private Map<Object, Object> response;

	private CommonMessages MSG;

	private FriendRequestServiceDao friendRequestServiceDao;

	private UserRepository userRepo;

	private FriendRequestRepository friendRequestRepo;

	private NotificationRepository notificationRepo;

	private RedisTemplate<String, Object> redisTemplate;

	private ModelMapper modelMapper;

	public FriendRequestServiceImpl(CommonMessages MSG, FriendRequestServiceDao friendRequestServiceDao,
			UserRepository userRepo, FriendRequestRepository friendRequestRepo, NotificationRepository notificationRepo,
			RedisTemplate<String, Object> redisTemplate, ModelMapper modelMapper) {
		this.MSG = MSG;
		this.friendRequestServiceDao = friendRequestServiceDao;
		this.userRepo = userRepo;
		this.friendRequestRepo = friendRequestRepo;
		this.notificationRepo = notificationRepo;
		this.redisTemplate = redisTemplate;
		this.modelMapper = modelMapper;
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	@Override
	public ResponseEntity<Object> sendRequest(long senderId, long receiverId) {
		log.info("***** Inside FriendRequestServiceImpl - sendRequest *****");
		response = new HashMap<>();
		User senderData = userRepo.findById(senderId)
				.orElseThrow(() -> new ResourceNotFoundException("Sender user with the Id " + senderId + " not found"));
		User receiverData = userRepo.findById(receiverId)
				.orElseThrow(() -> new ResourceNotFoundException("Receiver with the Id " + receiverId + " not found"));
		// check if a friend request already exist
		Optional<FriendRequest> requestExists = friendRequestRepo.findBidirectionalRequest(senderData, receiverData);
		if (senderId == receiverId) {
			response.put("status", CommonMessages.FAILED);
			response.put("message", MSG.FREQUEST_SENT_TO_OWN);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		} else if (requestExists.isPresent()) {
			if (requestExists.get().getStatus().equals("APPROVED")
					&& requestExists.get().getStatus().equals("PENDING")) {
				response.put("status", CommonMessages.FAILED);
				response.put("message", MSG.FREQUEST_ALREADY_FRIEND);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
			response.put("status", CommonMessages.FAILED);
			response.put("message", MSG.FREQUEST_ALREADY_SENT);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

		FriendRequest fr = new FriendRequest();
		fr.setSender(senderData);
		fr.setReceiver(receiverData);
		fr.setStatus("PENDING");
		friendRequestRepo.save(fr);

		// Remove cache for both users
		redisTemplate.delete("friendsList:" + senderData.getUserId());
		redisTemplate.delete("friendsList:" + receiverData.getUserId());
		// add in notification
		Notification notify = new Notification();
		notify.setNotificationMessage(
				MSG.NOTIFICATION_SEND_FREQUEST + senderData.getUserFirstName() + " " + senderData.getUserLastName());
		notify.setNotificationStatus(NotificationStatus.UNREAD);
		notify.setSender(senderData);
		notify.setReceiver(receiverData);
		notificationRepo.save(notify);

		response.put("status", CommonMessages.SUCCESS);
		response.put("message", MSG.FREQUEST_SENT_SUCCESSFUL);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@Transactional
	@Override
	public ResponseEntity<Object> approveRequest(long friendRequestId) {
		log.info("***** Inside FriendRequestServiceImpl - approveRequest *****");
		response = new HashMap<>();
		Optional<FriendRequest> requestData = friendRequestRepo.findById(friendRequestId);
		if (requestData.isEmpty()) {
			response.put("status", CommonMessages.FAILED);
			response.put("message", MSG.FREQUEST_ID_NOT_FOUND);
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		} else if (requestData.get().getStatus().equals("APPROVED")) {
			response.put("status", CommonMessages.FAILED);
			response.put("message", MSG.FREQUEST_ALREADY_FRIEND);
			return new ResponseEntity<>(response, HttpStatus.CONFLICT);
		} else {
			requestData.get().setStatus("APPROVED");
			friendRequestRepo.save(requestData.get());

			// add in notification
			Notification notify = new Notification();
			notify.setNotificationMessage(requestData.get().getReceiver().getUserFirstName() + " "
					+ requestData.get().getReceiver().getUserLastName() + MSG.NOTIFICATION_ACCEPT_FREQUEST);
			notify.setNotificationStatus(NotificationStatus.UNREAD);
			notify.setSender(requestData.get().getReceiver());
			notify.setReceiver(requestData.get().getSender());
			notificationRepo.save(notify);

			// Remove cache for both users
			redisTemplate.delete("friendsList:" + requestData.get().getSender().getUserId());
			redisTemplate.delete("friendsList:" + requestData.get().getReceiver().getUserId());

			response.put("status", CommonMessages.SUCCESS);
			response.put("message", MSG.FREQUEST_APPROVED);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@Transactional
	@Override
	public ResponseEntity<Object> rejectRequest(long friendRequestId) {
		log.info("***** Inside FriendRequestServiceImpl - approveRequest *****");
		response = new HashMap<>();
		Optional<FriendRequest> requestData = friendRequestRepo.findById(friendRequestId);
		if (requestData.isEmpty()) {
			response.put("status", CommonMessages.FAILED);
			response.put("message", MSG.FREQUEST_ID_NOT_FOUND);
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		} else {
			friendRequestRepo.deleteById(friendRequestId);
			// Remove cache for both users
			redisTemplate.delete("friendsList:" + requestData.get().getSender().getUserId());
			redisTemplate.delete("friendsList:" + requestData.get().getReceiver().getUserId());
			response.put("status", CommonMessages.SUCCESS);
			response.put("message", MSG.FREQUEST_REJECT);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@Transactional
	@Override
	public ResponseEntity<Object> getAllFriendRequest(long receiverId) {
		log.info("***** Inside FriendRequestServiceImpl - getAllFriendRequest *****");
		response = new HashMap<>();
		List<FriendRequestList> requestList = friendRequestServiceDao.getRequestList(receiverId);
		if (requestList.isEmpty()) {
			// response.put("status", MSG.FAILED);
			// response.put("message", MSG.FREQUEST_NOT_FOUND);
			response.put("friendRequestData", requestList);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			response.put("friendRequestData", requestList);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}

	}

	@Transactional
	@Override
	public ResponseEntity<Object> getAllFriendsList(long userId) {
		log.info("***** Inside FriendRequestServiceImpl - getAllFriendsList *****");
		Map<Object, Object> response = new HashMap<>();
		// Create a Redis cache key specific to the user's friends list
		String redisKey = "friendsList:" + userId;

		// Check if the data is already in cache
		Object cachedData = redisTemplate.opsForValue().get(redisKey);
		if (cachedData != null) {
			log.info("Returning cached friends list for userId: {}", userId);
			return new ResponseEntity<>(cachedData, HttpStatus.OK); // Return cached data if available
		}
		List<FriendsList> friendList = fetchFriendsList(userId);
		long countFriend = friendRequestRepo.countApprovedFriends(userId);
		if (friendList.isEmpty()) {
			response.put("status", CommonMessages.FAILED);
			response.put("message", MSG.FREQUEST_FRIEND_NOT_FOUND);
			response.put("friendListData", friendList);
			response.put("totalFriend", countFriend);
		} else {
			response.put("friendListData", friendList);
			response.put("totalFriend", countFriend);
		}
		// Cache the response for 10 minutes
		redisTemplate.opsForValue().set(redisKey, response, 1, TimeUnit.DAYS);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// this function is used by more than one

	@Transactional
	public List<FriendsList> fetchFriendsList(long userId) {
		log.info("***** Inside FriendRequestServiceImpl - fetchFriendsList *****");
		return friendRequestServiceDao.getFriendsList(userId);
	}

	@Transactional
	@Override
	public ResponseEntity<Object> rejectRequest(long currentUserId, long targetUserId) {
		log.info("***** Inside FriendRequestServiceImpl - rejectRequest-v1 *****");
		response = new HashMap<>();
		Optional<FriendRequest> requestData = friendRequestRepo
				.findBySenderUserIdAndReceiverUserIdOrSenderUserIdAndReceiverUserId(currentUserId, targetUserId,
						targetUserId, currentUserId);
		if (requestData.isEmpty()) {
			response.put("status", CommonMessages.FAILED);
			response.put("message", MSG.FREQUEST_ID_NOT_FOUND);
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		} else {
			friendRequestRepo.delete(requestData.get());
			response.put("status", CommonMessages.SUCCESS);
			response.put("message", MSG.FREQUEST_REJECT);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@Override
	public ResponseEntity<Object> findUserByName(Long userId, String userName) {
		if (log.isInfoEnabled()) {
			log.info("***** Inside FriendRequestServiceImpl - findUserByName *****");
		}
		Map<Object, Object> response = new HashMap<>();

		// Split the userName to check if it contains both first and last names
		String[] nameParts = userName.split(" ");
		List<User> userData;

		if (nameParts.length > 1) {
			// Handle full name (e.g., "komal kumari")
			String firstName = nameParts[0];
			String lastName = nameParts[1];

			// Find users matching both first and last name together
			userData = userRepo.findByUserFirstNameOrUserLastName(userId, firstName);
System.out.println(userData);
			// Now filter the userData further to check if lastName is also a match for any
			// users
			userData = userData.stream()
					.filter(user -> user.getUserLastName().toLowerCase().contains(lastName.toLowerCase()))
					.collect(Collectors.toList());
		} else {
			// Handle single name (e.g., "komal")
			userData = userRepo.findByUserFirstNameOrUserLastName(userId, userName);
		}

		if (userData == null || userData.isEmpty()) {
			response.put("status", CommonMessages.FAILED);
			response.put("message", MSG.USER_SEARCH_USER_NOT_FOUND);
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		} else {
			// Convert entity to model to show specific fields
			List<UserSearchModel> userModelData = userData.stream().map(user -> {
				UserSearchModel userModel = modelMapper.map(user, UserSearchModel.class);
				userModel.setUserFirstName(user.getUserFirstName());
				userModel.setUserLastName(user.getUserLastName());
				userModel.setUserProfileImagePath(user.getUserProfileImagePath());
				userModel.setUserUUID(user.getUuid());
				return userModel;
			}).collect(Collectors.toList());

			response.put("searchedData", userModelData);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

}

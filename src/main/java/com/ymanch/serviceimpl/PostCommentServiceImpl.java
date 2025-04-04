package com.ymanch.serviceimpl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ymanch.dao.HomeServiceDao;
import com.ymanch.entity.Notification;
import com.ymanch.entity.PostComment;
import com.ymanch.entity.Posts;
import com.ymanch.entity.User;
import com.ymanch.exception.ResourceNotFoundException;
import com.ymanch.helper.CommonMessages;
import com.ymanch.helper.Enums.NotificationStatus;
import com.ymanch.model.PostIndex;
import com.ymanch.model.PostIndexComments;
import com.ymanch.repository.NotificationRepository;
import com.ymanch.repository.PostCommentRepository;
import com.ymanch.repository.PostRepository;
import com.ymanch.repository.UserRepository;
import com.ymanch.service.PostCommentService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PostCommentServiceImpl implements PostCommentService {
	private Map<Object, Object> response;

	private CommonMessages MSG;

	private UserRepository userRepo;

	private PostRepository postRepo;

	private PostCommentRepository postCommentRepo;

	private HomeServiceDao homeServiceDao;

	private NotificationRepository notificationRepo;

	private SimpMessageSendingOperations messagingOperations;

	public PostCommentServiceImpl(CommonMessages mSG, UserRepository userRepo, PostRepository postRepo,
			PostCommentRepository postCommentRepo, HomeServiceDao homeServiceDao,
			NotificationRepository notificationRepo, SimpMessageSendingOperations messagingOperations) {
		super();
		MSG = mSG;
		this.userRepo = userRepo;
		this.postRepo = postRepo;
		this.postCommentRepo = postCommentRepo;
		this.homeServiceDao = homeServiceDao;
		this.notificationRepo = notificationRepo;
		this.messagingOperations = messagingOperations;
	}

	@Transactional
	@Override
	public ResponseEntity<Object> storeComment(long userId, long postId, PostComment comment)
			throws ResourceNotFoundException {
		log.info("***** Inside - PostCommentServiceImpl - storeComment *****");
		response = new HashMap<>();
		User userData = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User with the Id " + userId + " not found"));
		Posts postData = postRepo.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post with the Id" + postId + " not found"));
		comment.setUser(userData);
		comment.setPosts(postData);
		PostComment data = postCommentRepo.save(comment);

		// Prepare notification only if the commenter's user ID does not match the post
		// owner's user ID
		if (userId != postData.getUser().getUserId()) {
			// add in notification
			Notification notify = new Notification();
			notify.setNotificationMessage(userData.getUserFirstName() + " " + userData.getUserLastName() + ""
					+ MSG.NOTIFICATION_ADD_COMMENT_POST);
			notify.setNotificationStatus(NotificationStatus.UNREAD);
			notify.setSender(userData);
			notify.setReceiver(postData.getUser());
			notificationRepo.save(notify);
			// Broadcast to a specific user (could be the post author, etc.)
//			long receiverId = postData.getUser().getUserId(); // You can fetch this dynamically based on your logic
//			messagingOperations.convertAndSend("/queue/notifications/" + receiverId,
//					"New comment: " + comment.getComment());
		}

		PostIndexComments pi = new PostIndexComments();
		pi.setUserProfileImageUrl(data.getUser().getUserProfileImagePath());
		pi.setCommentTime(data.getCommentCreatedAt().toString());
		pi.setComment(data.getComment());
		pi.setUserName(data.getUser().getUserFirstName() + " ".concat(data.getUser().getUserLastName()));
		pi.setParentCommentId(data.getPostCommentId());
		pi.setChildCommentId(0);
		pi.setChildren(null);
		response.put("status", CommonMessages.SUCCESS);
		response.put("commentsAndReacts", pi);
		response.put("message", MSG.POST_COMMENT_ADD_SUCCESSFUL);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@Transactional
	@Override
	public ResponseEntity<Object> storeCommentOnComment(long userId, long postId, long commentId, PostComment comment)
			throws ResourceNotFoundException {
		log.info("***** Inside - PostCommentServiceImpl - storeComment *****");
		response = new HashMap<>();
		User userData = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User with the Id " + userId + " not found"));
		Posts postData = postRepo.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post with the Id" + postId + " not found"));
		PostComment postCommentData = postCommentRepo.findById(commentId)
				.orElseThrow(() -> new ResourceNotFoundException(("Comment with the Id " + commentId + " not found")));
		comment.setUser(userData);
		comment.setPosts(postData);
		comment.setParentComment(postCommentData);
		postCommentRepo.save(comment);
		response.put("status", CommonMessages.SUCCESS);
		response.put("message", MSG.POST_COMMENT_ON_COMMENT_ADD_SUCCESSFUL);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@Transactional
	@Override
	public ResponseEntity<Object> removeParentComment(long userId, long postCommentId)
			throws ResourceNotFoundException {
		log.info("***** Inside - PostCommentServiceImpl - storeComment *****");
		response = new HashMap<>();
		PostComment postCommentData = postCommentRepo.findById(postCommentId).orElseThrow(
				() -> new ResourceNotFoundException("Comment with the Id " + postCommentId + " not found"));
		if (postCommentData.getUser().getUserId() != userId) {
			response.put("status", MSG.FAILED);
			response.put("message", MSG.POST_UNAUTHORIZED_DELETE_FAILED);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		} else {
			postCommentRepo.delete(postCommentData);
			response.put("status", CommonMessages.SUCCESS);
			response.put("message", MSG.POST_COMMENT_DELETE_SUCCESSFUL);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@Transactional
	@Override
	public ResponseEntity<Object> getAllCommentsAndReact(long postId) {
		log.info("***** Inside - storeComment - getAllHomePageData *****");
		response = new HashMap<>();
		postRepo.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post with the Id" + postId + " not found"));
		response.put("commentsAndReacts", homeServiceDao.getPostIndexDataComments(postId));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}

package com.ymanch.serviceimpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ymanch.config.ActiveSessionTracker;
import com.ymanch.entity.ChatMessage;
import com.ymanch.entity.User;
import com.ymanch.exception.ResourceNotFoundException;
import com.ymanch.helper.DateTimeUtil;
import com.ymanch.mapper.ChatMessageForwardDTO;
import com.ymanch.model.CustomResponseModel;
import com.ymanch.model.GroupChatModel;
import com.ymanch.repository.ChatMessageRepository;
import com.ymanch.repository.UserRepository;
import com.ymanch.service.ChatMessageService;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {

	private UserRepository userRepo;
	private ChatMessageRepository chatMessageRepository;
	private SimpMessageSendingOperations messagingOperations;

	public ChatMessageServiceImpl(UserRepository userRepo, ChatMessageRepository chatMessageRepository,
			SimpMessageSendingOperations messagingOperations) {
		super();
		this.userRepo = userRepo;
		this.chatMessageRepository = chatMessageRepository;
		this.messagingOperations = messagingOperations;

	}

	@Transactional
	@Override
	public ChatMessage saveMessage(ChatMessage message) {
		User sender = userRepo.findById(message.getSender().getUserId()).orElse(null);
		User receiver = userRepo.findById(message.getReceiver().getUserId()).orElse(null);
		message.setSender(sender);
		message.setReceiver(receiver);
		return chatMessageRepository.save(message);
	}

//	@Override
//	public List<ChatMessage> getMessagesBetweenUsers(User sender, User receiver) {
//		return chatMessageRepository.findBySenderAndReceiver(sender, receiver);
//	}
	@Transactional
	@Override
	public List<ChatMessage> getMessagesForUser(User receiver) {
		return chatMessageRepository.findByReceiver(receiver);
	}

	@Transactional
	@Override
	public List<ChatMessage> findChatHistory(Long senderId, Long receiverId) {
//		User sender = userRepo.findById(senderId).orElse(null);
//		User receiver = userRepo.findById(receiverId).orElse(null);
		return chatMessageRepository.findBySenderAndReceiver(senderId, receiverId);
	}

	@Transactional
	@Override
	public ChatMessage saveGroupMessage(ChatMessage message) {
		User sender = userRepo.findById(message.getSender().getUserId()).orElse(null);
		message.setSender(sender);
		// Group messages do not have a receiver, so make sure the receiver is set to
		// null
		message.setReceiver(null);
		return chatMessageRepository.save(message);
	}

	@Transactional
	@Override
	public ResponseEntity<Object> findGroupChatHistory(long groupId, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<GroupChatModel> data = chatMessageRepository.findMessagesInGroup(groupId, pageable);
		// reverse the content order ot ensure the latest message appears at the top
		List<GroupChatModel> reversedOrder = new ArrayList<>(data.getContent());
		Collections.reverse(reversedOrder);

		// Now, set the time
		reversedOrder.forEach(message -> {
			String timeAgo = DateTimeUtil.convertToTimeAgo(message.getLastMessageTimestamp());
			message.setLastMessageTime(timeAgo);
		});
		CustomResponseModel<GroupChatModel> customResponse = new CustomResponseModel<>(reversedOrder,
				data.getTotalPages(), data.getNumber(), data.getTotalElements(), data.getSize(), data.hasNext(),
				data.getNumber() + 1);
		return new ResponseEntity<>(customResponse, HttpStatus.OK);
	}

	@Transactional
	@Override
	public void forwardMessageToMultipleUsers(ChatMessageForwardDTO messageForwardDTO) {
		System.out.println("Method called with sender ID: " + messageForwardDTO.getSenderId());
		System.out.println("Receiver IDs: " + messageForwardDTO.getReceiverIds());
		System.out.println("Message Content: " + messageForwardDTO.getContent());

		User sender = userRepo.findById(messageForwardDTO.getSenderId())
				.orElseThrow(() -> new ResourceNotFoundException("Sender not found"));

		List<ChatMessage> messagesToNotifySender = new ArrayList<>();
		for (Long receiverId : messageForwardDTO.getReceiverIds()) {
			User receiver = userRepo.findById(receiverId)
					.orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

			ChatMessage newMessage = new ChatMessage();
			newMessage.setSender(sender);
			newMessage.setReceiver(receiver);
			newMessage.setContent(messageForwardDTO.getContent());
			newMessage.setTimestamp(LocalDateTime.now());

			chatMessageRepository.save(newMessage);
			messagesToNotifySender.add(newMessage);

			// Notify the receiver
			String receiverUserId = String.valueOf(receiverId);
			messagingOperations.convertAndSendToUser(receiverUserId, "/queue/messages", newMessage);

			System.out.println("Message sent to receiver ID: " + receiverId);
		}

		messagingOperations.convertAndSendToUser(String.valueOf(sender.getUserId()), "/queue/messages",
				messagesToNotifySender);
		// Notify any active participants (e.g., User A)
		notifyActiveParticipants(sender, messagesToNotifySender);
	}

	private void notifyActiveParticipants(User sender, List<ChatMessage> messages) {
		// Example: Fetch connected user IDs from an active session tracker
		CopyOnWriteArraySet<String> activeSessions = ActiveSessionTracker.getSessions(sender.getUserId());

		for (String sessionId : activeSessions) {
			// Notify each session
			messagingOperations.convertAndSendToUser(sessionId, "/queue/messages", messages);
		}

	}
	

    @Transactional
    @Override
    public void deleteMessage(Long messageId) {
        // Find the message by its ID
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        // Hard delete: Remove the message from the database
        chatMessageRepository.delete(message);
    }

}

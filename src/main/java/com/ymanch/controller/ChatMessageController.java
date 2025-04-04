package com.ymanch.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ymanch.entity.ChatMessage;
import com.ymanch.entity.Group;
import com.ymanch.entity.User;
import com.ymanch.exception.ResourceNotFoundException;
import com.ymanch.helper.DateTimeUtil;
import com.ymanch.mapper.ChatMessageForwardDTO;
import com.ymanch.model.FriendsList;
import com.ymanch.model.GroupChatModel;
import com.ymanch.model.GroupChatModel1;
import com.ymanch.repository.GroupRepository;
import com.ymanch.repository.UserRepository;
import com.ymanch.service.ChatMessageService;
import com.ymanch.service.FriendRequestService;
import com.ymanch.serviceimpl.FriendRequestServiceImpl;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ymanch/users/chatmessage")
@Slf4j
public class ChatMessageController {

	private ChatMessageService chatMessageService;
	private SimpMessageSendingOperations messagingOperations;
	private GroupRepository groupRepo;
	private UserRepository userRepo;
	private FriendRequestServiceImpl friendRequestService;

	public ChatMessageController(ChatMessageService chatMessageService,
			SimpMessageSendingOperations messagingOperations, GroupRepository groupRepo, UserRepository userRepo,FriendRequestServiceImpl friendRequestService) {
		super();
		this.chatMessageService = chatMessageService;
		this.messagingOperations = messagingOperations;
		this.groupRepo = groupRepo;
		this.userRepo = userRepo;
		this.friendRequestService = friendRequestService;
	}

	@MessageMapping("/chat")
	public void handleChatMessage(ChatMessage message) {
		// Extract the group and sender information
		Group group = message.getGroup();
		User sender = message.getSender();
		if (group != null) { // Group message processing
			processGroupMessage(group, sender, message);
		} else { // Private message processing
			processPrivateMessage(sender, message);
		    // Send friend list update after a private message
	        updateFriendListAfterMessage(sender, message);
		}
	}

private void updateFriendListAfterMessage(User sender, ChatMessage message) {
//	 // This method should fetch the sender's and receiver's friend list and send an update via WebSocket
//    List<FriendsList> friendsList = friendRequestService.fetchFriendsList(sender.getUserId());
//   
//    // Here, sending the updated friend list to the sender
//    messagingOperations.convertAndSendToUser(String.valueOf(sender.getUserId()), "/queue/friendList", friendsList);
//    
//    // Optionally, you can also send the updated list to the receiver
//    messagingOperations.convertAndSendToUser(String.valueOf(message.getReceiver().getUserId()), "/queue/friendList", friendsList);
	   // Fetch and send updated friend list to the sender
    List<FriendsList> senderFriendList = friendRequestService.fetchFriendsList(sender.getUserId());
    messagingOperations.convertAndSendToUser(String.valueOf(sender.getUserId()), "/queue/friendList", senderFriendList);

    // Fetch and send updated friend list to the receiver
    List<FriendsList> receiverFriendList = friendRequestService.fetchFriendsList(message.getReceiver().getUserId());
    messagingOperations.convertAndSendToUser(String.valueOf(message.getReceiver().getUserId()), "/queue/friendList", receiverFriendList);
	}

//	final Group finalGroup = group;
	private void processGroupMessage(Group group, User sender, ChatMessage message) {
		// Fetch group and sender in one go
		group = groupRepo.findById(group.getGroupId())
				.orElseThrow(() -> new ResourceNotFoundException("Group not found"));

		sender = userRepo.findById(sender.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException("Sender not found"));
		// Validate if the sender is a member or admin of the group
		if (!isValidGroupMemberOrAdmin(group, sender)) {
			throw new ResourceNotFoundException("User is not a member or admin of the group");
		}
		// Generate current timestamp as LocalDateTime
		LocalDateTime currentTimestamp = LocalDateTime.now(); // Current time

		// Create a GroupChatModel for the group chat message
		GroupChatModel1 groupChatModel = new GroupChatModel1(group.getGroupId(), group.getGroupName(),
				message.getContent(), currentTimestamp, sender.getUserId(),
				sender.getUserFirstName() + " " + sender.getUserLastName(), "Just Now");
		// Save the message asynchronously to reduce latency
		CompletableFuture.runAsync(() -> chatMessageService.saveGroupMessage(message));
		final Group finalGroup = group;
		// Notify all group members
		group.getMembers().forEach(member -> {
			messagingOperations.convertAndSend("/topic/group/" + finalGroup.getGroupId(), groupChatModel);
		});
	}

	private void processPrivateMessage(User sender, ChatMessage message) {
		// Save the private message
		ChatMessage savedMessage = chatMessageService.saveMessage(message);

		// Notify the receiver and sender
		String receiverUserId = String.valueOf(message.getReceiver().getUserId());
		String senderUserId = String.valueOf(sender.getUserId());

		messagingOperations.convertAndSendToUser(receiverUserId, "/queue/messages", savedMessage);
		messagingOperations.convertAndSendToUser(senderUserId, "/queue/messages", savedMessage);
	}

	private boolean isValidGroupMemberOrAdmin(Group group, User sender) {
		// Check if the sender is either an admin or a member of the group
		return group.getUser().getUserId() == sender.getUserId()
				|| group.getMembers().stream().anyMatch(member -> member.getUserId() == sender.getUserId());
	}

	@GetMapping("/history/{senderId}/{receiverId}")
	public List<ChatMessage> getChatHistory(@PathVariable Long senderId, @PathVariable Long receiverId) {
		return chatMessageService.findChatHistory(senderId, receiverId);
	}

	@GetMapping("/history/group/{groupId}")
	public ResponseEntity<Object> getGroupChatHistory(@PathVariable long groupId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
		return chatMessageService.findGroupChatHistory(groupId, page, size);
	}

	@MessageMapping("/forwardMessage")
	public void forwardMessage(ChatMessageForwardDTO forwardDTO) {
//		if (messageForwardDTO.getReceiverIds() != null && !messageForwardDTO.getReceiverIds().isEmpty()) {
//			// Forward to multiple users
//			chatMessageService.forwardMessageToMultipleUsers(messageForwardDTO);
//		}
		List<Long> receiverIds = forwardDTO.getReceiverIds();

		for (Long receiverId : receiverIds) {
			// Create a forwarded message
			ChatMessage forwardedMessage = new ChatMessage();
			forwardedMessage.setSender(userRepo.findById(forwardDTO.getSenderId()).orElseThrow());
			forwardedMessage.setReceiver(userRepo.findById(receiverId).orElseThrow());
			forwardedMessage.setContent(forwardDTO.getContent());
			forwardedMessage.setTimestamp(LocalDateTime.now());

			// Save the forwarded message to the database
			chatMessageService.saveMessage(forwardedMessage);

			// Send the message to the recipient (user A)
			messagingOperations.convertAndSendToUser(String.valueOf(receiverId), "/queue/messages", forwardedMessage);

			// Send the message to the sender (user B) if needed
//			messagingOperations.convertAndSendToUser(String.valueOf(forwardDTO.getSenderId()), "/queue/messages",
//					forwardedMessage);
		}
	}

	@DeleteMapping("/delete/chatmessage/{messageId}")
	public ResponseEntity<String> deleteMessage(@PathVariable Long messageId) {
		chatMessageService.deleteMessage(messageId);
		return ResponseEntity.ok("Message deleted successfully");
	}
	
	

}

//@PostMapping("/chat")
//public ChatMessage sendMessage(@RequestBody ChatMessage message) {
//	log.info("Received HTTP message: " + message);
//	ChatMessage savedMessage = chatMessageService.saveMessage(message);
//
//	String receiverUserId = String.valueOf(message.getReceiver().getUserId());
//	log.info("Sending message to user: " + receiverUserId);
//
//	messagingOperations.convertAndSendToUser(receiverUserId, "/queue/messages", savedMessage);
//	return savedMessage;
//}

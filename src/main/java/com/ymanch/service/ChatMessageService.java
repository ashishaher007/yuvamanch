package com.ymanch.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.ymanch.entity.ChatMessage;
import com.ymanch.entity.User;
import com.ymanch.mapper.ChatMessageForwardDTO;

public interface ChatMessageService {
	ChatMessage saveMessage(ChatMessage message);

//	List<ChatMessage> getMessagesBetweenUsers(User sender, User receiver);

	List<ChatMessage> getMessagesForUser(User receiver);

	List<ChatMessage> findChatHistory(Long senderId, Long receiverId);

	ChatMessage saveGroupMessage(ChatMessage message);

	ResponseEntity<Object> findGroupChatHistory(long groupId, int page, int size);

	void forwardMessageToMultipleUsers(ChatMessageForwardDTO messageForwardDTO);

	void deleteMessage(Long messageId);
}

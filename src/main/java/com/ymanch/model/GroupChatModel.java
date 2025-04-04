package com.ymanch.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupChatModel {
	private long chatId;
	private Long groupId;
	private String groupName;
	private String lastMessageContent;
	private LocalDateTime lastMessageTimestamp;
	private Long lastMessageSenderId;
	private String lastMessageSenderName;
	private String lastMessageTime;

	public GroupChatModel(long chatId,Long groupId, String groupName, String lastMessageContent, LocalDateTime lastMessageTimestamp,
			Long lastMessageSenderId, String lastMessageSenderName) {
		super();
		this.chatId = chatId;
		this.groupId = groupId;
		this.groupName = groupName;
		this.lastMessageContent = lastMessageContent;
		this.lastMessageTimestamp = lastMessageTimestamp;
		this.lastMessageSenderId = lastMessageSenderId;
		this.lastMessageSenderName = lastMessageSenderName;
	}

}

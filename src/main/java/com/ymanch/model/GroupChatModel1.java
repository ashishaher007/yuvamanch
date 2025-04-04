package com.ymanch.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupChatModel1 {
	private Long groupId;
	private String groupName;
	private String lastMessageContent;
	private LocalDateTime lastMessageTimestamp;
	private Long lastMessageSenderId;
	private String lastMessageSenderName;
	private String lastMessageTime;

	public GroupChatModel1(Long groupId, String groupName, String lastMessageContent,
			LocalDateTime lastMessageTimestamp, Long lastMessageSenderId, String lastMessageSenderName,
			String lastMessageTime) {
		super();
		this.groupId = groupId;
		this.groupName = groupName;
		this.lastMessageContent = lastMessageContent;
		this.lastMessageTimestamp = lastMessageTimestamp;
		this.lastMessageSenderId = lastMessageSenderId;
		this.lastMessageSenderName = lastMessageSenderName;
		this.lastMessageTime = lastMessageTime;
	}

}

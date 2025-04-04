package com.ymanch.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageModel {
	private long senderId;
	private long receiverId;
	private String message;
}

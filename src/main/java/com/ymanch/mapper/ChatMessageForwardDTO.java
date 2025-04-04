package com.ymanch.mapper;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChatMessageForwardDTO {
	@JsonProperty("sender")
	private Long senderId;
	private String content;
	private List<Long> receiverIds;
}

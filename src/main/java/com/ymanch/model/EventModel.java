package com.ymanch.model;

import org.springframework.web.multipart.MultipartFile;

import com.ymanch.helper.Enums.PostOwnerType;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventModel {

	private String eventName;

	private String eventDescription;

	private String startDate;

	private String startTime;

	private String endDate;

	private String endTime;

	private String eventAddress;

	private MultipartFile eventImageUrl;

	private String eventPostType;

	private String eventVideoThumbnailUrl;

	@Enumerated(EnumType.STRING)
	private PostOwnerType postOwnerType;

	private String eventMode;

	private String eventNotify;

	private String virtualEventLink;

}

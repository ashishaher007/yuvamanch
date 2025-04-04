package com.ymanch.model;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import com.ymanch.helper.Enums.PostOwnerType;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminPostAdvertisementModel {
	@NotEmpty(message = "Post name should not be empty")
	private String postName;

	private MultipartFile selectFile;

	private String postImageUrl;

	private String postType;

	private String videoThumbnailUrl;

	private String advertisementDescription;

	private LocalDateTime postCreatedAt;

	private long postId;

	private String postCategory;
}

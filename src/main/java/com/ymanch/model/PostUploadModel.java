package com.ymanch.model;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostUploadModel {
	private String postName;
	private String postType;
	private String videoThumbnailUrl;
	private MultipartFile postImage; // The uploaded image file

	private List<String> hashtag;
	private List<Long> mentionId;
	
	
}


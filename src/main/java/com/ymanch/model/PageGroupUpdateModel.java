package com.ymanch.model;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageGroupUpdateModel {
	private String name;
	private String description;
	private MultipartFile coverImage;
	private String linkUrlName;
	private String linkUrl;
}

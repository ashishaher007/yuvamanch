package com.ymanch.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PagesModel {

	@NotEmpty(message = "Page name should not be empty")
	private String pageName;

	@NotEmpty(message = "Page description should not be empty")
	private String pageDescription;

	private String pageCoverProfileImagePath;

	private String linkUrlName;

	private String linkUrl;

}

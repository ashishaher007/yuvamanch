package com.ymanch.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostReactUpdate {
	@Pattern(regexp = "Like|Dislike|Angry|Funny|Happy|Love|Wow", message = "Post react name must be Like Or Dislike Or Angry Or Funny Or Happy Or Love Or Wow")
	private String postReactName;

	@NotEmpty(message = "Post react image url should not be empty")
	private String postReactImageUrl;
}

package com.ymanch.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Home {
	private List<PostIndex> postData = new ArrayList<>();
}

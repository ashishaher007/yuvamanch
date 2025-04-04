package com.ymanch.helper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtResponse {
	String token;

	public JwtResponse() {

	}

	public JwtResponse(String token) {
		super();
		this.token = token;
	}
}

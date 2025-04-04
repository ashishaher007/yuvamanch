package com.ymanch.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


public class UserInactiveException extends RuntimeException {
	public UserInactiveException(String message) {
		super(message);
	}
}

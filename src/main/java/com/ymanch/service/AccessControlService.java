package com.ymanch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.ymanch.entity.User;
import com.ymanch.exception.AuthenticationException;
import com.ymanch.repository.UserRepository;

@Service
public class AccessControlService {
	@Autowired
	private UserRepository userRepo;

	public void verifyUserAccess(Long requestedUserId) {
		// Extract the currently authenticated user's username
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentUsername = authentication.getName();

		// Load user details
		User currentUser = userRepo.findByUserEmailAndUserId(currentUsername, requestedUserId);
		if (currentUser == null || !Long.valueOf(currentUser.getUserId()).equals(requestedUserId)) {
			throw new AuthenticationException("You are not authorized to access this resource");
		}
	}
}

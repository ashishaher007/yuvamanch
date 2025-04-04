package com.ymanch.helper;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ymanch.entity.User;
import com.ymanch.exception.ResourceNotFoundException;
import com.ymanch.exception.UserInactiveException;
import com.ymanch.exception.UserRoleException;
import com.ymanch.repository.UserRepository;
import com.ymanch.service.AccessControlService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class AccessControlAspect {

	private UserRepository userRepo;
	private AccessControlService accessControlService;
	private CommonFunctions commonFunctions;

	public AccessControlAspect(UserRepository userRepo, AccessControlService accessControlService,
			CommonFunctions commonFunctions) {
		super();
		this.userRepo = userRepo;
		this.accessControlService = accessControlService;
		this.commonFunctions = commonFunctions;
	}

	@Transactional
	@Before("@annotation(com.ymanch.helper.RequireUserAccess)")
	public void verifyUserAccess(JoinPoint joinPoint)
			throws ResourceNotFoundException, UserInactiveException, UserRoleException {
		log.info("***** Inside RequireUserAccess - AccessControlAspect *****");
		// Log the method arguments for debugging
		Object[] args = joinPoint.getArgs();
//		for (Object arg : args) {
//			log.info("Argument: " + arg);
//		}
		// Extract the userId from the method arguments if available
		Long requestedUserId = null;
		for (Object arg : args) {
			if (arg instanceof Long) {
				requestedUserId = (Long) arg;
				break;
			} else if (arg instanceof HttpServletRequest) {
				requestedUserId = commonFunctions.getUserIdFromRequest((HttpServletRequest) arg);
				break;
			}
		}

		if (requestedUserId == null) {
//			log.error("User ID cannot be null.");
			throw new IllegalArgumentException("User ID cannot be null");
		}
		// Verify user access if requestedUserId is not null
		accessControlService.verifyUserAccess(requestedUserId);
		System.out.println("called" + requestedUserId);

		// Check user status
		User userData = userRepo.findById(requestedUserId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		if (!"ACTIVE".equals(userData.getUserStatus())) {
			throw new UserInactiveException("User with the Id " + requestedUserId + " is In-Active");
		}
//		if (!"ROLE_USER".equals(userData.getUserRole()) && !"ROLE_ADMIN".equals(userData.getUserRole())) {
//			throw new UserRoleException("Users with the Id " + requestedUserId + " does not have the required role");
//		}
	}
}
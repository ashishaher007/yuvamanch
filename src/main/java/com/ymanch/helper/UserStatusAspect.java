package com.ymanch.helper;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ymanch.entity.User;
import com.ymanch.exception.ResourceNotFoundException;
import com.ymanch.exception.UserInactiveException;
import com.ymanch.exception.UserRoleException;
import com.ymanch.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class UserStatusAspect {
	@Autowired
	private UserRepository userRepo;

	@Transactional
	@Before("@annotation(com.ymanch.helper.CheckUserStatus)")
	public void checkUserStatus(JoinPoint joinPoint) throws ResourceNotFoundException, UserInactiveException {
		log.info("***** Inside UserStatusAspect - checkUserStatus *****");
		Object[] args = joinPoint.getArgs();
		Long userId = extractUserId(args);
		if (userId != null) {
			User userData = userRepo.findById(userId)
					.orElseThrow(() -> new ResourceNotFoundException("User with the Id " + userId + " not found"));

			if (!"ACTIVE".equals(userData.getUserStatus())) {
				throw new UserInactiveException("User with the Id " + userId + " is In-Active");
			}
			if (!"ROLE_USER".equals(userData.getUserRole())) {
				throw new UserRoleException("User with the Id " + userId + " does not have the required role");
			}
		}
	}

	private Long extractUserId(Object[] args) {
//		if (args.length > 1 && args[1] instanceof Long) {
//			return (Long) args[1];
//		}
		if (args.length > 0 && args[0] instanceof Long) {
			return (Long) args[0];
		}
		return null;
	}

}

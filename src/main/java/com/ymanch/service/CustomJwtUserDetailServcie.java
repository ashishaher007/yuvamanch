package com.ymanch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.ymanch.config.CustomJwtUserDetail;
import com.ymanch.entity.User;
import com.ymanch.exception.ResourceNotFoundException;
import com.ymanch.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomJwtUserDetailServcie implements UserDetailsService {

	@Autowired
	private UserRepository userRepo;

	@Override
	public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
		if (log.isInfoEnabled()) {
			log.info("***** Inside CustomJwtUserDetailServcie - loadUserByUsername *****");
		}
		User user = userRepo.findFirstByUserEmail(identifier);
		if (user == null) {
			user = userRepo.findByUserMobileNumberOrUserpassword(identifier, identifier);
		}
		if (user == null) {
			throw new UsernameNotFoundException("User not found with identifier: " + identifier);
		}

//		return new org.springframework.security.core.userdetails.User(user.getUserEmail(), user.getUserPassword(),
//				new ArrayList<>());
		CustomJwtUserDetail customUserDetails = new CustomJwtUserDetail(user);
		return customUserDetails;

	}

}

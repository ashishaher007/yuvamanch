package com.ymanch.mapper;

import org.springframework.stereotype.Service;

import com.ymanch.model.AdminUserDetails;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserMapper {

	

	public AdminUserDetails map(Object[] source) {
		log.info("***** Inside UserMapper - map()");

		// Assuming the Object[] is in the same order as in the SELECT clause
		return AdminUserDetails.builder().userId(((Number) source[0]).longValue()).userFirstName((String) source[1])
				.userLastName((String) source[2]).userEmail((String) source[3]).userProfileImagePath((String) source[4])
				.userCreatedAt(((java.sql.Timestamp) source[5]).toLocalDateTime())
				.totalPostCount(((Number) source[6]).intValue())
				.userDateOfBirth(source[7] != null ? source[7].toString() : null)  // Fix: Extract date properly
	            .userAddress((String) source[8])
				.build();

	}

}

package com.ymanch.service;

import java.util.HashMap;

import org.springframework.http.ResponseEntity;

import com.ymanch.exception.ResourceNotFoundException;
import com.ymanch.model.UserHobbiesInterestModel;
import com.ymanch.model.UserLoginModel;
import com.ymanch.model.UserRegisterModel;
import com.ymanch.model.UserUpdateModel;

import jakarta.validation.Valid;

public interface UserService {

	ResponseEntity<Object> addUser(UserRegisterModel user);

	ResponseEntity<Object> userLogin(@Valid UserLoginModel login) throws Exception;

	ResponseEntity<Object> updateUserPassword(String userEmail, String userPassword);

	ResponseEntity<Object> getUserAboutData(String uuid) throws ResourceNotFoundException;

	ResponseEntity<Object> getUserTimelineDetails(String userUUID, String targetUserUUID, long cursor, int size);

	ResponseEntity<Object> findUserByName(String userName);

	ResponseEntity<Object> changeUserDetails(long userId, UserUpdateModel userDetails);

	ResponseEntity<Object> removeUser(long userId);

	ResponseEntity<Object> sendEmailToUpdatePassword(String emailId);

	ResponseEntity<Object> validateUserPassword(String emailId, String password);

	ResponseEntity<Object> getUserInfoData(long userId, long targetUserId);

	ResponseEntity<Object> getAllActiveUsers();

	ResponseEntity<Object> districtwiseCount();

}

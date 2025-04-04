package com.ymanch.service;

import org.springframework.http.ResponseEntity;

import com.ymanch.controller.Admin.AdminPostController;
import com.ymanch.entity.DisputeTitles;
import com.ymanch.entity.Organisation;
import com.ymanch.helper.Enums.PostOwnerType;
import com.ymanch.model.AdminPostAdvertisementModel;
import com.ymanch.model.SendMessage;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

public interface AdminService {

//	ResponseEntity<Object> adminLogin(@Valid UserLoginModel login) throws Exception;

	ResponseEntity<Object> getUserActivityCounts();

	ResponseEntity<Object> getUserLineChartDetails();

//	ResponseEntity<Object> getAllUserDetails() throws Exception;

	ResponseEntity<Object> getAllPostDetails(int page, int size);

	ResponseEntity<Object> getAllUserDetailsWithPagination(String firstName, long districtId, int page, int size)
			throws Exception;

	ResponseEntity<Object> storeAdminAdvertisementPost(long adminId, AdminPostAdvertisementModel post, long district);

	ResponseEntity<Object> logInData(String tokenHeader);

	ResponseEntity<Object> stats(HttpServletRequest tokenHeader);

	ResponseEntity<Object> updateUUID();

	ResponseEntity<Object> fetchPosts(long userId, int page, int size);

	ResponseEntity<Object> storeOrgDetails(@Valid Organisation org);

	ResponseEntity<Object> retrieveOrgDetails();

	ResponseEntity<Object> updateUserFullname();

	ResponseEntity<Object> sendAdminMessage(SendMessage message, Long adminId);

}

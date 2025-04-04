package com.ymanch.service;

import org.springframework.http.ResponseEntity;

import com.ymanch.entity.DisputeTitles;
import com.ymanch.entity.Disputes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

public interface DisputeService {

	ResponseEntity<Object> addDisputesTitles(@Valid DisputeTitles disputeTitles);

	ResponseEntity<Object> raiseDispute(long userId, long postId, long disputeTitleId, Disputes dispute);

	ResponseEntity<Object> getDisputesTitles();

	ResponseEntity<Object> getAllDisputedPost(Long adminId, int page, int size) throws Exception;

	ResponseEntity<Object> getAllDisPost(HttpServletRequest request, int page, int size);

}

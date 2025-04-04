package com.ymanch.controller.Admin;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ymanch.service.DisputeService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ymanch/admin/dispute")
@Slf4j
public class AdminDisputeController {

	private DisputeService disputeService;

	public AdminDisputeController(DisputeService disputeService) {
		super();
		this.disputeService = disputeService;
	}

	@Operation(summary = "Get Disputes By District API", description = "This API is used to retrieve all disputed posts raised by the user")
	@GetMapping("/getAllDisputesByDistrict")
	public ResponseEntity<Object> getDispDistrict(HttpServletRequest request, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) throws Exception {
		log.info("***** Inside AdminDisputeController - getDispDistrict *****");
		Long adminId = (Long) request.getAttribute("userId");
		return disputeService.getAllDisputedPost(adminId,page,size);
	}
	
	@Operation(summary = "Get All District API", description = "This API is used to retrieve all disputed posts raised by the user")
	@GetMapping("/getAllDisputes")
	public ResponseEntity<Object> getAllDisputes(HttpServletRequest request, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) throws Exception {
		log.info("***** Inside AdminDisputeController - get *****");
		return disputeService.getAllDisPost(request,page,size);
	}
}

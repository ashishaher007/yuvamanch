
package com.ymanch.controller.SuperAdmin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.ymanch.entity.DisputeTitles;
import com.ymanch.entity.EventCategory;
import com.ymanch.entity.Organisation;
import com.ymanch.exception.ResourceNotFoundException;
import com.ymanch.helper.CommonFunctions;
import com.ymanch.helper.RequireUserAccess;
import com.ymanch.helper.Views;
import com.ymanch.helper.Enums.PostOwnerType;
import com.ymanch.model.AdminPostAdvertisementModel;
import com.ymanch.model.SendMessage;
import com.ymanch.service.AdminService;
import com.ymanch.service.DisputeService;
import com.ymanch.service.EventService;
import com.ymanch.service.GroupService;
import com.ymanch.service.PagesService;
import com.ymanch.service.PostService;
import com.ymanch.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ymanch/supadmin/eventcatg")
@Slf4j
public class SuperAdminController {
	private EventService eventService;
	private DisputeService disputeService;
	private AdminService adminService;
	private CommonFunctions commonFunctions;
	private PostService postService;
	private GroupService groupService;
	private PagesService pagesService;
	private UserService userservice;

	public SuperAdminController(EventService eventService, DisputeService disputeService, AdminService adminService,
			CommonFunctions commonFunctions, PostService postService,GroupService groupService,PagesService pagesService,UserService userservice) {
		super();
		this.eventService = eventService;
		this.disputeService = disputeService;
		this.adminService = adminService;
		this.commonFunctions = commonFunctions;
		this.postService = postService;
		this.groupService=groupService;
		this.pagesService=pagesService;
		this.userservice=userservice;
	}

	@Operation(summary = "Add Event Category", description = "This API allows to add new event categories")
	@JsonView(Views.Public.class)
	@PostMapping("/add")
	public ResponseEntity<Object> add(@RequestBody EventCategory evCatgeory) {
		if (log.isInfoEnabled()) {
			log.info("***** Inside SuperAdminController - add *****");
		}
		return eventService.add(evCatgeory);

	}

	@Operation(summary = "Get Event Category", description = "This API is used to get all categories of events")
	@JsonView(Views.Internal.class)
	@GetMapping("/get")
	public ResponseEntity<Object> get() {
		if (log.isInfoEnabled()) {
			log.info("***** Inside SuperAdminController - get *****");
		}
		return eventService.get();
	}

	@Operation(summary = "Add Dispute Titles API", description = "This API is used to add dispute titles")
	@PostMapping("/addDisputeTitles")
	public ResponseEntity<Object> addDisputesTitles(@Valid @RequestBody DisputeTitles disputeTitles) {
		if (log.isInfoEnabled()) {
			log.info("***** Inside SuperAdminController - addDisputeTitles *****");
		}
		log.info("***** Inside AdminDisputeController - addDisputesTitles *****");
		return disputeService.addDisputesTitles(disputeTitles);
	}

	@Operation(summary = "Get Dispute Titles API", description = "This API is used to get dispute titles")
	@GetMapping("/getDisputeTitles")
	public ResponseEntity<Object> getDisputeTitles() {
		if (log.isInfoEnabled()) {
			log.info("***** Inside SuperAdminController - get *****");
		}
		log.info("***** Inside AdminDisputeController - getDisputeTitles *****");
		return disputeService.getDisputesTitles();
	}

	@Operation(summary = "Update UUID API", description = "This API is used to add uuid for old records")
	@PutMapping("/updateUserUUID")
	public ResponseEntity<Object> updateUserUUID() {
		if (log.isInfoEnabled()) {
			log.info("***** Inside SuperAdminController - updateUserUUID *****");
		}
		log.info("***** Inside AdminDisputeController - updateUserUUID *****");
		return adminService.updateUUID();
	}

	@Operation(summary = "Update full name", description = "This API is used to add full name for old records")
	@PutMapping("/updateUserFullName")
	public ResponseEntity<Object> updateUserFullname() {
		if (log.isInfoEnabled()) {
			log.info("***** Inside SuperAdminController - updateUserFullname *****");
		}
		log.info("***** Inside AdminDisputeController - updateUserFullname *****");
		return adminService.updateUserFullname();
	}

	@JsonView(Views.Public.class)
	@Operation(summary = "Add Organisation Names API", description = "This API is used to add names of the organisation")
	@PostMapping("/addOrg")
	public ResponseEntity<Object> addOrg(@Valid @RequestBody Organisation org) {
		if (log.isInfoEnabled()) {
			log.info("***** Inside SuperAdminController - addOrg *****");
		}
		return adminService.storeOrgDetails(org);

	}

	@Operation(summary = "Get Organisation Details API", description = "This API is used to get organisation details")
	@GetMapping("/getOrgDetails")
	public ResponseEntity<Object> getOrgDetails() {
		if (log.isInfoEnabled()) {
			log.info("***** Inside SuperAdminController - getOrgDetails *****");
		}
		return adminService.retrieveOrgDetails();
	}

	@Operation(summary = "Add Advertisement(Event) API", description = "This API is used to add a new advertisement")
	@PostMapping("/uploadImage")
	ResponseEntity<Object> uploadImage(HttpServletRequest request, @ModelAttribute AdminPostAdvertisementModel post)
			throws ResourceNotFoundException {
		if (log.isInfoEnabled()) {
			log.info("***** Inside SuperAdminController - uploadImage *****");
		}
		Long supAdminId = commonFunctions.getUserIdFromRequest(request);
		return adminService.storeAdminAdvertisementPost(supAdminId, post, Long.valueOf(37));
	}
	
//	@Operation(summary = "Add Advertisement(Event) API", description = "This API is used to add a new advertisement")
//	@PostMapping("/uploadImage/{districtId}")
//	ResponseEntity<Object> uploadImage(HttpServletRequest request, @ModelAttribute AdminPostAdvertisementModel post,@PathVariable long districtId)
//			throws ResourceNotFoundException {
//		if (log.isInfoEnabled()) {
//			log.info("***** Inside SuperAdminController - uploadImage *****");
//		}
//		Long supAdminId = commonFunctions.getUserIdFromRequest(request);
//		return adminService.storeAdminAdvertisementPost(supAdminId, post,districtId);
//	}

	@Operation(summary = "Get Advertisement By District Api", description = "This API is used to retrieve advertisements by districts")
	@GetMapping("/advertisement/{districtId}")
	ResponseEntity<Object> getAdvertisementByDistrict(@PathVariable long districtId) {
		log.info("***** Inside SuperAdminController - getAdvertisementByDistrict");
		return postService.getAllPostDetailsByDistricts(districtId);
	}

	@Operation(summary = "Get Advertisement[MINISTER] API", description = "This API is used to retrieve advertisement for ministers")
	@GetMapping("/getAds/{postCategory}")
	ResponseEntity<Object> getAdvertisements(@PathVariable String postCategory) {
		log.info("***** Inside SuperAdminController - getAdvertisements");
		return postService.retrieveAds(postCategory);
	}

	@Operation(summary = "Delete Post Api", description = "This API is used to delete post(Advertisement)")
	@DeleteMapping("/delete/{postId}")
	ResponseEntity<Object> deletAdv(@PathVariable long postId) throws ResourceNotFoundException {
		log.info("***** Inside - SuperAdminController - deletAdv *****");
		return postService.deletePost(postId);
	}

//	@Operation(summary = "Send Message API", description = "This API is used to send message to user")
//	@PostMapping("/sendMessage")
//	@RequireUserAccess
//	ResponseEntity<Object> sendMessage(HttpServletRequest request, @RequestBody SendMessage message)
//			throws ResourceNotFoundException {
//		if (log.isInfoEnabled()) {
//			log.info("***** Inside SuperAdminController - sendMessage *****");
//		}
//		Long adminId = commonFunctions.getUserIdFromRequest(request);
//		return adminService.sendAdminMessage(message, adminId);
//	}
	
	@Operation(summary = "Get all Groups Api", description = "This API is used to retrieve All groups")
	@GetMapping("/getGroups")
	ResponseEntity<Object> getAllGroups() {
		log.info("***** Inside SuperAdminController - getAllGroups");
		return groupService.getAllGroups();
	}
	
	@Operation(summary = "Get all Pages Api", description = "This API is used to retrieve All Pages")
	@GetMapping("/getAllPages")
	ResponseEntity<Object> getAllPages() {
		log.info("***** Inside SuperAdminController - getAllPages");
		return pagesService.getAllPages();
	}
	
	@Operation(summary = "Get all Active users Api", description = "This API is used to retrieve All Active Users")
	@GetMapping("/getAllActiveUsers")
	ResponseEntity<Object> getAllActiveUsers() {
		log.info("***** Inside SuperAdminController - getAllActiveUsers");
		return userservice.getAllActiveUsers();
	}
	
//	@Operation(summary = "Get Announcement At Userside API", description = "This API is used to retrieve Announcement At Userside")
//	@GetMapping("/getAnnouncement/{postCategory}/{districtId}")
//	ResponseEntity<Object> getAnnouncementAtUserSide(@PathVariable String postCategory,@PathVariable Long districtId) {
//		log.info("***** Inside SuperAdminController - getAnnouncementAtUserSide");
//		return postService.getAnnouncementAtUserSide(postCategory,districtId);
//	}

}

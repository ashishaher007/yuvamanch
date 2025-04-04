package com.ymanch.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ymanch.entity.User;
import com.ymanch.exception.ResourceNotFoundException;
import com.ymanch.helper.CheckUserStatus;
import com.ymanch.helper.CommonFunctions;
import com.ymanch.helper.CommonMessages;
import com.ymanch.helper.RequireUserAccess;
import com.ymanch.model.UserLoginModel;
import com.ymanch.model.UserRegisterModel;
import com.ymanch.model.UserUpdateModel;
import com.ymanch.repository.UserRepository;
import com.ymanch.service.AccessControlService;
import com.ymanch.service.AdminService;
import com.ymanch.service.PostService;
import com.ymanch.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ymanch/users/user")
@Slf4j
public class UserController {

//	@Value("${jwt.privateKey.path}")
//	private String privateKeyPath; // RSA private key from application.properties
//
//	@Value("${jwt.publicKey.path}")
//	private String publicKeyPath; // RSA public key from application.properties
	private CommonMessages msg;
	private UserService userService;
	private AccessControlService accessControlService;
	private AdminService adminService;
	private PostService postService;
	private CommonFunctions commonFunctions;
	@Autowired
	private UserRepository userRepo;

	public UserController(CommonMessages msg, UserService userService, AccessControlService accessControlService,
			AdminService adminService,PostService postService,CommonFunctions commonFunctions) {
		this.msg = msg;
		this.userService = userService;
		this.accessControlService = accessControlService;
		this.adminService = adminService;
		this.postService=postService;
		this.commonFunctions=commonFunctions;
	}

	@GetMapping("/test")
	public ResponseEntity<Object> test() {
		Map<String, Object> response = new HashMap<>();

		log.info("**** Inside - UserController - test ****");

		/*
		 * try { // Step 1: Read the PEM file and extract the Base64 content
		 * StringBuilder pemContent = new StringBuilder(); try (BufferedReader reader =
		 * new BufferedReader(new FileReader(privateKeyPath))) { String line; while
		 * ((line = reader.readLine()) != null) { if (!line.startsWith("-----")) { //
		 * Skip BEGIN/END lines pemContent.append(line); } } }
		 * 
		 * // Step 2: Decode the Base64 content byte[] privateKeyBytes =
		 * Base64.getDecoder().decode(pemContent.toString());
		 * 
		 * // Step 3: Generate the private key using PKCS#8 PKCS8EncodedKeySpec keySpec
		 * = new PKCS8EncodedKeySpec(privateKeyBytes); KeyFactory keyFactory =
		 * KeyFactory.getInstance("RSA"); RSAPrivateKey privateKey = (RSAPrivateKey)
		 * keyFactory.generatePrivate(keySpec);
		 * 
		 * // If no exception is thrown, the key is successfully loaded
		 * response.put("status", "Private key loaded successfully."); } catch
		 * (Exception e) { log.error("Error loading private key", e);
		 * response.put("status", "Error: " + e.getMessage()); }
		 */

		//String url = "https://www.youtube.com/shorts/JoyKXJdWKOE?feature=share";
		String url = "https://youtube.com/shorts/sZy1Q7frqPk?si=fkvxLZjcZPE1iRDo";
		try {

			Document doc = Jsoup.connect(url).get();

			// Open Graph metadata
			String title = doc.select("meta[property=og:title]").attr("content");
			if (title.isEmpty()) {
				title = doc.title(); // Fallback
			}
			String description = doc.select("meta[property=og:description]").attr("content");
			if (description.isEmpty()) {
				description = doc.select("meta[name=description]").attr("content");
			}
			String imageUrl = doc.select("meta[property=og:image]").attr("content");
			String siteName = doc.select("meta[property=og:site_name]").attr("content");
			String type = doc.select("meta[property=og:type]").attr("content");
			String urlTag = doc.select("meta[property=og:url]").attr("content");

			// Twitter metadata
			String twitterTitle = doc.select("meta[name=twitter:title]").attr("content");
			String twitterDescription = doc.select("meta[name=twitter:description]").attr("content");
			String twitterImage = doc.select("meta[name=twitter:image]").attr("content");

			// Additional metadata
			String canonicalUrl = doc.select("link[rel=canonical]").attr("href");
			String keywords = doc.select("meta[name=keywords]").attr("content");
			String author = doc.select("meta[name=author]").attr("content");
			String publishedDate = doc.select("meta[property=article:published_time]").attr("content");

			// Add to response
			response.put("title", title);
			response.put("description", description);
			response.put("imageUrl", imageUrl);
			response.put("siteName", siteName);
			response.put("type", type);
			response.put("urlTag", urlTag);
			response.put("twitterTitle", twitterTitle);
			response.put("twitterDescription", twitterDescription);
			response.put("twitterImage", twitterImage);
			response.put("canonicalUrl", canonicalUrl);
			response.put("keywords", keywords);
			response.put("author", author);
			response.put("publishedDate", publishedDate);

		} catch (IOException e) {
			response.put("error", "Failed to fetch metadata: " + e.getMessage());
		}

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Operation(summary = "Register User API", description = "This API is used to register a user.")
	@PostMapping("/userRegister")
	ResponseEntity<Object> userRegister(@Valid @RequestBody UserRegisterModel user) {
		log.info("***** Inside - UserController - userRegister *****");
		return userService.addUser(user);
	}

	@Operation(summary = "User Login API", description = "This API is used by the user for login.")
	@PostMapping("/userLogin")
	public ResponseEntity<Object> userLogin(@Valid @RequestBody UserLoginModel login) throws Exception {
	    log.info("***** Inside UserController - userLogin *****");
	    return userService.userLogin(login);
	}


//	@Operation(summary = "Forget Password API", description = "This API is to add a new password.")
//	@PutMapping("/userForgetPassword/{userEmail}")
//	@CheckUserStatus
//	ResponseEntity<Object> userForgetPassword(@PathVariable String userEmail, @RequestParam String userPassword) {
//		log.info("***** Inside - UserController - userForgetPassword");
//		return userService.updateUserPassword(userEmail, userPassword);
//	}

	@Operation(summary = "User About Api", description = "This API is used to get all information about a user by their userId")
	@GetMapping("/userAbout/{uuid}")
	ResponseEntity<Object> userAbout(@PathVariable String uuid) throws ResourceNotFoundException {
		log.info("***** Inside - UserController - userAbout *****");
//		accessControlService.verifyUserAccess(userId);
		return userService.getUserAboutData(uuid);
	}

	@Operation(summary = "User About Api For Android", description = "This API is used to get all information about a user by their userId")
	@GetMapping("/userAbout/v1/{userId}/{targetUserId}")
	ResponseEntity<Object> userInfo(@PathVariable long userId, @PathVariable long targetUserId)
			throws ResourceNotFoundException {
		log.info("***** Inside - UserController - userInfo *****");
//		accessControlService.verifyUserAccess(userId);
		return userService.getUserInfoData(userId, targetUserId);
	}

	@Operation(summary = "User Timeline Api", description = "This Api is used to get information from a user's timeline")
	@GetMapping("/userTimeline/{userUUID}/{targetUserUUID}")
	@CheckUserStatus
	ResponseEntity<Object> userTimeline(@PathVariable String userUUID, @PathVariable String targetUserUUID,
			@RequestParam(defaultValue = "0") Long cursor, @RequestParam(defaultValue = "5") int size) {
		log.info("***** Inside - UserController - userTimeline *****");
		return userService.getUserTimelineDetails(userUUID, targetUserUUID, cursor, size);
	}

	@Operation(summary = "Search User Api", description = "This Api is used to search for a specific user by name")
	@GetMapping("/searchUser/{userName}")
	ResponseEntity<Object> searchUser(@PathVariable String userName) {
		log.info("***** Inside UserController - searchUser *****");
		return userService.findUserByName(userName);
	}

	@Operation(summary = "Update User Api", description = "This Api is used to update a user details by userId")
	@PutMapping("/updateUser/{userId}")
	@CheckUserStatus
	ResponseEntity<Object> updateUser(@PathVariable long userId, @ModelAttribute UserUpdateModel userDetails) {
		log.info("***** Inside UserController - updateUser *****");
		accessControlService.verifyUserAccess(userId);
		return userService.changeUserDetails(userId, userDetails);
	}

	@Operation(summary = "Forget Password API", description = "This API is used add new password of user by email address")
	@PostMapping("/forgetPassword/{emailId}")
	ResponseEntity<Object> forgetPassword(@PathVariable String emailId) throws ResourceNotFoundException {
		log.info("***** Inside - UserController - forgetPassword *****");
		return userService.sendEmailToUpdatePassword(emailId);
	}

	@Operation(summary = "Validate Password Api", description = "This Api is used to validate the otp and update the new password")
	@PutMapping("/validatePassword")
	ResponseEntity<Object> validatePassword(@RequestParam String password, @RequestParam String otp) {
		log.info("***** Inside UserController - searchUser *****");
		return userService.validateUserPassword(password, otp);
	}

	@Operation(summary = "Get Organisation Details API", description = "This API is used to get organisation details")
	@CheckUserStatus
	@GetMapping("/getOrgDetails")
	public ResponseEntity<Object> getOrgDetails() {
		if (log.isInfoEnabled()) {
			log.info("***** Inside UserController - getOrgDetails *****");
		}
		return adminService.retrieveOrgDetails();
	}

	@Operation(summary = "Delete User Api", description = "This Api is used to delete a user by userId")
	@DeleteMapping("/deleteUser/{userId}")
	@RequireUserAccess
	ResponseEntity<Object> deleteUser(@PathVariable long userId) {
		if (log.isInfoEnabled()) {
			log.info("***** Inside UserController - deleteUser *****");
		}
		return userService.removeUser(userId);

	}
	
//	@Operation(summary = "Get Announcement At Userside API", description = "This API is used to retrieve Announcement At Userside")
//	@GetMapping("/getAnnouncement/{postCategory}")
//	ResponseEntity<Object> getAnnouncementAtUserSide(HttpServletRequest request,@PathVariable String postCategory) {
//		log.info("***** Inside SuperAdminController - getAnnouncementAtUserSide");
//		Long userId = commonFunctions.getUserIdFromRequest(request);
//		Optional<User> user=userRepo.findById(userId);
//		System.out.println(user.get().getDistrict().getDistrictId()+"***********************"+user.get().getUserId());
//		System.out.println(postCategory);
//		return postService.getAnnouncementAtUserSide(postCategory,user.get().getDistrict().getDistrictId());
//	}
	
	@Operation(summary = "Get Announcement At Userside API", description = "This API is used to retrieve Announcement At Userside")
	@GetMapping("/getAnnouncement/{postCategory}")
	ResponseEntity<Object> getAnnouncementAtUserSide(HttpServletRequest request,@PathVariable String postCategory) {
		log.info("***** Inside SuperAdminController - getAnnouncementAtUserSide");
		Long userId = commonFunctions.getUserIdFromRequest(request);
		Optional<User> user=userRepo.findById(userId);
		System.out.println(user.get().getDistrict().getDistrictId()+"***********************"+user.get().getUserId());
		System.out.println(postCategory);
		return postService.getAnnouncementAtUserSide(postCategory,Long.valueOf(37));
	}
	
	@Operation(summary = "Count By District Api", description = "This Api is used to get district wise new registration count")
	@GetMapping("/district-counts")
	ResponseEntity<Object> registerCountByDistrict()
	{
		log.info("***** Inside UserController - searchUser *****");
		return userService.districtwiseCount();
	}


}

package com.ymanch.serviceimpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ymanch.entity.District;
import com.ymanch.entity.ForgetPassword;
import com.ymanch.entity.FriendRequest;
import com.ymanch.entity.Group;
import com.ymanch.entity.Organisation;
import com.ymanch.entity.Pages;
import com.ymanch.entity.Posts;
import com.ymanch.entity.User;
import com.ymanch.exception.AuthenticationException;
import com.ymanch.exception.ResourceNotFoundException;
import com.ymanch.helper.ApiResponse;
import com.ymanch.helper.CommonFunctions;
import com.ymanch.helper.CommonMessages;
import com.ymanch.helper.JwtHelper;
import com.ymanch.model.FriendsList;
import com.ymanch.model.PostIndexPage;
import com.ymanch.model.UserLoginModel;
import com.ymanch.model.UserRegisterModel;
import com.ymanch.model.UserSearchModel;
import com.ymanch.model.UserUpdateModel;
import com.ymanch.repository.DistrictRepository;
import com.ymanch.repository.ForgetPasswordRepository;
import com.ymanch.repository.FriendRequestRepository;
import com.ymanch.repository.GroupRepository;
import com.ymanch.repository.OrganisationRepository;
import com.ymanch.repository.PagesRepository;
import com.ymanch.repository.PostRepository;
import com.ymanch.repository.UserRepository;
import com.ymanch.service.CustomJwtUserDetailServcie;
import com.ymanch.service.UserService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
	private PasswordEncoder encoder;
	private ModelMapper modelMapper;
	private CommonMessages MSG;
	private CustomJwtUserDetailServcie customJwtUserDetailService;
	private JwtHelper jwtHelper;
	private UserRepository userRepo;
	private PostRepository postRepo;
	private FriendRequestRepository friendRequestRepo;
	private GroupRepository groupRepo;
	private AuthenticationManager authenticationManager;
	private ForgetPasswordRepository forgetPasswordRepo;
	private JavaMailSender mailSender;
	private FriendRequestServiceImpl friendRequestServiceImpl;
	private DistrictRepository districtRepo;
	private CommonFunctions commonFunctions;
	private PagesRepository pagesRepo;
	private OrganisationRepository organisationRepo;
	private RedisTemplate<String, Object> redisTemplate;

	@Value("${app.url}")
	private String appUrl;

	public UserServiceImpl(PasswordEncoder encoder, ModelMapper modelMapper, CommonMessages mSG,
			CustomJwtUserDetailServcie customJwtUserDetailService, JwtHelper jwtHelper, UserRepository userRepo,
			PostRepository postRepo, FriendRequestRepository friendRequestRepo,
			AuthenticationManager authenticationManager, ForgetPasswordRepository forgetPasswordRepo,
			JavaMailSender mailSender, FriendRequestServiceImpl friendRequestServiceImpl, GroupRepository groupRepo,
			DistrictRepository districtRepo, CommonFunctions commonFunctions, PagesRepository pagesRepo,
			OrganisationRepository organisationRepo, RedisTemplate<String, Object> redisTemplate) {
		this.encoder = encoder;
		this.modelMapper = modelMapper;
		MSG = mSG;
		this.customJwtUserDetailService = customJwtUserDetailService;
		this.jwtHelper = jwtHelper;
		this.userRepo = userRepo;
		this.postRepo = postRepo;
		this.friendRequestRepo = friendRequestRepo;
		this.authenticationManager = authenticationManager;
		this.forgetPasswordRepo = forgetPasswordRepo;
		this.mailSender = mailSender;
		this.friendRequestServiceImpl = friendRequestServiceImpl;
		this.groupRepo = groupRepo;
		this.districtRepo = districtRepo;
		this.commonFunctions = commonFunctions;
		this.pagesRepo = pagesRepo;
		this.organisationRepo = organisationRepo;
		this.redisTemplate = redisTemplate;
	}

	@Transactional
	@Override
	public ResponseEntity<Object> addUser(UserRegisterModel userDetails) {
		log.info("***** Inside - UserServiceImpl - addUser *****");
		Map<Object, Object> response = new HashMap<>();
		/* get district details */
		District diDetails = districtRepo.findById(userDetails.getDistrictId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"District with the Id" + userDetails.getDistrictId() + " not found"));
		User userData = userRepo.findFirstByUserEmail(userDetails.getUserEmail());
		User userMData = userRepo.findByUserMobileNumber(userDetails.getUserMobileNumber());
		if (userMData != null) {
			response.put(CommonMessages.STATUS, CommonMessages.FAILED);
			response.put(CommonMessages.MESSAGE, MSG.USER_REGISTER_MOBILE_NO_FAILED);
			return new ResponseEntity<>(response, HttpStatus.CONFLICT);
		}

		if (userData == null || userData.getUserEmail() == null || userData.getUserEmail().isEmpty()
				|| !userData.getUserEmail().equals(userDetails.getUserEmail())) {
			User user = new User();
			user.setUserFirstName(userDetails.getUserFirstName());
			user.setUserLastName(userDetails.getUserLastName());
			user.setUserEmail(userDetails.getUserEmail());
			user.setUserDateOfBirth(userDetails.getUserDateOfBirth());
			user.setUserGender(userDetails.getUserGender());
			user.setUserRole(userDetails.getUserRole());
			user.setUserpassword(encoder.encode(userDetails.getUserPassword()));
			user.setUserStatus("ACTIVE");
			user.setUserAddress(userDetails.getUserAddress());
			user.setUserProfileImagePath(userDetails.getUserProfileImagePath());
			user.setUserCoverProfileImagePath(userDetails.getUserCoverProfileImagePath());
			user.setDistrict(diDetails);
			user.setUserMobileNumber(userDetails.getUserMobileNumber());
			user.setTermsAndConditionsAccepted(true);
			user.setFullName(userDetails.getUserFirstName() + " " + userDetails.getUserLastName());
			userRepo.save(user);

			response.put("status", CommonMessages.SUCCESS);
			response.put("message", MSG.USER_REGISTARTION_SUCCESSFUL);
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} else {
			response.put("status", CommonMessages.FAILED);
			response.put("message", MSG.USER_REGISTARTION_FAILED);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
	}

//	private void authenticate(String userEmail, String password) throws Exception {
//		try {
//			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userEmail, password));
//		} catch (DisabledException e) {
//			throw new Exception("USER_DISABLED", e);
//		} catch (BadCredentialsException e) {
//			throw new Exception("INVALID_CREDENTIALS", e);
//		}
//
//	}

	@Transactional
	@Override
	public ResponseEntity<Object> userLogin(@Valid UserLoginModel login) throws Exception {
		log.info("***** inside - UserServiceImpl - login *****");
		Map<Object, Object> response = new HashMap<>();
		String token, identifier;

		if (login.getUserEmailOrMobileNumber() == null || login.getUserEmailOrMobileNumber().isEmpty()) {
			response.put("status", CommonMessages.FAILED);
			response.put("message", MSG.USER_LOGIN_EMAIL_FAILED);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

		User userData = userRepo.findFirstByUserEmailOrUserMobileNumber(login.getUserEmailOrMobileNumber(),
				login.getUserEmailOrMobileNumber());

		if (userData == null) {
			response.put("status", CommonMessages.FAILED);
			response.put("message", MSG.USER_LOGIN_EMAIL_FAILED);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

		// for admin login
		if (login.getLoginType() != null && !login.getLoginType().isEmpty() && !login.getLoginType().isBlank()) {
			if (!userData.getUserRole().equals(login.getLoginType())) {
				throw new AuthenticationException("You are not authorized to access this resource");
			}
		}

		if (!encoder.matches(login.getUserPassword(), userData.getUserpassword())) {
			response.put("status", CommonMessages.FAILED);
			response.put("message", MSG.USER_LOGIN_PASSWORD_FAILED);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

//		authenticate(login.getUserEmailOrMobileNumber(), login.getUserPassword());

		if (userData.getUserEmail() == null || userData.getUserEmail().isEmpty() || userData.getUserEmail().isBlank()) {
			identifier = userData.getUserMobileNumber();
		} else {
			identifier = userData.getUserEmail();
		}

		final UserDetails ud = customJwtUserDetailService.loadUserByUsername(identifier);

		token = jwtHelper.generateToken(ud, userData.getUserId(), userData.getUserStatus());

		final String expirationDate = jwtHelper.extractExpiration(token).toLocaleString();

		response.put("status", CommonMessages.SUCCESS);
		response.put("userEmail", userData.getUserEmail());
		response.put("userId", userData.getUserId());
		response.put("userFirstName", userData.getUserFirstName());
		response.put("userLastName", userData.getUserLastName());
		response.put("token", token);
		if (userData.getUserRole().equals("ROLE_USER")) {
			response.put("isAdmin", false);
			response.put("message", MSG.USER_LOGIN_SUCCESSFUL);
		} else {
			response.put("isAdmin", true);
			response.put("message", MSG.ADMIN_LOGIN_SUCCESSFUL);
		}
		response.put("userProfileImage", userData.getUserProfileImagePath());
		response.put("userCoverImage", userData.getUserCoverProfileImagePath());
		response.put("token", token);
		response.put("districtId", userData.getDistrict().getDistrictId());
		response.put("tokenExpirationDate", expirationDate);
		response.put("userUUID", userData.getUuid());
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	@Override
	public ResponseEntity<Object> updateUserPassword(String userEmail, String userPassword) {
		log.info("***** inside - UserServiceImpl - updateUserPassword *****");
		Map<Object, Object> response = new HashMap<>();
		User userData = userRepo.findByUserEmail(userEmail);
		if (userData != null) {
			userData.setUserpassword(encoder.encode(userPassword));
			userRepo.save(userData);
			response.put("status", CommonMessages.SUCCESS);
			response.put("message", MSG.USER_UPDATE_PASSWORD);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			response.put("status", CommonMessages.FAILED);
			response.put("message", MSG.USER_DATA_NOT_FOUND);
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}
	}

	@Transactional
	@Override
	public ResponseEntity<Object> getUserAboutData(String uuid) throws ResourceNotFoundException {
		log.info("***** Inside - UserServiceImpl - getUserAboutData *****");
		Map<Object, Object> response = new HashMap<>();

		// Generate a unique cache key based on UUID
		String cacheKey = "userAboutData:" + uuid;

		// Check if the data is already in cache
//		Object cachedData = redisTemplate.opsForValue().get(cacheKey);
//		if (cachedData != null) {
//			log.info("***** Returning cached data for UUID: " + uuid + " *****");
//			return new ResponseEntity<>(cachedData, HttpStatus.OK);
//		}

		User userData = userRepo.findByUuid(uuid)
				.orElseThrow(() -> new ResourceNotFoundException("User with the UUID " + uuid + " not found"));
		long totalPost = postRepo.countPostByUserId(userData.getUserId());
		long totalFriends = friendRequestRepo.countFriendsByUserId(userData.getUserId());
		response.put("userId", userData.getUserId());
		response.put("userFirstName", userData.getUserFirstName());
		response.put("userLastName", userData.getUserLastName());
		response.put("userGender", userData.getUserGender());
		response.put("userProfileImage", userData.getUserProfileImagePath());
		response.put("userCoverProfileImage", userData.getUserCoverProfileImagePath());
		response.put("userBirthDate", userData.getUserDateOfBirth());
		response.put("userLocation", userData.getUserAddress());
		response.put("userEmail", userData.getUserEmail());
		response.put("totalPosts", totalPost);
		response.put("totalFriends", totalFriends);
		response.put("userMobileNumber", userData.getUserMobileNumber());
		response.put("orgId", userData.getOrg() != null ? userData.getOrg().getOrgId() : 0);
		response.put("userOrgname", userData.getOrg() != null ? userData.getOrg().getOrgName() : null);

		// Cache the response for a specific duration (e.g., 10 minutes)
//		redisTemplate.opsForValue().set(cacheKey, response, 7, TimeUnit.DAYS);

		return new ResponseEntity<>(response, HttpStatus.OK);
		}

	@Transactional
	@Override
	public ResponseEntity<Object> getUserTimelineDetails(String userUUID, String targetUUID, long cursor, int size) {
		log.info("***** Inside - UserServiceImpl - getUserTimelineDetails *****");

		Map<Object, Object> response = new HashMap<>();
		// Generate cache key based on userUUID, targetUUID, and cursor
		String redisKey;
		if ("sameUser".equals(targetUUID)) {
			redisKey = "sameUserTimeline:" + userUUID + ":cursor:" + cursor + ":size:" + size; // Cache for own timeline
			log.info("Cache added for same user");
		} else {
			redisKey = "targetUserTimeline:" + targetUUID + ":cursor:" + cursor + ":size:" + size; // Cache for other
																									// user
			log.info("Cache added for other user");
		}
		// Check if the data is already cached
		Object cachedData = redisTemplate.opsForValue().get(redisKey);
		if (cachedData != null) {
			return new ResponseEntity<>(cachedData, HttpStatus.OK); // Return cached data if available
		}
		Page<PostIndexPage> postData;
		Pageable pageable = PageRequest.of(0, size);
		Optional<User> userData = userRepo.findByUuid(targetUUID.equals("sameUser") ? userUUID : targetUUID);
		if (userData.isEmpty()) {
			ApiResponse apiResponse = new ApiResponse(CommonMessages.FAILED, MSG.USER_DETAILS);
			return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
		}
		// Fetch posts based on whether the user is the same
		if (targetUUID.equals("sameUser")) {
			postData = postRepo.findAllPostDataById(userData.get().getUserId(), cursor, pageable,
					userData.get().getUserId());
		} else {
			Optional<User> currentUser = userRepo.findByUuid(userUUID);
			if (currentUser.isEmpty()) {
				ApiResponse apiResponse = new ApiResponse(CommonMessages.FAILED, MSG.USER_DETAILS);
				return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
			}

			postData = postRepo.findAllPostDataById(userData.get().getUserId(), cursor, pageable,
					currentUser.get().getUserId());

		}
		// Check if post data is empty
		if (postData.isEmpty()) {
			ApiResponse apiResponse = new ApiResponse(CommonMessages.FAILED, MSG.POST_NO_MORE_POST);
			return new ResponseEntity<>(apiResponse, HttpStatus.NO_CONTENT);
		}
		long totalPost = postRepo.countPostByUserId(userData.get().getUserId());
		long totalFriends = friendRequestRepo.countFriendsByUserId(userData.get().getUserId());
		// Convert time to ISO format
		commonFunctions.updatePostTimeAgo(postData.getContent());
		// Determine the next cursor based on the last item in the current page
		Long nextCursor = postData.hasNext() ? postData.getContent().get(postData.getContent().size() - 1).getPostId()
				: null;
		response.put("homePostData", postData.getContent());
		response.put(MSG.TOTAL_ELEMENTS, postData.getTotalElements());
		response.put("hasNextPage", postData.getSize() == size); // Checks if there's another page
		response.put("nextCursor", nextCursor); // Sets the next cursor to continue from here
		response.put("totalPosts", totalPost);
		response.put("totalFriends", totalFriends);

		// Cache the data in Redis with the specified TTL
		if ("sameUser".equals(targetUUID)) {
			redisTemplate.opsForValue().set(redisKey, response, 1, TimeUnit.DAYS); // 1 day TTL for same user
		} else {
			redisTemplate.opsForValue().set(redisKey, response, 2, TimeUnit.MINUTES); // 2 minutes TTL for target user
		}
		log.info("Cache added for user timeline");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Transactional
	@Override
	public ResponseEntity<Object> findUserByName(String userName) {
		log.info("***** Inside - UserServiceImpl - findUserByName *****");
		Map<Object, Object> response = new HashMap<>();

		// Split the userName to check if it contains both first and last names
		String[] nameParts = userName.split(" ");
		List<User> userData;

		if (nameParts.length > 1) {
			// Handle full name (e.g., "komal kumari")
			String firstName = nameParts[0];
			String lastName = nameParts[1];

			// Find users matching both first and last name together
			userData = userRepo.findByUserFirstNameOrUserLastName(firstName);

			// Now filter the userData further to check if lastName is also a match for any
			// users
			userData = userData.stream()
					.filter(user -> user.getUserLastName().toLowerCase().contains(lastName.toLowerCase()))
					.collect(Collectors.toList());
		} else {
			// Handle single name (e.g., "komal")
			userData = userRepo.findByUserFirstNameOrUserLastName(userName);
		}

		if (userData == null || userData.isEmpty()) {
			response.put("status", CommonMessages.FAILED);
			response.put("message", MSG.USER_SEARCH_USER_NOT_FOUND);
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		} else {
			// Convert entity to model to show specific fields
			List<UserSearchModel> userModelData = userData.stream().map(user -> {
				UserSearchModel userModel = modelMapper.map(user, UserSearchModel.class);
				userModel.setUserFirstName(user.getUserFirstName());
				userModel.setUserLastName(user.getUserLastName());
				userModel.setUserProfileImagePath(user.getUserProfileImagePath());
				userModel.setUserUUID(user.getUuid());
				return userModel;
			}).collect(Collectors.toList());

			response.put("searchedData", userModelData);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@Transactional
	@Override
	public ResponseEntity<Object> changeUserDetails(long userId, UserUpdateModel userDetails) {
		log.info("***** Inside UserServiceImpl - changeUserDetails");
		Map<Object, Object> response = new HashMap<>();
		User userData = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User with the Id " + userId + " not found"));
		if (userDetails.getOrgId() != 0) {
			Organisation orgDetails = organisationRepo.findById(userDetails.getOrgId())
					.orElseThrow(() -> new ResourceNotFoundException(
							"Organisation with the Id " + userDetails.getOrgId() + " not found"));
			userData.setOrg(orgDetails);
		}

		if (userDetails.getUserDateOfBirth() != null && !userDetails.getUserDateOfBirth().trim().isEmpty()) {
			userData.setUserDateOfBirth(userDetails.getUserDateOfBirth());
		}
		if (userDetails.getUserAddress() != null && !userDetails.getUserAddress().trim().isEmpty()) {
			userData.setUserAddress(userDetails.getUserAddress());
		}
		if (userDetails.getUserFirstName() != null && !userDetails.getUserFirstName().trim().isEmpty()) {
			userData.setUserFirstName(userDetails.getUserFirstName());
		}
		if (userDetails.getUserLastName() != null && !userDetails.getUserLastName().trim().isEmpty()) {
			userData.setUserLastName(userDetails.getUserLastName());
		}

		commonFunctions.updateImage(userData::setUserProfileImagePath, userDetails.getUserProfileImagePath());
		commonFunctions.updateImage(userData::setUserCoverProfileImagePath, userDetails.getUserCoverProfileImagePath());
		String newPassword = userDetails.getUserPassword();
		if (newPassword != null && !newPassword.isEmpty()) {
			userData.setUserpassword(encoder.encode(newPassword));
		}

		userRepo.save(userData);

		// remove cache
		redisTemplate.delete("userAboutData:" + userData.getUuid());

		response.put("status", CommonMessages.SUCCESS);
		response.put("message", MSG.USER_UPDATE_DETAILS);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

//	private void updateImage(Consumer<String> setImagePath, MultipartFile multipartFile) {
//		if (multipartFile != null && !multipartFile.isEmpty()) {
//			try {
//				String imageUrl = commonFunctions.saveImageToServer(multipartFile);
//				setImagePath.accept(imageUrl);
//			} catch (Exception e) {
//				log.error("Error saving image: {}", e.getMessage());
//				// Handle the error as needed (e.g., set a default image or notify the user)
//			}
//		}
//	}

	@Transactional
	@Override
	public ResponseEntity<Object> removeUser(long userId) {
		log.info("***** Inside UserServiceImpl - removeUser *****");
		Map<Object, Object> response = new HashMap<>();
		User userdata = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User with the Id " + userId + " not found"));
		Group groupDetails = groupRepo.findByUserUserId(userId);
		if (groupDetails != null) {
			groupDetails.getMembers().clear();
			groupRepo.save(groupDetails);
		}
		// Remove user from associated pages if any
		Optional<Pages> pageData = pagesRepo.findByUserUserId(userId);
		pageData.ifPresent(page -> {
			page.getMembers().remove(userdata); // Remove user directly from page members
			pagesRepo.save(page); // Save changes to pages
		});
		List<Posts> postData = postRepo.findByUserUserId(userId);
		for (Posts posts : postData) {
			posts.getHashtags().clear();
			postRepo.save(posts);
		}
		userRepo.delete(userdata);
		response.put("status", CommonMessages.SUCCESS);
		response.put("message", MSG.USER_DELETE_SUCCESSFUL);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Transactional
	@Override
	public ResponseEntity<Object> sendEmailToUpdatePassword(String emailId) {
		log.info("***** Inside UserServiceImpl - sendEmailToUpdatePassword *****");
		Map<Object, Object> response = new HashMap<>();
		User checkEmail = userRepo.findByUserEmail(emailId);
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		if (checkEmail == null) {
			response.put("status", CommonMessages.FAILED);
			response.put("message", MSG.USER_DATA_NOT_FOUND);
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		} else {

			Optional<ForgetPassword> checkOldEmail = forgetPasswordRepo.findByUserEmail(emailId);
			if (checkOldEmail.isPresent()) {
				try {
					String otp = UUID.randomUUID().toString();
					mailMessage.setTo(emailId);
					mailMessage.setSubject("Change Password Link from Stri Shakti.");

					mailMessage.setFrom("gpkp166014@gmail.com");

					mailMessage.setText(
							"To reset your password, click the link:" + appUrl + "/reset-password?token=" + otp);
					mailSender.send(mailMessage);

					// save in db
					checkOldEmail.get().setOtp(otp);
					forgetPasswordRepo.save(checkOldEmail.get());

					response.put("message", MSG.FP_EMAIL_SEND_SUCCESSFULL);
					response.put("status", CommonMessages.SUCCESS);
					return ResponseEntity.status(HttpStatus.OK).body(response);
				} catch (Exception e) {
					log.error("Failed to send email to: {}", emailId, e);
					response.put("message", "Failed to send email.");
					response.put("status", "ERROR");
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
				}
			} else {
				try {
					ForgetPassword fpass = new ForgetPassword();
					String otp = UUID.randomUUID().toString();
					mailMessage.setTo(emailId);
					mailMessage.setSubject("Change Password OTP from Stri Shakti.");
					mailMessage.setFrom("gpkp166014@gmail.com");

					mailMessage.setText(
							"To reset your password, click the link:" + appUrl + "/reset-password?token=" + otp);
					mailSender.send(mailMessage);

					// save in db
					fpass.setUserEmail(emailId);
					fpass.setOtp(otp);
					forgetPasswordRepo.save(fpass);

					response.put("message", MSG.FP_EMAIL_SEND_SUCCESSFULL);
					response.put("status", CommonMessages.SUCCESS);
					return ResponseEntity.status(HttpStatus.OK).body(response);
				} catch (Exception e) {
					log.error("Failed to send email to: {}", emailId, e);
					response.put("message", "Failed to send email.");
					response.put("status", "ERROR");
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
				}
			}
		}
	}

	@Transactional
	@Override
	public ResponseEntity<Object> validateUserPassword(String password, String otp) {
		log.info("***** Inside UserServiceImpl - sendEmailToUpdatePassword *****");
		Map<Object, Object> response = new HashMap<>();

		Optional<ForgetPassword> forgetPasswordData = forgetPasswordRepo.findByOtp(otp);
		if (forgetPasswordData.isPresent()) {
			User checkEmail = userRepo.findByUserEmail(forgetPasswordData.get().getUserEmail());
			checkEmail.setUserpassword(encoder.encode(password));
			userRepo.save(checkEmail);

			// delete otp
			forgetPasswordRepo.delete(forgetPasswordData.get());

			response.put("status", CommonMessages.SUCCESS);
			response.put("message", MSG.FP_PASS_UPDATE_SUCCESSFUL);
			return new ResponseEntity<>(response, HttpStatus.OK);

		} else {
			response.put("status", CommonMessages.FAILED);
			response.put("message", MSG.FP_INVALID_OTP);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

	}

	@Transactional
	@Override
	public ResponseEntity<Object> getUserInfoData(long userId, long targetUserId) {
		log.info("***** Inside - UserServiceImpl - getUserInfoData *****");

		Map<Object, Object> response = new HashMap<>();
		Map<String, Object> userData = new HashMap<>();
		User userDetails = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User with the Id " + userId + " not found"));
		User targetUserDetails = userRepo.findById(targetUserId)
				.orElseThrow(() -> new ResourceNotFoundException("User with the Id " + targetUserId + " not found"));
		// userDetails
		long totalPost = postRepo.countPostByUserId(userId);
		long totalFriends = friendRequestRepo.countFriendsByUserId(userId);
		userData.put("userFullName", userDetails.getUserFirstName() + " " + userDetails.getUserLastName());
		userData.put("userGender", userDetails.getUserGender());
		userData.put("userProfileImage", userDetails.getUserProfileImagePath());
		userData.put("userCoverProfileImage", userDetails.getUserCoverProfileImagePath());
		userData.put("userBirthDate", userDetails.getUserDateOfBirth());
		userData.put("userLocation", userDetails.getUserAddress());
		userData.put("userEmail", userDetails.getUserEmail());
		userData.put("totalPosts", totalPost);
		userData.put("totalFriends", totalFriends);

		// Determine friend request status
		Optional<FriendRequest> friendRequest = friendRequestRepo.findBySenderAndReceiver(userDetails,
				targetUserDetails);
		try {
			userData.put("friendRequestId", friendRequest.get().getUserFriendRequestId());
		} catch (Exception e) {

		}
		// Filter out records where the target user is the receiver
		friendRequest.stream().filter(fr -> !fr.getReceiver().equals(targetUserDetails)).collect(Collectors.toList());

		String status;
		if (friendRequest.isEmpty()) {

			status = "NOT SENT";
		} else if (!friendRequest.get().getStatus().equalsIgnoreCase("approved")) {

			status = "PENDING";
		} else {

			status = "APPROVED";
		}

		// friendList
		List<FriendsList> friendList = friendRequestServiceImpl.fetchFriendsList(targetUserId);
		response.put("userDetails", userData);
		response.put("friendRequestStatus", status);
		response.put("friendList", friendList);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Object> getAllActiveUsers() {
		Map<Object, Object> response = new HashMap<>();
		List<User> activeUsers=userRepo.getAllActiveUsers();
			if (activeUsers == null || activeUsers.isEmpty()) {
				response.put(CommonMessages.STATUS, CommonMessages.FAILED);
				response.put(CommonMessages.MESSAGE, MSG.POST_NOT_FOUND);
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			} else {
				long totalActiveUsers = userRepo.countActiveUsers();
				response.put(CommonMessages.TOTAL_ELEMENTS,totalActiveUsers);
				response.put("status", CommonMessages.SUCCESS);
				response.put("message", MSG.ACTIVE_USER);
				response.put("activeUsers", activeUsers);
				return new ResponseEntity<>(response, HttpStatus.OK);
//		response.put("activeUsers", activeUsers);
//		return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@Override
	public ResponseEntity<Object> districtwiseCount() {
	    List<Object[]> results = userRepo.getDistrictWiseUserCount();
	    Map<String, Object> response = new HashMap<>();
	    Map<String, Long> districtCountMap = new HashMap<>();

	    for (Object[] result : results) {
	        String districtName = (String) result[0];
	        Long count = (Long) result[1];
	        districtCountMap.put(districtName, count);
	    }

	    response.put("status", "SUCCESS");
	    response.put("message", "District-wise user registration count retrieved successfully.");
	    response.put("data", districtCountMap);

	    return ResponseEntity.ok(response);
	}

//	List<PostIndexPage> indexData = postData.getContent();for(
//	PostIndexPage postIndex:indexData)
//	{
//		// convert the time in ISO
//		String timeAgo = DateTimeUtil.convertToTimeAgo(postIndex.getPostCreatedAt());
//		postIndex.setPostUploadedAt(timeAgo);
//	}

}

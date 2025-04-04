package com.ymanch.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ymanch.entity.District;
import com.ymanch.entity.FriendRequest;
import com.ymanch.entity.Group;
import com.ymanch.entity.Organisation;
import com.ymanch.entity.Pages;
import com.ymanch.entity.Posts;
import com.ymanch.entity.User;
import com.ymanch.exception.ResourceNotFoundException;
import com.ymanch.helper.CommonFunctions;
import com.ymanch.helper.CommonMessages;
import com.ymanch.helper.JwtHelper;
import com.ymanch.helper.Enums.PostOwnerType;
import com.ymanch.mapper.UserMapper;
import com.ymanch.model.AdminPostAdvertisementModel;
import com.ymanch.model.AdminUserDetails;
import com.ymanch.model.CustomResponseModel;
import com.ymanch.model.PostModel;
import com.ymanch.model.SendMessage;
import com.ymanch.model.UserDetails;
import com.ymanch.model.UserLineChartDetail;
import com.ymanch.model.admin.AdminModel.DistrictStats;
import com.ymanch.repository.DistrictRepository;
import com.ymanch.repository.FriendRequestRepository;
import com.ymanch.repository.GroupRepository;
import com.ymanch.repository.OrganisationRepository;
import com.ymanch.repository.PagesRepository;
import com.ymanch.repository.PostRepository;
import com.ymanch.repository.UserRepository;
import com.ymanch.repository.Page.PageUserRepository;
import com.ymanch.service.AdminService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AdminServiceImpl implements AdminService {

	private CommonMessages MSG;
	private UserRepository userRepo;// admin data is present
	private PostRepository postRepo;
	private PageUserRepository pageUserRepo;
	private UserMapper mapper;
	private DistrictRepository districtRepo;
	private JwtHelper jwtHelperPagesRepository;
	private PagesRepository pagesRepo;
	private GroupRepository groupRepo;
	private OrganisationRepository organisationRepo;
	private CommonFunctions commonFunctions;
	private FriendRequestRepository friendRequestRepo;
	private RedisTemplate<String, Object> redisTemplate;

	public AdminServiceImpl(CommonMessages mSG, UserRepository userRepo, PostRepository postRepo,
			PageUserRepository pageUserRepo, UserMapper mapper, DistrictRepository districtRepo,
			JwtHelper jwtHelperPagesRepository, PagesRepository pagesRepo, GroupRepository groupRepo,
			OrganisationRepository organisationRepo, CommonFunctions commonFunctions,
			FriendRequestRepository friendRequestRepo, RedisTemplate<String, Object> redisTemplate) {
		super();
		MSG = mSG;
		this.userRepo = userRepo;
		this.postRepo = postRepo;
		this.pageUserRepo = pageUserRepo;
		this.mapper = mapper;
		this.districtRepo = districtRepo;
		this.jwtHelperPagesRepository = jwtHelperPagesRepository;
		this.pagesRepo = pagesRepo;
		this.groupRepo = groupRepo;
		this.organisationRepo = organisationRepo;
		this.commonFunctions = commonFunctions;
		this.friendRequestRepo = friendRequestRepo;
		this.redisTemplate = redisTemplate;
	}

	@Transactional
	@Override
	public ResponseEntity<Object> getUserActivityCounts() {
		if (log.isInfoEnabled()) {
			log.info("***** Inside AdminServiceImpl - getUserActivityCounts *****");
		}
		Map<Object, Object> response = new HashMap<>();
		// Fetch count of users registered since 30 days ago

		try {
			long totalActiveUsersLastThirtyDays = userRepo.countUsersRegisteredSince();
			long totalActiveUsers = userRepo.countActiveUsers();
			long totalPosts = postRepo.countPostAddedSince();
			long todaysPosts = postRepo.countTodaysPost();

			response.put("totalActiveUser", totalActiveUsers);
			response.put("lastThirtyDaysActiveUser", totalActiveUsersLastThirtyDays);
			response.put("lastThirtyDaysPosts", totalPosts);
			response.put("todayPostCount", todaysPosts);

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			response.put("message", "An error occurred while fetching user activity counts");
			response.put("status", CommonMessages.FAILED);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@Transactional
	@Override
	public ResponseEntity<Object> getUserLineChartDetails() {
		if (log.isInfoEnabled()) {
			log.info("***** Inside AdminServiceImpl - getUserLineChartDetails *****");
		}
		Map<Object, Object> response = new HashMap<>();
		List<Object[]> results = userRepo.findUserRegistrationCountsByDateAndDistrictNative();

		if (results.isEmpty() || results == null) {
			response.put("message", "An error occurred while fetching user activity counts");
			response.put("status", CommonMessages.FAILED);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		} else {
			List<UserLineChartDetail> userLineChartDetails = results.stream()
					.map(result -> new UserLineChartDetail(((java.sql.Date) result[0]).toLocalDate(),
							(String) result[1], ((Number) result[2]).longValue()))
					.collect(Collectors.toList());

			response.put("data", userLineChartDetails);
			return ResponseEntity.status(HttpStatus.OK).body(response);
		}

	}

	/*
	 * @Override public ResponseEntity<Object> getAllUserDetails() throws Exception
	 * { log.info("***** Inside - AdminServiceImpl - getAllUserDetails *****");
	 * Map<Object, Object> response = new HashMap<>(); List<AdminUserDetails>
	 * userDetails = adminServiceDao.getAdminUserDetails();
	 * 
	 * if (userDetails.isEmpty() || userDetails == null) { response.put("status",
	 * MSG.FAILED); response.put("message", MSG.USER_DETAILS); return
	 * ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); } else {
	 * response.put("userDetails", userDetails); return
	 * ResponseEntity.status(HttpStatus.OK).body(response); }
	 * 
	 * }
	 */

	@Transactional
	@Override
	public ResponseEntity<Object> getAllPostDetails(int page, int size) {
		if (log.isInfoEnabled()) {
			log.info("***** Inside AdminServiceImpl - getAllPostDetails *****");
		}
		Pageable pageable = PageRequest.of(page, size);
		Page<PostModel> posts = postRepo.findPosts(pageable);
		if (posts.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} else {
			CustomResponseModel<PostModel> response = new CustomResponseModel<>(posts.getContent(),
					posts.getTotalPages(), posts.getNumber(), posts.getTotalElements(), posts.getSize(),
					posts.hasNext(), posts.getNumber() + 1);
			return ResponseEntity.status(HttpStatus.OK).body(response);
		}
	}

	@Transactional
	@Override
	public ResponseEntity<Object> getAllUserDetailsWithPagination(String firstName, long districtId, int page, int size)
			throws Exception {
		if (log.isInfoEnabled()) {
			log.info("***** Inside AdminServiceImpl - getAllUserDetailsWithPagination *****");
		}
		Map<Object, Object> response = new HashMap<>();
		Pageable pageable = PageRequest.of(page, size);
		Page<Object[]> resultsPage = pageUserRepo.findAllUsersWithPagination(firstName, districtId, pageable);
		List<AdminUserDetails> userDetails = resultsPage.getContent().stream().map(mapper::map)
				.collect(Collectors.toList());
		
		for(AdminUserDetails a :userDetails)
		{
			System.out.println(a.getUserDateOfBirth());
		}
		if (userDetails.isEmpty() || userDetails == null) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} else {
			response.put("userDetails", userDetails);
			response.put(MSG.TOTAL_PAGES, resultsPage.getTotalPages());
			response.put(MSG.CURRENT_PAGE, resultsPage.getNumber());
			response.put(MSG.TOTAL_ELEMENTS, resultsPage.getTotalElements());
			response.put(MSG.PAGE_SIZE, resultsPage.getSize());
			response.put(MSG.HAS_NEXT_PAGE, resultsPage.hasNext());
			response.put(MSG.NEXT_PAGE_NO, page + 1);
			return ResponseEntity.status(HttpStatus.OK).body(response);
		}
	}

	@Transactional
	@Override
	public ResponseEntity<Object> storeAdminAdvertisementPost(long adminId, AdminPostAdvertisementModel postModel,
			long district) {
		if (log.isInfoEnabled()) {
			log.info("***** Inside AdminServiceImpl - storeAdminAdvertisementPost *****");
		}
		Map<Object, Object> response = new HashMap<>();
		User userdata = userRepo.findById(adminId)
				.orElseThrow(() -> new ResourceNotFoundException("Admin with the Id " + adminId + " not found"));
		/* get district details */
		District diDetails = districtRepo.findById(district)
				.orElseThrow(() -> new ResourceNotFoundException("District with the Id" + district + " not found"));
		Posts post = new Posts();
		post.setPostName(postModel.getPostName());
		post.setPostType(postModel.getPostType());
		post.setVideoThumbnailUrl(postModel.getVideoThumbnailUrl());
		post.setAdvertisementDescription(postModel.getAdvertisementDescription());
		// Save the image file to the server (GoDaddy VPS in your case)
		if (postModel.getSelectFile() != null && !postModel.getSelectFile().isEmpty()) {
			try {
				// Get MIME type of the uploaded file
				String contentType = postModel.getSelectFile().getContentType();

				if (contentType != null) {
					// Check if the file is a video (based on MIME type and file extension)
					if (contentType.startsWith("video")) {
						String fileName = postModel.getSelectFile().getOriginalFilename();
						if (fileName != null && (fileName.endsWith(".mp4") || fileName.endsWith(".avi")
								|| fileName.endsWith(".mov") || fileName.endsWith(".mkv"))) {
							// Valid video format based on MIME type and extension
							try {
								// Generate video thumbnail and store it
								String videoThumbnailUrl = commonFunctions
										.generateVideoThumbnail(postModel.getSelectFile());
								post.setVideoThumbnailUrl(videoThumbnailUrl); // Store the video thumbnail URL

//			                        // Optionally, save the video file to server
								String imageUrl = commonFunctions.saveImageToServer(postModel.getSelectFile());
								post.setPostImageUrl(imageUrl);
							} catch (Exception e) {
								e.printStackTrace(); // Handle exception properly (e.g., logging)
							}
						} else {
							System.out.println("Uploaded file is not a valid video format");
						}
					}
					// Check if the file is an image
					else if (contentType.startsWith("image")) {

						String fileName = postModel.getSelectFile().getOriginalFilename();
						if (fileName != null
								&& (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png")
										|| fileName.endsWith(".gif") || fileName.endsWith(".bmp"))) {
							// Valid image format based on MIME type and extension

							try {
								// Optionally, save the image file to server
								String imageUrl = commonFunctions.saveImageToServer(postModel.getSelectFile());
								post.setPostImageUrl(imageUrl); // Store the image URL
							} catch (Exception e) {
								e.printStackTrace(); // Handle exception properly (e.g., logging)
							}
						} else {
							System.out.println("Uploaded file is not a valid image format.");
						}
					}
					// If it's neither a video nor an image
					else {
						System.out.println("The uploaded file is neither a video nor an image.");
					}
				}
			} catch (Exception e) {
				e.printStackTrace(); // Handle any unexpected errors properly
			}
		}
//		post.setPostImageUrl(postModel.getPostImageUrl());
		post.setUser(userdata);

		System.out.println(postModel.getPostCategory()+"******************** at store");
		Set<PostOwnerType> supAdminCategories = Set.of(PostOwnerType.SUP_ADMIN, PostOwnerType.SUP_ADMIN_ANNOUNCEMENT);

		String categoryString = postModel.getPostCategory(); // e.g., "ADMIN"
		PostOwnerType postOwnerType = PostOwnerType.valueOf(categoryString);
		
		post.setPostOwnerType(postOwnerType.equals(PostOwnerType.ADMIN) ? PostOwnerType.ADMIN
				: supAdminCategories.contains(postModel.getPostCategory()) ? PostOwnerType.SUP_ADMIN
						: PostOwnerType.SUP_ADMIN_ANNOUNCEMENT);
		post.setDistrict(diDetails);
		postRepo.save(post);
		
		response.put("status", CommonMessages.SUCCESS);
		response.put("message", MSG.POST_ADD_SUCCESSFULL);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@Transactional
	@Override
	public ResponseEntity<Object> logInData(String tokenHeader) {
		if (log.isInfoEnabled()) {
			log.info("***** Inside AdminServiceImpl - logInData *****");
		}
		Map<Object, Object> response = new HashMap<>();
		// Extract the token by removing the "Bearer " prefix
		String token = tokenHeader.replace("Bearer ", "");

		// Extract specific data from the claims
		Long userId = jwtHelperPagesRepository.extractUserId(token);
		User userData = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User with the Id " + userId + " not found"));

		// Build the response
		response.put("status", CommonMessages.SUCCESS);
		response.put("userEmail", userData.getUserEmail());
		response.put("userId", userData.getUserId());
		response.put("userFirstName", userData.getUserFirstName());
		response.put("userLastName", userData.getUserLastName());
		if (userData.getUserRole().equals("ROLE_USER")) {
			response.put("isAdmin", false);
			response.put("message", MSG.USER_LOGIN_SUCCESSFUL);
		} else {
			response.put("isAdmin", true);
			response.put("message", MSG.ADMIN_LOGIN_SUCCESSFUL);
		}
		response.put("userProfileImage", userData.getUserProfileImagePath());
		response.put("userCoverImage", userData.getUserCoverProfileImagePath());
		response.put("districtName", userData.getDistrict().getDistrictName());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Transactional
	@Override
	public ResponseEntity<Object> stats(HttpServletRequest requestHeaderToken) {
		if (log.isInfoEnabled()) {
			log.info("***** Inside AdminServiceImpl - stats *****");
		}

		// Fetch all districts along with their IDs
		List<District> allDistricts = districtRepo.findAllByOrderByDistrictIdAsc(); // Ensure this method fetches the
																					// districts sorted by ID

		// Fetch the statistics from the repository
		List<Object[]> statsList = districtRepo.getStatsForAllDistricts();

		// Create map for district stats, initializing all districts with zero values
		Map<String, DistrictStats> statsMap = new LinkedHashMap<>(); // Use LinkedHashMap to maintain the order

		// Initialize an aggregated stats object to calculate totals for "All"
		DistrictStats totalStats = new DistrictStats();
		totalStats.setTotalPosts(0);
		totalStats.setTotalUsers(0);
		totalStats.setTotalDisputes(0);
		totalStats.setTotalGroups(0);
		totalStats.setTotalPages(0);

		// Initialize every district with default stats (0s)
		for (District district : allDistricts) {
			DistrictStats defaultStats = new DistrictStats();
			defaultStats.setTotalPosts(0);
			defaultStats.setTotalUsers(0);
			defaultStats.setTotalDisputes(0);
			defaultStats.setTotalGroups(0);
			defaultStats.setTotalPages(0);
			statsMap.put(district.getDistrictName(), defaultStats); // Use district name as key
		}

		// Update the map with actual data from the statsList
		for (Object[] result : statsList) {
			String districtName = (String) result[0]; // Assuming districtName is at index 0
			long totalPosts = (long) result[1];
			long totalUsers = (long) result[2];
			long totalDisputes = (long) result[3];
			long totalGroups = (long) result[4];
			long totalPages = (long) result[5];

			// Update the district stats if the district already exists in the map
			DistrictStats stats = statsMap.get(districtName);
			if (stats != null) {
				stats.setTotalPosts(totalPosts);
				stats.setTotalUsers(totalUsers);
				stats.setTotalDisputes(totalDisputes);
				stats.setTotalGroups(totalGroups);
				stats.setTotalPages(totalPages);
			}

			// Update the aggregated totals for the "All" field
			totalStats.setTotalPosts(totalStats.getTotalPosts() + totalPosts);
			totalStats.setTotalUsers(totalStats.getTotalUsers() + totalUsers);
			totalStats.setTotalDisputes(totalStats.getTotalDisputes() + totalDisputes);
			totalStats.setTotalGroups(totalStats.getTotalGroups() + totalGroups);
			totalStats.setTotalPages(totalStats.getTotalPages() + totalPages);
		}
		// Add the aggregated totals under "All"
		statsMap.put("All", totalStats);
		return new ResponseEntity<>(statsMap, HttpStatus.OK);
	}

	@Transactional
	@Override
	public ResponseEntity<Object> updateUUID() {
		if (log.isInfoEnabled()) {
			log.info("***** Inside AdminServiceImpl - updateUUID *****");
		}
		Map<Object, Object> response = new HashMap<>();
		// for user
		List<User> usersWithNullOrEmptyUuid = userRepo.findByUuidIsNullOrEmpty();
		for (User user : usersWithNullOrEmptyUuid) {
			user.setUuid(UUID.randomUUID().toString()); // Generate a new UUID
			userRepo.save(user); // Save the updated user
		}
		// for pages
		List<Pages> pagesWithNullOrEmptyUuid = pagesRepo.findByPuuidIsNullOrEmpty();
		for (Pages page : pagesWithNullOrEmptyUuid) {
			page.setPuuid(UUID.randomUUID().toString()); // Generate a new UUID
			pagesRepo.save(page); // Save the updated user
		}
		// for groups
		List<Group> groupWithNullOrEmptyUuid = groupRepo.findByGuuidIsNullOrEmpty();
		for (Group group : groupWithNullOrEmptyUuid) {
			group.setGuuid(UUID.randomUUID().toString()); // Generate a new UUID
			groupRepo.save(group); // Save the updated user
		}
		response.put(CommonMessages.MESSAGE, "UUID updated");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Transactional
	@Override
	public ResponseEntity<Object> fetchPosts(long userId, int page, int size) {
		if (log.isInfoEnabled()) {
			log.info("***** Inside AdminServiceImpl - fetchPosts *****");
		}
		userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User with the Id " + userId + " not found"));
		Pageable pageable = PageRequest.of(page, size);
		Page<PostModel> posts = postRepo.findPostsByUserUserId(userId, pageable);
		if (posts.isEmpty()) {
			throw new ResourceNotFoundException("No posts found for user with Id " + userId);
		} else {
			CustomResponseModel<PostModel> response = new CustomResponseModel<>(posts.getContent(),
					posts.getTotalPages(), posts.getNumber(), posts.getTotalElements(), posts.getSize(),
					posts.hasNext(), posts.getNumber() + 1);
			return ResponseEntity.status(HttpStatus.OK).body(response);
		}
	}

	@Transactional
	@Override
	public ResponseEntity<Object> storeOrgDetails(@Valid Organisation org) {
		if (log.isInfoEnabled()) {
			log.info("***** Inside AdminServiceImpl - storeOrgDetails *****");
		}
		Map<Object, Object> response = new HashMap<>();
		Optional<Organisation> orgDetails = organisationRepo.findByOrgName(org.getOrgName());
		if (orgDetails.isPresent()) {
			throw new ResourceNotFoundException("Organisation name already present");
		}
		organisationRepo.save(org);
		// not works with @Json View
//		ApiResponse apiResponse = new ApiResponse(CommonMessages.SUCCESS, CommonMessages.ORG_ADD_SUCCESSFUL);
		response.put(CommonMessages.STATUS, CommonMessages.SUCCESS);
		response.put(CommonMessages.MESSAGE, CommonMessages.ORG_ADD_SUCCESSFUL);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Override
	public ResponseEntity<Object> retrieveOrgDetails() {
		if (log.isInfoEnabled()) {
			log.info("***** Inside AdminServiceImpl - retrieveOrgDetails *****");
		}
		List<Organisation> orgDetails = organisationRepo.findAll();
		CustomResponseModel<Organisation> response = new CustomResponseModel<>(orgDetails, 0, 0, 0, 0, false, 0);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Transactional
	@Override
	public ResponseEntity<Object> updateUserFullname() {
		if (log.isInfoEnabled()) {
			log.info("***** Inside AdminServiceImpl - updateUUID *****");
		}
		Map<Object, Object> response = new HashMap<>();
//		// for user
//		List<User> usersWithFullNameNullOrEmpty = userRepo.findByFullNameNullOrEmpty();
//		for (User user : usersWithFullNameNullOrEmpty) {
//	        // Check if userFirstName or userLastName is null or empty before concatenating
//	        String fullName = (user.getUserFirstName() != null ? user.getUserFirstName() : "") +" "+
//	                          (user.getUserLastName() != null ? user.getUserLastName() : "");
//	        
//	        // Set the fullName if it’s empty or null
//	     
//	            user.setFullName(fullName);  // Concatenate first and last names
//	            userRepo.save(user);  // Save the updated user
//	        }
		// Perform the batch update
		userRepo.updateFullNames();
		response.put(CommonMessages.MESSAGE, "Full name updated");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Transactional
	@Override
	public ResponseEntity<Object> sendAdminMessage(SendMessage message, Long adminId) {
		if (log.isInfoEnabled()) {
			log.info("***** Inside AdminServiceImpl - sendAdminMessage *****");
		}
		Map<String, Object> response = new HashMap<>();
		User senderData = userRepo.findById(adminId)
				.orElseThrow(() -> new ResourceNotFoundException("Admin with the Id " + adminId + " not found"));
		List<UserDetails> distWiseDetails = userRepo.findUsersByDistrictId(message.getDistrictId());
		List<FriendRequest> data = new ArrayList<>();
		for (UserDetails userDetails : distWiseDetails) {
			Optional<User> receiverData = userRepo.findById(userDetails.getUserId());
			FriendRequest ob = new FriendRequest();
			ob.setStatus("APPROVED");
			ob.setSender(senderData);
			ob.setReceiver(receiverData.get());
			data.add(ob);
			redisTemplate.delete("friendsList:" + userDetails.getUserId());
		}
		friendRequestRepo.saveAll(data);

		response.put(CommonMessages.STATUS, CommonMessages.SUCCESS);
		response.put(CommonMessages.MESSAGE, "Friend added successfully");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}

package com.ymanch.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ymanch.dao.GroupServiceDao;
import com.ymanch.entity.Group;
import com.ymanch.entity.User;
import com.ymanch.exception.ResourceNotFoundException;
import com.ymanch.helper.CommonFunctions;
import com.ymanch.helper.CommonMessages;
import com.ymanch.helper.DateTimeUtil;
import com.ymanch.mapper.GroupMapper;
import com.ymanch.model.CustomResponseModel;
import com.ymanch.model.FriendsList;
import com.ymanch.model.GroupModel;
import com.ymanch.model.PageGroupUpdateModel;
import com.ymanch.model.PostIndexPage;
import com.ymanch.model.UserDetails;
import com.ymanch.repository.GroupRepository;
import com.ymanch.repository.PostRepository;
import com.ymanch.repository.UserRepository;
import com.ymanch.service.GroupService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GroupServiceImpl implements GroupService {
	private Map<Object, Object> response;

	private CommonMessages commonMessages;
	private GroupRepository groupRepo;
	private UserRepository userRepo;
	private GroupMapper groupMapper;
	private GroupServiceDao groupServiceDao;
	private PostRepository postRepo;
	private CommonFunctions commonFunctions;

	public GroupServiceImpl(GroupRepository groupRepo, UserRepository userRepo, CommonMessages commonMessages,
			GroupMapper groupMapper, GroupServiceDao groupServiceDao, PostRepository postRepo,
			CommonFunctions commonFunctions) {
		super();
		this.groupRepo = groupRepo;
		this.userRepo = userRepo;
		this.commonMessages = commonMessages;
		this.groupMapper = groupMapper;
		this.groupServiceDao = groupServiceDao;
		this.postRepo = postRepo;
		this.commonFunctions = commonFunctions;
	}

	@Transactional
	@Override
	public ResponseEntity<Object> createGroup(long adminUserId, @Valid Group group, List<Long> membersIds) {
		log.info("***** Inside - GroupServiceImpl - createGroup *****");
		response = new HashMap<>();

		// Retrieve the admin user from the repository
		User admin = userRepo.findById(adminUserId).orElseThrow(
				() -> new ResourceNotFoundException("Admin user with the Id" + adminUserId + " not found"));
		// Fetch all the users based on the provided member IDs
		if (membersIds != null && !membersIds.isEmpty()) {
			List<User> members = userRepo.findAllById(membersIds);
			group.setMembers(members);
		}
		group.setUser(admin);

		groupRepo.save(group);
		response.put(CommonMessages.STATUS, CommonMessages.SUCCESS);
		response.put(CommonMessages.MESSAGE, commonMessages.GROUP_CREATED_SUCCESSFUL);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Transactional
	@Override
	public ResponseEntity<Object> addMember(long groupId, long adminUserId, List<Long> user) {
		log.info("***** Inside - GroupServiceImpl - addMember *****");
		response = new HashMap<>();

		Group group = groupRepo.findById(groupId)
				.orElseThrow(() -> new ResourceNotFoundException("Group with Id " + groupId + " not found"));

		Group groupAdminDetails = groupRepo.findByUserUserIdAndGroupId(adminUserId, groupId);
		// Verify that the admin is indeed an admin of the group
		if (groupAdminDetails == null) {
			response.put(CommonMessages.STATUS, CommonMessages.FAILED);
			response.put(CommonMessages.MESSAGE, commonMessages.GROUP_ADMIN_NOT_PRESENT);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
		// Collect existing member IDs
		List<Long> existingMemberIds = group.getMembers().stream().map(User::getUserId) // Assuming User has a getId()
				// method
				.collect(Collectors.toList());

		// Check if any of the users are already members of the group
		for (Long userId : user) {

			String userRole = userRepo.getUserByRole(userId); // Assume this method retrieves the user's role

			if (!"ROLE_USER".equals(userRole)) {
				response.put(CommonMessages.STATUS, CommonMessages.FAILED);
				response.put(CommonMessages.MESSAGE, commonMessages.USER_ROLE_CONFLICT);
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
			}

			if (existingMemberIds.contains(userId)) {
				response.put(CommonMessages.STATUS, CommonMessages.FAILED);
				response.put(CommonMessages.MESSAGE, commonMessages.GROUP_USER_ALREADY_IN_GROUP);
				return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
			}
		}

		// Retrieve User entities for the given user IDs
		List<User> newMembers = user.stream()
				.map(userId -> userRepo.findById(userId)
						.orElseThrow(() -> new ResourceNotFoundException("User with Id " + userId + " not found")))
				.collect(Collectors.toList());

		// Add the User entities to the group
		group.getMembers().addAll(newMembers);

		// Save the updated group entity
		groupRepo.save(group);

		response.put(CommonMessages.STATUS, CommonMessages.SUCCESS);
		response.put(CommonMessages.MESSAGE, commonMessages.GROUP_USER_ADDED_SUCCESSFUL);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Transactional
	@Override
	public ResponseEntity<Object> getGroupsByUserId(long userId) {
		log.info("***** Inside - GroupServiceImpl - getGroupsByUserId *****");
		response = new HashMap<>();

		User user = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User with Id " + userId + " not found"));

		// Retrieve groups where the user is a member
		List<Group> groups = groupRepo.findByMembersContaining(user);

		// Also add groups where the user is the admin
		List<Group> adminGroups = groupRepo.findByUser(user);
		for (Group group : adminGroups) {
			if (!groups.contains(group)) {
				groups.add(group);
			}
		}

		if (groups.isEmpty()) {
			response.put(CommonMessages.STATUS, CommonMessages.FAILED);
			response.put(CommonMessages.MESSAGE, commonMessages.GROUP_NO_GROUPS);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}

		// Convert Group entities to GroupDTOs
		List<GroupModel> groupDTOs = groups.stream().map(this::convertToDTO).collect(Collectors.toList());

		response.put("groupDetails", groupDTOs);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	private GroupModel convertToDTO(Group group) {
		GroupModel dto = new GroupModel();
		dto.setGroupId(group.getGroupId());
		dto.setGroupName(group.getGroupName());
		dto.setGroupDescription(group.getGroupDescription());
		dto.setGroupCreatedAt(group.getGroupCreatedAt());
		dto.setGroupCoverProfileImagePath(group.getGroupCoverProfileImagePath());
		dto.setGroupUUID(group.getGuuid());

		// Populate admin details
		User admin = group.getUser();
		if (admin != null) {
			dto.setAdminId(admin.getUserId());
			dto.setAdminUserFirstName(admin.getUserFirstName());
			dto.setAdminUserLastName(admin.getUserLastName());
			dto.setAdminUserProfileImagePath(admin.getUserProfileImagePath());
		}

		return dto;
	}

	@Transactional
	@Override
	public ResponseEntity<Object> deleteGroupsById(long adminUserId, long groupId) {
		log.info("***** Inside - GroupServiceImpl - v *****");
		response = new HashMap<>();

		Group groupDetails = groupRepo.findByUserUserIdAndGroupId(adminUserId, groupId);
		if (groupDetails == null) {
			response.put(CommonMessages.STATUS, CommonMessages.FAILED);
			response.put(CommonMessages.MESSAGE, commonMessages.GROUP_NO_GROUPS);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		} else {
			groupDetails.getMembers().clear();
			groupRepo.save(groupDetails);
			groupRepo.delete(groupDetails);
			response.put(CommonMessages.STATUS, CommonMessages.SUCCESS);
			response.put(CommonMessages.MESSAGE, commonMessages.GROUP_DELETE_SUCCESSFUL);
			return ResponseEntity.status(HttpStatus.OK).body(response);
		}

	}

	@Transactional
	@Override
	public ResponseEntity<Object> getGroupDetails(long userId, String groupUUID, int page, int size) {
		log.info("***** Inside - GroupServiceImpl - getGroupDetails *****");
		response = new HashMap<>();
		Pageable pageable = PageRequest.of(page, size);
		List<Map<String, Object>> grpMembersDetails = new ArrayList<>();
		Group group = groupRepo.findByGuuid(groupUUID)
				.orElseThrow(() -> new ResourceNotFoundException("Group with UUID " + groupUUID + " not found"));
		GroupModel convertedGroupDetails = groupMapper.convertToDto(group);
		for (int i = 0; i < group.getMembers().size(); i++) {
			Map<String, Object> grpMembers = new HashMap<>();
			grpMembers.put("userId", group.getMembers().get(i).getUserId());
			grpMembers.put("userFirstName", group.getMembers().get(i).getUserFirstName());
			grpMembers.put("userlastName", group.getMembers().get(i).getUserLastName());
			grpMembers.put("userProfileImagePath", group.getMembers().get(i).getUserProfileImagePath());
			grpMembers.put("userUUID", group.getMembers().get(i).getUuid());
			grpMembersDetails.add(grpMembers);

		}

//		/* get the remaining friends who have not yet been added to the group */
//		List<FriendsList> friendList = groupServiceDao.getRemainingFriendsList(group.getGroupId(),
//				group.getUser().getUserId());

		/* get posts from groups */
		Page<PostIndexPage> indexDataPage;
		if (userId == group.getUser().getUserId()) {
			indexDataPage = postRepo.findAllGroupPosts(pageable, group.getGroupId(), group.getUser().getUserId(),
					group.getUser().getUserId());
		} else {
			indexDataPage = postRepo.findAllGroupPosts(pageable, group.getGroupId(), group.getUser().getUserId(),
					userId);
		}
		response.put("groupAbout", convertedGroupDetails);
		response.put("currentGroupMembers", grpMembersDetails);
//		response.put("pendingFriends", friendList);
		response.put("postDetails", indexDataPage.getContent());
		response.put(commonMessages.TOTAL_PAGES, indexDataPage.getTotalPages());
		response.put(commonMessages.CURRENT_PAGE, indexDataPage.getNumber());
		response.put(commonMessages.TOTAL_ELEMENTS, indexDataPage.getTotalElements());
		response.put(commonMessages.PAGE_SIZE, indexDataPage.getSize());
		response.put(commonMessages.HAS_NEXT_PAGE, indexDataPage.hasNext());
		response.put(commonMessages.NEXT_PAGE_NO, page + 1);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Transactional
	@Override
	public ResponseEntity<Object> updateGroup(long adminUserId, long groupId, PageGroupUpdateModel group) {
		log.info("***** Inside - GroupServiceImpl - updateGroup *****");
		response = new HashMap<>();
		Group groupDetails = groupRepo.findByUserUserIdAndGroupId(adminUserId, groupId);
		if (groupDetails == null) {
			response.put(CommonMessages.STATUS, CommonMessages.FAILED);
			response.put(CommonMessages.MESSAGE, commonMessages.GROUP_NO_GROUPS);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		} else {
			if (group.getName() == null || group.getName().isEmpty() || group.getName().isBlank()) {
				groupDetails.setGroupName(groupDetails.getGroupName());
			} else {
				groupDetails.setGroupName(group.getName());
			}

			if (group.getDescription() == null || group.getDescription().isEmpty()
					|| group.getDescription().isBlank()) {
				groupDetails.setGroupDescription(groupDetails.getGroupDescription());
			} else {
				groupDetails.setGroupDescription(group.getDescription());
			}
			// Image
			if (group.getCoverImage() != null && !group.getCoverImage().isEmpty()) {
				// Save the image file to the server (GoDaddy VPS in your case)
				commonFunctions.deleteObject(groupDetails.getGroupCoverProfileImagePath());
			}
			commonFunctions.updateImage(groupDetails::setGroupCoverProfileImagePath, group.getCoverImage());
			groupRepo.save(groupDetails);
			response.put(CommonMessages.STATUS, CommonMessages.SUCCESS);
			response.put(CommonMessages.MESSAGE, commonMessages.GROUP_DETAILS_UPDATE_SUCCESSFUL);
			return ResponseEntity.status(HttpStatus.OK).body(response);
		}
	}

	@Transactional
	@Override
	public ResponseEntity<Object> exitGroup(long adminUserId, long groupId, long userId) {
		log.info("***** Inside - GroupServiceImpl - exitGroup *****");
		response = new HashMap<>();
		User user = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User with Id " + userId + " not found"));
		// find group details by admin Id(user Id)
		Group groupDetails = groupRepo.findByUserUserIdAndGroupId(adminUserId, groupId);
		if (groupDetails == null) {
			response.put(CommonMessages.STATUS, CommonMessages.FAILED);
			response.put(CommonMessages.MESSAGE, commonMessages.GROUP_NO_GROUPS);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
		if (!groupDetails.getMembers().contains(user)) {
			response.put(CommonMessages.STATUS, CommonMessages.FAILED);
			response.put(CommonMessages.MESSAGE, commonMessages.GROUP_NO_GROUPS);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		groupDetails.getMembers().remove(user);
		groupRepo.save(groupDetails);
		response.put(CommonMessages.STATUS, CommonMessages.SUCCESS);
		response.put(CommonMessages.MESSAGE, commonMessages.GROUP_USER_REMOVED_FROM_GROUP);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Override
	public ResponseEntity<Object> retrieveGrpDetails(int page, int size) {
		if (log.isInfoEnabled()) {
			log.info("***** Inside GroupServiceImpl - retrieveGrpDetails *****");
		}
		Pageable pageable = PageRequest.of(page, size);
//		Page<Group> groupPage = groupRepo.findAll(pageable);
		Page<Group> groupPage = groupRepo.findAllWithUserDetails(pageable);
		// Map each group to GroupModel
		List<GroupModel> groupDetails = groupPage.getContent().stream().map(group -> {
			GroupModel groupMap = new GroupModel();
			groupMap.setGroupId(group.getGroupId());
			groupMap.setGroupName(group.getGroupName());
			groupMap.setGroupDescription(group.getGroupDescription());
			groupMap.setGroupCoverProfileImagePath(group.getGroupCoverProfileImagePath());
			groupMap.setGroupCreatedAt(group.getGroupCreatedAt());
			groupMap.setGroupUUID(group.getGuuid());

			// Get the admin user details (already fetched via JOIN FETCH)
			User adminUser = group.getUser(); // No additional queries are triggered here

			if (adminUser != null) {
				groupMap.setAdminId(adminUser.getUserId());
				groupMap.setAdminUserFirstName(adminUser.getUserFirstName());
				groupMap.setAdminUserLastName(adminUser.getUserLastName());
				groupMap.setAdminUserProfileImagePath(adminUser.getUserCoverProfileImagePath());
			}

			return groupMap;
		}).collect(Collectors.toList());
		CustomResponseModel<GroupModel> response = new CustomResponseModel<>(groupDetails, groupPage.getTotalPages(),
				groupPage.getNumber(), groupPage.getTotalElements(), groupPage.getSize(), groupPage.hasNext(),
				groupPage.getNumber() + 1);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Transactional
	@Override
	public ResponseEntity<Object> fetchPendingFriendList(long groupAdminId, long groupId, int page, int size) {
		if (log.isInfoEnabled()) {
			log.info("***** Inside GroupServiceImpl - fetchPendingFriendList *****");
		}
//		Pageable pageable = PageRequest.of(page, size);
		/* get the remaining friends who have not yet been added to the group */
		List<FriendsList> friendList = groupServiceDao.getRemainingFriendsList(groupId, groupAdminId, page, size);
		// Fetch the total count of remaining friends
		long totalCount = groupServiceDao.getRemainingFriendsCount(groupId, groupAdminId);

		// Calculate pagination details
		int totalPages = (int) Math.ceil((double) totalCount / size);
		boolean hasNextPage = page < totalPages - 1;
		int nextPageNo = hasNextPage ? page + 1 : page;
		CustomResponseModel<FriendsList> customResponse = new CustomResponseModel<>(friendList, totalPages, page,
				totalCount, size, hasNextPage, nextPageNo);
		return ResponseEntity.status(HttpStatus.OK).body(customResponse);
	}

	@Override
	public ResponseEntity<Object> getAllGroups() {
		response = new HashMap<>();
		List<Group> indexData = groupRepo.findAll();

		if (indexData.isEmpty()) {
			response.put(CommonMessages.STATUS, "Failed");
			response.put(CommonMessages.MESSAGE, "You're all caught up! There are no more Groups to show at the moment");
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		} else {
			
			response.put("GroupsData", indexData);
			response.put(CommonMessages.TOTAL_ELEMENTS, indexData.size());
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

}

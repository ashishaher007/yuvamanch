package com.ymanch.controller;

import java.util.List;

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

import com.ymanch.entity.Group;
import com.ymanch.exception.ResourceNotFoundException;
import com.ymanch.helper.CheckUserStatus;
import com.ymanch.helper.CommonFunctions;
import com.ymanch.helper.RequireUserAccess;
import com.ymanch.helper.Enums.PostOwnerType;
import com.ymanch.model.PageGroupUpdateModel;
import com.ymanch.model.PostUploadModel;
import com.ymanch.service.AccessControlService;
import com.ymanch.service.GroupService;
import com.ymanch.service.PostService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ymanch/users/group")
@Slf4j
public class GroupController {

	private GroupService groupService;
	private AccessControlService accessControlService;
	private PostService postService;

	public GroupController(GroupService groupService, AccessControlService accessControlService,
			PostService postService) {
		super();
		this.groupService = groupService;
		this.accessControlService = accessControlService;
		this.postService = postService;
	}

	@Operation(summary = "Create Group API", description = "This API is uses to create a group by admin. Admin is nothing but the user")
	@PostMapping("/createGroup")
	@CheckUserStatus
	ResponseEntity<Object> createGroup(@Valid @RequestBody Group group, @RequestParam long adminUserId,
			@RequestParam List<Long> membersIds) {
		log.info("***** Inside - GroupController - createGroup *****");
		accessControlService.verifyUserAccess(adminUserId);
		return groupService.createGroup(adminUserId, group, membersIds);
	}

	@Operation(summary = "Add Member In Group API", description = "This API is uses to add single members in group. Admin is nothing but the user")
	@PostMapping("/addmemberToGroup/{adminUserId}/{groupId}")
	@CheckUserStatus
	ResponseEntity<Object> addmemberToGroup(@PathVariable long adminUserId, @PathVariable long groupId,
			@RequestParam List<Long> userId) {
		log.info("***** Inside - GroupController - addmemberToGroup *****");
//		accessControlService.verifyUserAccess(adminUserId);
		return groupService.addMember(groupId, adminUserId, userId);
	}

	@Operation(summary = "Get All Groups Detail API", description = "This API is used to retrieve all groups created by the admin (user) based on the user ID")
	@GetMapping("/getGroups/{userId}")
	@RequireUserAccess
	public ResponseEntity<Object> getGroups(@PathVariable long userId) {
		log.info("***** Inside - GroupController - getGroups *****");
		return groupService.getGroupsByUserId(userId);
	}

	@Operation(summary = "Delete Group API", description = "This API is used to delete group by admin and group Id")
	@DeleteMapping("/deleteGroup/{adminUserId}/{groupId}")
	@RequireUserAccess
	public ResponseEntity<Object> deleteGroup(@PathVariable long adminUserId, @PathVariable long groupId) {
		log.info("***** Inside - GroupController - deleteGroup *****");
		return groupService.deleteGroupsById(adminUserId, groupId);
	}

	@Operation(summary = "Get Group Details API", description = "This API is use to get all details of group by group Id")
	@GetMapping("/getGroupDetails/{userId}/{groupUUID}")
	@CheckUserStatus
	public ResponseEntity<Object> getGroupDetails(@PathVariable long userId, @PathVariable String groupUUID,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
		log.info("***** Inside - GroupController - getGroupDetails *****");
//		accessControlService.verifyUserAccess(userId);
		return groupService.getGroupDetails(userId, groupUUID, page, size);
	}

	@Operation(summary = "Add Post In Group API", description = "This API is used to add a new Post in group by user and group Id")
	@PostMapping("/addPost/{groupdUserOrAdminUserId}/{groupId}")
	@CheckUserStatus // user define annotation
	ResponseEntity<Object> addPost(@PathVariable long groupdUserOrAdminUserId, @PathVariable long groupId,
			@ModelAttribute PostUploadModel post) throws ResourceNotFoundException {
		log.info("***** Inside - GroupController - addPost *****");
		accessControlService.verifyUserAccess(groupdUserOrAdminUserId);
		return postService.storePost(groupdUserOrAdminUserId, post, "", groupId, PostOwnerType.GROUP);
	}

	@Operation(summary = "Update Group Details Api", description = "This Api is used to update group details by the admin user Id")
	@PutMapping("/updateGroup/{adminUserId}/{groupId}")
	@CheckUserStatus
	ResponseEntity<Object> updateGroup(@PathVariable long adminUserId, @PathVariable long groupId,
			@ModelAttribute PageGroupUpdateModel group) {
		log.info("***** Inside GroupController - updateGroup *****");
		accessControlService.verifyUserAccess(adminUserId);
		return groupService.updateGroup(adminUserId, groupId, group);
	}

	@Operation(summary = "Exit Or Remove User Group API", description = "The API provides functionality for users to exit groups on their own, or for admin to remove users from the group as needed")
	@DeleteMapping("/exitGroup/{groupId}/{adminUserId}/{userId}")
	public ResponseEntity<Object> exitGroup(@PathVariable long groupId, @PathVariable long adminUserId,
			@PathVariable long userId) {
		log.info("***** Inside - GroupController - exitGroup *****");
//		accessControlService.verifyUserAccess(adminUserId);
		return groupService.exitGroup(adminUserId, groupId, userId);
	}

	@Operation(summary = "Get Remaining Friends Of Group Details API", description = "This API is use to retrieve remaining friend list who are not added in the group")
	@GetMapping("/getRemainingFriend/{groupAdminId}/{groupId}")
	public ResponseEntity<Object> getRemainingFriend(@PathVariable long groupAdminId, @PathVariable long groupId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
		log.info("***** Inside - GroupController - getRemainingFriend *****");
		return groupService.fetchPendingFriendList(groupAdminId, groupId, page, size);
	}
}

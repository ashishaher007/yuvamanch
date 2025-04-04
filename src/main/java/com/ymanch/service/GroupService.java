package com.ymanch.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.ymanch.entity.Group;
import com.ymanch.model.PageGroupUpdateModel;

import jakarta.validation.Valid;

public interface GroupService {

	ResponseEntity<Object> createGroup(long adminUserId, @Valid Group group, List<Long> membersIds);

	ResponseEntity<Object> addMember(long groupId, long adminUserId, List<Long> user);

	ResponseEntity<Object> getGroupsByUserId(long userId);

	ResponseEntity<Object> deleteGroupsById(long adminUserId, long groupId);

	ResponseEntity<Object> getGroupDetails(long groupId, String groupUUID, int page, int size);

	ResponseEntity<Object> updateGroup(long adminuserId, long groupId, PageGroupUpdateModel group);

	ResponseEntity<Object> exitGroup(long adminUserId, long groupId, long userId);

	ResponseEntity<Object> retrieveGrpDetails(int page, int size);

	ResponseEntity<Object> fetchPendingFriendList(long userId, long groupId, int page, int size);

	ResponseEntity<Object> getAllGroups();

}

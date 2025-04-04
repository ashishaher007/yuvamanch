package com.ymanch.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.ymanch.entity.Group;
import com.ymanch.entity.Posts;
import com.ymanch.model.AdminPostAdvertisementModel;
import com.ymanch.model.GroupModel;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GroupMapper {
	private ModelMapper modelMapper;

	public GroupMapper(ModelMapper modelMapper) {
		super();
		this.modelMapper = modelMapper;
	}

	public GroupModel convertToDto(Group group) {
		// to add admin details
		GroupModel groupModel = modelMapper.map(group, GroupModel.class);
		groupModel.setAdminId(group.getUser().getUserId());
		groupModel.setAdminUserFirstName(group.getUser().getUserFirstName());
		groupModel.setAdminUserLastName(group.getUser().getUserLastName());
		groupModel.setAdminUserProfileImagePath(group.getUser().getUserProfileImagePath());
		return groupModel;
	}
}

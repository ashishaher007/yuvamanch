package com.ymanch.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.ymanch.entity.Group;
import com.ymanch.entity.Pages;
import com.ymanch.model.GroupModel;
import com.ymanch.model.PagesModelV1;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PagesMapper {
	private ModelMapper modelMapper;

	public PagesMapper(ModelMapper modelMapper) {
		super();
		this.modelMapper = modelMapper;
	}

	public PagesModelV1 convertToDto(Pages page) {
		// to add admin details
		PagesModelV1 pageModel = modelMapper.map(page, PagesModelV1.class);
		pageModel.setAdminId(page.getUser().getUserId());
		pageModel.setAdminUserFirstName(page.getUser().getUserFirstName());
		pageModel.setAdminUserLastName(page.getUser().getUserLastName());
		pageModel.setAdminUserProfileImagePath(page.getUser().getUserProfileImagePath());
		return pageModel;
	}

}

package com.ymanch.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.ymanch.entity.Posts;
import com.ymanch.model.AdminPostAdvertisementModel;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PostMapper {

	private ModelMapper modelMapper;

	public PostMapper(ModelMapper modelMapper) {
		super();
		this.modelMapper = modelMapper;
	}
	
	 public List<AdminPostAdvertisementModel> convertToDto(List<Posts> getPostByDistrict) {
		 return getPostByDistrict.stream()
			        .map(post -> modelMapper.map(post, AdminPostAdvertisementModel.class))
			        .collect(Collectors.toList());
	    }
	 
	 // ✅ NEW METHOD: Convert a single Post entity to DTO
	    public AdminPostAdvertisementModel convertToDto(Posts post) {
	        if (post == null) {
	            return null;
	        }
	        return modelMapper.map(post, AdminPostAdvertisementModel.class);
	    }
	
}

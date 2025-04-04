package com.ymanch.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ymanch.entity.Boost;
import com.ymanch.entity.District;
import com.ymanch.entity.Posts;
import com.ymanch.entity.User;
import com.ymanch.helper.CommonMessages;
import com.ymanch.helper.Enums.PostOwnerType;
import com.ymanch.helper.Enums.Status;
import com.ymanch.model.BoostRequestModel;
import com.ymanch.repository.BoostRepository;
import com.ymanch.repository.DistrictRepository;
import com.ymanch.repository.PostRepository;
import com.ymanch.repository.UserRepository;
import com.ymanch.service.BoostService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BoostServiceImpl implements BoostService {
	
	private Map<Object, Object> response;

	@Autowired
    BoostRepository boostRepository;
    
	@Autowired
    UserRepository userRepository;
    
	@Autowired
     PostRepository postrepository;
	
	 @Autowired
	 DistrictRepository districtRepository;
	 
	 @Autowired
     private CommonMessages commonMessages;
     
    

    @Override
    public Boost createBoost(Boost boost) {
        // You can add any additional business logic here before saving the boost
        return boostRepository.save(boost);
    }

    @Override
    public Optional<Boost> getBoostById(Long boostId) {
        return boostRepository.findById(boostId);
    }

    @Override
    public List<Boost> getAllBoosts() {
        return boostRepository.findAll();
    }

    @Transactional
    @Override
    public Boost updateBoost(Long boostId, BoostRequestModel boostRequestModel, Long userId) {
        // Check if the user is an admin
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!user.getUserRole().equals("ROLE_ADMIN")) {
            throw new IllegalArgumentException("Only an admin can update a boost.");
        }

        // Check if the boost exists
        Boost existingBoost = boostRepository.findById(boostId).orElseThrow(() -> new IllegalArgumentException("Boost not found"));
        
        if (!(existingBoost.getUser().getUserId() == (userId))) {
            throw new IllegalArgumentException("You are not authorized to update this boost.");
        }

        // Update the boost details based on optional fields
        if (boostRequestModel.getStartDate() != null) {
            existingBoost.setStartDate(boostRequestModel.getStartDate());  // Update startDate if provided
        }

        if (boostRequestModel.getStartTime() != null) {
            existingBoost.setStartTime(boostRequestModel.getStartTime());  // Update startTime if provided
        }

        if (boostRequestModel.getEndDate() != null) {
            existingBoost.setEndDate(boostRequestModel.getEndDate());  // Update startTime if provided
        }
        
        if (boostRequestModel.getEndTime() != null) {
            existingBoost.setEndTime(boostRequestModel.getEndTime());  // Update startTime if provided
        }

        // Handle districts
        if (boostRequestModel.getDistrictIds() != null && !boostRequestModel.getDistrictIds().isEmpty()) {
            // Assuming you have a method to get a list of Districts by their IDs
            List<District> districts = districtRepository.findAllById(boostRequestModel.getDistrictIds());
            existingBoost.setDistricts(districts);
        }

        checkAndUpdateBoostStatus(boostId);
        // Save the updated boost and return it
        return boostRepository.save(existingBoost);
    }


    @Override
    public void deleteBoost(long boostId, long userId) {
        // Check if the user is an admin
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!user.getUserRole().equals("ROLE_ADMIN")) {
            throw new IllegalArgumentException("Only an admin can delete a boost.");
        }

        // Check if the boost exists
        Boost boost = boostRepository.findById(boostId).orElseThrow(() -> new IllegalArgumentException("Boost not found"));
        
        if (!(boost.getUser().getUserId()==(userId))) {
            throw new IllegalArgumentException("You are not authorized to delete this boost.");
        }

        // Delete the boost
        boostRepository.delete(boost);
    }

//    @Override
//    public boolean isBoostActive(Long boostId) {
//        Optional<Boost> boost = boostRepository.findById(boostId);
//        return boost.map(Boost::isBoosted).orElse(false);  // Returns false if boost is not found
//    }
    
    
    @Override
    public ResponseEntity<Object> boostPost(Long userId, Long postId, BoostRequestModel boostRequestModel) {
    	response = new HashMap<>();
        // Fetch the user (admin) and post by their IDs
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Posts post = postrepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
       log.info(post.getPostOwnerType()+"*******************************");
        if(!post.getPostOwnerType().equals(PostOwnerType.PUBLIC)) {
        	 throw new IllegalArgumentException("not an Public post");
        }
      
        List<District> districts = districtRepository.findAllById(boostRequestModel.getDistrictIds());
        
        Boost existingBoost = boostRepository.findByPostId2(postId);
        

        if (existingBoost != null) {
        	response.put(CommonMessages.STATUS, commonMessages.FAILED);
			response.put(CommonMessages.MESSAGE, commonMessages.POST_ALREADY_BOOSTED);
			return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            // If no existing boost, create a new one
            Boost boost = new Boost();
            boost.setUser(user);  // Set the user who is boosting the post
            boost.setPost(post);  // Set the post to be boosted
            boost.setStartDate(boostRequestModel.getStartDate());
            boost.setStartTime(boostRequestModel.getStartTime());
            boost.setEndDate(boostRequestModel.getEndDate());
            boost.setEndTime(boostRequestModel.getEndTime());
            boost.setBoostStatus(Status.ACTIVE);
            boost.setDistricts(districts);  // Set the districts

            boostRepository.save(boost);
            // Save the new boost and return it
            response.put(CommonMessages.STATUS, CommonMessages.SUCCESS);
    		response.put(CommonMessages.MESSAGE, commonMessages.Boost_CREATED_SUCCESSFUL);
    		return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
    }
    
    @Override
    public void checkAndUpdateBoostStatus(Long boostId) {
    	
    	boostRepository.updateEndedEventsBatch(boostId);
    	boostRepository.updateActiveEventsBatch(boostId);
        }
    

	@Override
	public ResponseEntity<Object> addDistricts(long postId, List<Long> districts) {
		
		response = new HashMap<>();
		// TODO Auto-generated method stub
		postrepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
		Boost boost = boostRepository.findByPostId(postId)
	                .orElseThrow(() -> new IllegalArgumentException("Boost not found"));
		
		if (boost == null) {
			
			response.put(CommonMessages.STATUS, commonMessages.FAILED);
			response.put(CommonMessages.MESSAGE, "No Boost Post");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
		
		
		List<Long> existingDistricts= boost.getDistricts().stream().map(District::getDistrictId)
				.collect(Collectors.toList());
		
		 // Loop through the input districts and add only the ones that are not in the existing list
	    List<District> newDistricts = new ArrayList<>();
	    for (Long districtId : districts) {
	        if (!existingDistricts.contains(districtId)) {
	            // If the district is not already in the boost, add it to the newDistricts list
	            Optional<District> district = districtRepository.findById(districtId);
	            if (district.isPresent()) {
	                newDistricts.add(district.get());
	            }
	        }
	    }
	    if (!newDistricts.isEmpty()) {
	        // If there are new districts to add, update the boost and save it
	        boost.getDistricts().addAll(newDistricts);
	        boostRepository.save(boost);

	        response.put(CommonMessages.STATUS, commonMessages.SUCCESS);
	        response.put(CommonMessages.MESSAGE, "Districts added successfully.");
	        return ResponseEntity.status(HttpStatus.OK).body(response);
	    } else {
	        // If no new districts were added, return a message
	        response.put(CommonMessages.STATUS, commonMessages.FAILED);
	        response.put(CommonMessages.MESSAGE, "No new districts to add.");
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	    }
	}

	

}

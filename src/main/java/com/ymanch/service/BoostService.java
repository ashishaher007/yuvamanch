package com.ymanch.service;


import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import com.ymanch.entity.Boost;
import com.ymanch.model.BoostRequestModel;


public interface BoostService {
    
    Boost createBoost(Boost boost);
    
    Optional<Boost> getBoostById(Long boostId);
    
    List<Boost> getAllBoosts();
    
    Boost updateBoost(Long boostId, BoostRequestModel boost, Long userId);  // Update method with userId validation
    
    void deleteBoost(long boostId, long userId);  // Delete method with userId validation
    
//    boolean isBoostActive(Long boostId);  // Checks if a specific boost is still active
    
    ResponseEntity<Object> boostPost(Long userId, Long postId, BoostRequestModel boostRequestModel);
	
	void checkAndUpdateBoostStatus(Long boostId);

	ResponseEntity<Object> addDistricts(long postId, List<Long> districts);

//	List<Boost> findActiveBoosts();
	
//	Optional<Boost> getBoostByUserId(Long userId);
	
}

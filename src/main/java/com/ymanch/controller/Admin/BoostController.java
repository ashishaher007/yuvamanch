package com.ymanch.controller.Admin;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ymanch.entity.Boost;
import com.ymanch.model.BoostRequestModel;
import com.ymanch.service.AccessControlService;
import com.ymanch.service.BoostService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ymanch/admin/boosts")
@Slf4j
public class BoostController {

	@Autowired
    BoostService boostService;
	
	@Autowired
	private AccessControlService accessControlService;

	@Operation(summary = "Boost post")
    @PostMapping("/boost-post")
    public ResponseEntity<Object> boostPost(@RequestParam Long userId, @RequestParam Long postId, @RequestBody BoostRequestModel boost) {
        try {
//            Boost createdBoost = boostService.boostPost(userId, postId, boost);
            return boostService.boostPost(userId, postId, boost);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Endpoint to get a boost by its ID
	@Operation(summary = "get Boost by id")
    @GetMapping("/{boostId}")
    public ResponseEntity<Boost> getBoostById(@PathVariable Long boostId) {
        Optional<Boost> boost = boostService.getBoostById(boostId);
        return boost.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    
//    @GetMapping("/{userIdId}")
//    public ResponseEntity<Boost> getBoostByUserId(@PathVariable Long userId) {
//        Optional<Boost> boost = boostService.getBoostByUserId(userId);
//        return boost.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
//    }

    // Endpoint to get all boosts
	@Operation(summary = "get all Boost post")
    @GetMapping("/getAllEvents/{userId}")
    public List<Boost> getAllBoosts(@PathVariable long userId) {
		log.info("***** Inside - BoostController - getAllBoosts *****");
		accessControlService.verifyUserAccess(userId);
        return boostService.getAllBoosts();
    }
	


	@Operation(summary = "Add Districts in Boost", description = "This API is uses to add districts in boostPost")
	@PostMapping("/addDistricts/{postId}")
	ResponseEntity<Object> addDistricts(@PathVariable long postId,@RequestParam List<Long> districts) {
		return boostService.addDistricts(postId, districts);
	}
    
 // Endpoint for updating an existing boost (only accessible to admins)
	@Operation(summary = "Update Boosted post")
    @PutMapping("/{userId}/{boostId}")
    public ResponseEntity<Boost> updateBoost(
            @PathVariable Long userId,  // userId as path parameter
            @PathVariable Long boostId, // boostId as path parameter
            @RequestBody BoostRequestModel boost     // boost object in the body
    ) {
        try {
            // Check if the user is an admin
            Boost updatedBoost = boostService.updateBoost(boostId, boost, userId);
            return ResponseEntity.ok(updatedBoost);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Endpoint for deleting a boost (only accessible to admins)
	@Operation(summary = "Delete Boosted post")
    @DeleteMapping("/{userId}/{boostId}")
    public ResponseEntity<Void> deleteBoost(
            @PathVariable Long userId,  // userId as path parameter
            @PathVariable Long boostId  // boostId as path parameter
    ) {
        try {
            // Check if the user is an admin
            boostService.deleteBoost(boostId, userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

//    // Endpoint to check if a boost is active
//    @GetMapping("/{boostId}/is-active")
//    public ResponseEntity<Boolean> isBoostActive(@PathVariable Long boostId) {
//        boolean isActive = boostService.isBoostActive(boostId);
//        return ResponseEntity.ok(isActive);
//    }
    

}

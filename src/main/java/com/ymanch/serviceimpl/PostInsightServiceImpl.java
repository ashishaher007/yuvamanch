package com.ymanch.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ymanch.entity.District;
import com.ymanch.entity.PostInsight;
import com.ymanch.entity.Posts;
import com.ymanch.entity.User;
import com.ymanch.repository.DistrictRepository;
import com.ymanch.repository.PostInsightRepository;
import com.ymanch.repository.PostRepository;
import com.ymanch.repository.UserRepository;
import com.ymanch.service.PostInsightService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PostInsightServiceImpl implements PostInsightService {

	@Autowired
    private PostRepository postsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostInsightRepository postInsightRepository;
    @Autowired
	private DistrictRepository districtRepo;

			@Override
			public ResponseEntity<Object> insightCount(long userId, long postId, Long reachTimeSpent, Long viewTimeSpent) {
			try {
			    // Fetch user and post from the database
			    Optional<User> userOpt = userRepository.findById(userId);
			    Optional<Posts> postOpt = postsRepository.findById(postId);
			
			    if (userOpt.isEmpty() || postOpt.isEmpty()) {
			        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or Post not found.");
			    }
			
			    User user = userOpt.get();
			    Posts post = postOpt.get();
			
			    // Determine the district from the user
			    District district = user.getDistrict();
			
			    // Check if an insight record already exists for this user and post
			    Optional<PostInsight> existingInsightOpt = postInsightRepository.findByPostAndUser(post, user);
			
			    PostInsight insight;
			    if (existingInsightOpt.isPresent()) {
			        insight = existingInsightOpt.get();
			    } else {
			        // Create a new PostInsight entry
			        insight = new PostInsight();
			        insight.setPost(post);
			        insight.setUser(user);
			        insight.setDistrict(district);
			        insight.setReachCount(0); 
			        insight.setViewCount(0); 
			    }
			
			    // Apply conditions for reachCount and viewCount
			    if (viewTimeSpent != null && viewTimeSpent > 6) {
			        insight.setViewCount(insight.getViewCount() + 1);
			    }
			    if (reachTimeSpent != null && reachTimeSpent >= 3 && reachTimeSpent <= 6) {
			        insight.setReachCount(insight.getReachCount() + 1);
			    }
			
			    // Save the insight data
			    postInsightRepository.save(insight);
			
			    return ResponseEntity.status(HttpStatus.OK).body("Post insight updated successfully.");
			} catch (Exception e) {
			    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving post insight: " + e.getMessage());
			}
}

			@Override
			public ResponseEntity<Object> getPostCounts(long postId) {
			int reachcount = 0;
			int viewcount = 0;
			
			if (log.isInfoEnabled()) {
			    log.info("***** Inside PostInsightServiceImpl - getPostCounts *****");
			}
			
			Map<String, Object> response = new HashMap<>();
			List<PostInsight> post = postInsightRepository.findAllByPostId(postId);
			List<Map<String, Object>> details = new ArrayList<>();
			// Check if the post exists
			if (post == null || post.isEmpty()) {
			    return ResponseEntity.badRequest().body("Post not found");
			}
			
			// Aggregate the total reach and view counts for the given postId
			for (PostInsight p : post) {
			    reachcount += p.getReachCount();
			    viewcount += p.getViewCount();
			}
			
			// Add total reach and view counts to the response
			response.put("totalReach", reachcount);
			response.put("totalViews", viewcount);
			
			
			// Fetch district-wise counts (with postId)
			List<Object[]> districtCount = postInsightRepository.getDistrictAndPostWiseCounts(postId);
			
			
			for (Object[] objects : districtCount) {
				String districtName = (String) objects[0];
				Integer viewCount = ((Number) objects[1]).intValue();
				Integer reachCount = ((Number) objects[2]).intValue();
				// Create a map for the district with name, count, and ID
				Map<String, Object> districtcount = new HashMap<>();
				districtcount.put("districtName", districtName);
				districtcount.put("viewCount", viewCount);
				districtcount.put("reachCount", reachCount);
				// Add the district map to the list
				details.add(districtcount);
				//total += count;
			}
			
			response.put("details", details);
			return new ResponseEntity<>(response, HttpStatus.OK);
			}



}

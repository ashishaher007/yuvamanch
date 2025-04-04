package com.ymanch.serviceimpl;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ymanch.entity.Hashtag;
import com.ymanch.helper.CommonMessages;
import com.ymanch.helper.DateTimeUtil;
import com.ymanch.model.PostIndexPage;
import com.ymanch.repository.HashtagRepository;
import com.ymanch.repository.PostRepository;
import com.ymanch.service.HashtagService;

@Service
public class HashtagServiceImpl implements HashtagService {

	  private HashtagRepository hashtagRepository;
	  private CommonMessages MSG;
	  private PostRepository postRepo;

	public HashtagServiceImpl(HashtagRepository hashtagRepository,
			CommonMessages MSG,PostRepository postRepo) {
		this.hashtagRepository = hashtagRepository;
		this.MSG=MSG;
		this.postRepo=postRepo;
	}
	 @Override
	    public ResponseEntity<Object> createHashtag(Hashtag hashtag) {
	        // Check if hashtag with the same name already exists
		 System.out.println("Hashtag Name: " + hashtag.getName());
	        Optional<Hashtag> existingHashtag = hashtagRepository.findByName(hashtag.getName());
	        if (existingHashtag.isPresent()) {
	            return ResponseEntity.status(HttpStatus.CONFLICT)
	                    .body("Hashtag with the name '" + hashtag.getName() + "' already exists.");
	        }
	        
	        // Save the new hashtag
	        Hashtag savedHashtag = hashtagRepository.save(hashtag);
	        return ResponseEntity.status(HttpStatus.CREATED).body(savedHashtag);
	    }

	    @Override
	    public ResponseEntity<Object> getAllHashtags() {
	        return ResponseEntity.ok(hashtagRepository.findAll());
	    }

	    @Override
	    public ResponseEntity<Object> getHashtagByName(String name) {
	        List<Hashtag> hashtags = hashtagRepository.findByHashtagName(name); 
	        Map<Object, Object> response = new HashMap<>();

	        if (hashtags.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body("Hashtag with name '" + name + "' not found.");
	        }

	        // If only one hashtag is expected, return the first one
//	        if (hashtags.size() == 1) {
//	            Hashtag hashtag = hashtags.get(0);
//	            response.put("ID", hashtag.getId());
//	            response.put("Name", hashtag.getName());
//	            response.put("CreatedAt", hashtag.getCreatedAt());
//	            response.put("UpdatedAt", hashtag.getUpdatedAt());
//	            response.put("Count", hashtag.getCount());
//	            return new ResponseEntity<>(response, HttpStatus.OK);
//	        }

	        // If multiple hashtags are found, return a list
	        List<Map<String, Object>> hashtagList = new ArrayList<>();
	        for (Hashtag hashtag : hashtags) {
	            Map<String, Object> hashtagData = new HashMap<>();
	            hashtagData.put("ID", hashtag.getId());
	            hashtagData.put("Name", hashtag.getName());
	            hashtagData.put("CreatedAt", hashtag.getCreatedAt());
	            hashtagData.put("UpdatedAt", hashtag.getUpdatedAt());
	            hashtagData.put("Count", hashtag.getCount());
	            hashtagList.add(hashtagData);
	        }

	        return new ResponseEntity<>(hashtagList, HttpStatus.OK);
	    }
	    
	    

	    @Override
	    public ResponseEntity<Object> deleteHashtag(Long id) {
	        if (!hashtagRepository.existsById(id)) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body("Hashtag with ID '" + id + "' not found.");
	        }
	        hashtagRepository.deleteById(id);
	        return ResponseEntity.ok("Hashtag with ID '" + id + "' deleted successfully.");
	    }
		
	    @Override
		public ResponseEntity<Object> getHashtagById(int page, int size, String hashtagName,Long userId) {
			// TODO Auto-generated method stub
			Map<Object, Object> response = new HashMap<>();
			Pageable hashPageable = PageRequest.of(page, size);
			Page<PostIndexPage> boostedPosts = postRepo.findPostByHashName(hashPageable, hashtagName,userId);
			if (boostedPosts.isEmpty()) {
				response.put(MSG.STATUS, MSG.FAILED);
				response.put(MSG.MESSAGE, MSG.Hashtag_NOT_FOUND);
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}
			// Format time for all posts (e.g., "2 hours ago")
			boostedPosts.stream().map(postIndex -> {
				String timeAgo = DateTimeUtil.convertToTimeAgo(postIndex.getPostCreatedAt());
				postIndex.setPostUploadedAt(timeAgo);
				return postIndex;
			}).collect(Collectors.toList());

			// Prepare the response
			response.put("homePostData", boostedPosts.getContent());
			response.put(MSG.TOTAL_ELEMENTS, boostedPosts.getTotalElements());
			response.put("hasNextPage", boostedPosts.hasNext()); // Check if there's a next page
			response.put("nextCursor", page + 1); // Increment page for next request

			return new ResponseEntity<>(response, HttpStatus.OK);
		}
}

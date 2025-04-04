package com.ymanch.serviceimpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ymanch.dao.HomeServiceDao;
import com.ymanch.entity.Group;
import com.ymanch.entity.Hashtag;
import com.ymanch.entity.Pages;
import com.ymanch.entity.PostMention;
import com.ymanch.entity.Posts;
import com.ymanch.entity.User;
import com.ymanch.entity.UserSavedPost;
import com.ymanch.exception.ResourceNotFoundException;
import com.ymanch.helper.CommonFunctions;
import com.ymanch.helper.CommonMessages;
import com.ymanch.helper.Enums.PostOwnerType;
import com.ymanch.mapper.PostMapper;
import com.ymanch.model.AdminPostAdvertisementModel;
import com.ymanch.model.PostIndex;
import com.ymanch.model.PostIndexComments;
import com.ymanch.model.PostIndexPage;
import com.ymanch.model.PostUploadModel;
import com.ymanch.repository.DistrictRepository;
import com.ymanch.repository.GroupRepository;
import com.ymanch.repository.HashtagRepository;
import com.ymanch.repository.PagesRepository;
import com.ymanch.repository.PostCommentRepository;
import com.ymanch.repository.PostMentionRepository;
import com.ymanch.repository.PostRepository;
import com.ymanch.repository.UserRepository;
import com.ymanch.repository.UserSavePostRepository;
import com.ymanch.service.PostService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PostServiceImpl implements PostService {

	private Map<Object, Object> response;

	private PostRepository postRepo;
	private UserRepository userRepo;
	private CommonMessages MSG;
	private PostMapper postMapper;
	private GroupRepository groupRepo;
	private DistrictRepository districtRepo;
	private UserSavePostRepository userSavePostRepo;
	private HashtagRepository hashtagRepository;
	private HomeServiceDao homeServiceDao;
	private CommonFunctions commonFunctions;
	private PagesRepository pagesRepo;
	private PostMentionRepository postMentionRepo;
	private PostCommentRepository postCommentRepo;
	private RedisTemplate<String, Object> redisTemplate;

	public PostServiceImpl(PostRepository postRepo, UserRepository userRepo, CommonMessages mSG, PostMapper postMapper,
			GroupRepository groupRepo, DistrictRepository districtRepo, UserSavePostRepository userSavePostRepo,
			HashtagRepository hashtagRepository, HomeServiceDao homeServiceDao, CommonFunctions commonFunctions,
			PagesRepository pagesRepo, PostMentionRepository postMentionRepo,PostCommentRepository postCommentRepo,
			RedisTemplate<String, Object> redisTemplate) {
		super();
		this.postRepo = postRepo;
		this.userRepo = userRepo;
		MSG = mSG;
		this.postMapper = postMapper;
		this.groupRepo = groupRepo;
		this.districtRepo = districtRepo;
		this.userSavePostRepo = userSavePostRepo;
		this.hashtagRepository = hashtagRepository;
		this.homeServiceDao = homeServiceDao;
		this.commonFunctions = commonFunctions;
		this.pagesRepo = pagesRepo;
		this.postMentionRepo = postMentionRepo;
		this.postCommentRepo=postCommentRepo;
		this.redisTemplate = redisTemplate;

	}

	@Transactional
	@Override
	public ResponseEntity<Object> storePost(long userId, @Valid PostUploadModel post, String district, long id,
			PostOwnerType ownerType) throws ResourceNotFoundException {
		log.info("***** Inside PostServiceImpl - storePost *****");
		response = new HashMap<>();
		User userdata = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User with the Id " + userId + " not found"));

		PostIndex postIndexData = new PostIndex();
		boolean metaData = false;
//		boolean ancherData= false;
		Posts postEntity = new Posts();
		postEntity.setPostName(post.getPostName());
		postEntity.setUser(userdata);
		postEntity.setPostOwnerType(PostOwnerType.PUBLIC);
		postEntity.setDistrict(userdata.getDistrict());

		// Save the Post entity first (if not already saved)

		if (postEntity.getId() == null) {
			postEntity = postRepo.save(postEntity);
		}

		// Extract hashtags from post name
		if (post.getPostName() != null && !post.getPostName().isEmpty()) {
			List<String> hashtags = new ArrayList<>();

			// Split the post name into words
			String[] words = post.getPostName().split(" ");

			for (String word : words) {
				// Check if the word starts with "#" (i.e., it's a hashtag)
				if (word.startsWith("#") && word.length() > 1) {
					// Remove any trailing punctuation or special characters that might have
					// attached to the hashtag
					String hashtagName = word.replaceAll("[^\\w]", "");

					if (hashtagName.length() > 1 && !hashtags.contains(hashtagName)) {
						hashtags.add(hashtagName);
					}
				}
			}

			// If there are any hashtags extracted, process them
			if (!hashtags.isEmpty()) {
				List<Hashtag> hashtagEntities = new ArrayList<>();
				for (String hashtagName : hashtags) {
					Optional<Hashtag> existingHashtag = hashtagRepository.findByName(hashtagName);
					Hashtag hashtag = existingHashtag.orElseGet(() -> {
						// If not, create a new hashtag and set its count to 0
						Hashtag newHashtag = new Hashtag();
						newHashtag.setName(hashtagName);
						newHashtag.setCount(0); // Initialize the count
						return hashtagRepository.save(newHashtag);
					});

					// Add the hashtag to the list
					hashtagEntities.add(hashtag);

					// Update hashtag count
					hashtag.setCount(hashtag.getCount() + 1);
					hashtagRepository.save(hashtag);
				}

				// Set the hashtags in the post
				postEntity.setHashtags(hashtagEntities);
			}
		}

		postEntity.setPostType(post.getPostType());
		postEntity.setVideoThumbnailUrl(post.getVideoThumbnailUrl());

		// Regex to check if the string contains a URL
		if (post.getPostImage() == null || post.getPostImage().isEmpty()) {
			Pattern pattern = Pattern.compile(CommonMessages.METADATAURLPATTERN);
//			Pattern ancher=Pattern.compile(CommonMessages.ANCHORTAGPATTERN);
			Matcher matcher = pattern.matcher(post.getPostName());
//			Matcher ancherr=pattern.matcher(post.getPostName());
			metaData = matcher.find();
//			ancherData=ancherr.find();

//			if (metaData && !ancherData) {
			if (metaData) {
				// URL found in postName
				String extractedUrl = matcher.group(0); // Extract the URL
//				System.out.println("URL found in post name: " + extractedUrl);
				try {
					Document doc = Jsoup.connect(extractedUrl).get();
					// Open Graph metadata
					String title = doc.select("meta[property=og:title]").attr("content");
					if (title.isEmpty()) {
						title = doc.title(); // Fallback
					}
					String description = doc.select("meta[property=og:description]").attr("content");
					if (description.isEmpty()) {
						description = doc.select("meta[name=description]").attr("content");
					}
					String imageUrl = doc.select("meta[property=og:image]").attr("content");
//					String urlTag = doc.select("meta[property=og:url]").attr("content");

//				postIndexData.setTitle(title);
//				postEntity.setPostName(post.getPostName() + title);
					postEntity.setAdvertisementDescription(description);
//				postIndexData.setDescription(description);
					postEntity.setPostImageUrl(imageUrl);
//				postIndexData.setImageUrl(imageUrl);
//				postIndexData.setUrlTag(urlTag);
					postEntity.setVideoThumbnailUrl(title);
					postEntity.setPostType("link");

//	    		    Posts postData = postRepo.save(postEntity);
//	    	        return new ResponseEntity<>(response, HttpStatus.OK);
				} catch (IOException e) {
					response.put("error", "Failed to fetch metadata: " + e.getMessage());
				}
				// You can process this URL as needed (store it, use it, etc.)
			} else {
//				postEntity.setPostName(post.getPostName());
			}

			// You can process this URL as needed (store it, use it, etc.)
		} else {

			postEntity.setPostType(post.getPostType());
			postEntity.setVideoThumbnailUrl(post.getVideoThumbnailUrl());

		}

		/* to store post for group */
		if (ownerType.equals(PostOwnerType.GROUP)) {
			Group group = groupRepo.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException("Group with the Id " + id + " not found"));
			Group groupDetails = groupRepo.findByUserUserIdAndGroupId(userId, id);
			if (groupDetails == null) {
				response.put(CommonMessages.STATUS, CommonMessages.FAILED);
				response.put(CommonMessages.MESSAGE, MSG.GROUP_NO_GROUPS);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			} else {
				postEntity.setGroup(group);
				postEntity.setPostOwnerType(PostOwnerType.GROUP);
			}
		}
		/* to store post for group */
		if (ownerType.equals(PostOwnerType.PAGE)) {
			Pages pageData = pagesRepo.findByUserUserIdAndPageId(userId, id)
					.orElseThrow(() -> new ResourceNotFoundException(
							"Unable to find the specified page or admin. Please check with the Page ID or Admin ID"));
			postEntity.setPages(pageData);
			postEntity.setPostOwnerType(PostOwnerType.PAGE);

		}

		if (ownerType.equals(PostOwnerType.REEL)) {
			postEntity.setPostOwnerType(PostOwnerType.REEL);
		}
		// Save the image file to the server (GoDaddy VPS in your case)
		if (!metaData) {
			if (post.getPostImage() != null && !post.getPostImage().isEmpty()) {
				try {
					// Get MIME type of the uploaded file
					String contentType = post.getPostImage().getContentType();

					if (contentType != null) {
						// Check if the file is a video (based on MIME type and file extension)
						if (contentType.startsWith("video")) {
							String fileName = post.getPostImage().getOriginalFilename();
							if (fileName != null && (fileName.endsWith(".mp4") || fileName.endsWith(".avi")
									|| fileName.endsWith(".mov") || fileName.endsWith(".mkv"))) {
								// Valid video format based on MIME type and extension
								try {
									// Generate video thumbnail and store it
									String videoThumbnailUrl = commonFunctions
											.generateVideoThumbnail(post.getPostImage());
									postEntity.setVideoThumbnailUrl(videoThumbnailUrl); // Store the video thumbnail URL

//			                        // Optionally, save the video file to server
									String imageUrl = commonFunctions.saveImageToServer(post.getPostImage());
									postEntity.setPostImageUrl(imageUrl);
								} catch (Exception e) {
									e.printStackTrace(); // Handle exception properly (e.g., logging)
								}
							} else {
								System.out.println("Uploaded file is not a valid video format");
							}
						}
						// Check if the file is an image
						else if (contentType.startsWith("image")) {

							String fileName = post.getPostImage().getOriginalFilename();
							if (fileName != null && (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")
									|| fileName.endsWith(".png") || fileName.endsWith(".gif")
									|| fileName.endsWith(".bmp"))) {
								// Valid image format based on MIME type and extension

								try {
									// Optionally, save the image file to server
									String imageUrl = commonFunctions.saveImageToServer(post.getPostImage());
									postEntity.setPostImageUrl(imageUrl); // Store the image URL
								} catch (Exception e) {
									e.printStackTrace(); // Handle exception properly (e.g., logging)
								}
							} else {
								System.out.println("Uploaded file is not a valid image format.");
							}
						}
						// If it's neither a video nor an image
						else {
							System.out.println("The uploaded file is neither a video nor an image.");
						}
					}
				} catch (Exception e) {
					e.printStackTrace(); // Handle any unexpected errors properly
				}
			}

		}

		Posts postData = postRepo.save(postEntity);

		String redisKeyPattern = "sameUserTimeline:" + userdata.getUuid() + ":*"; // Pattern to match keys
		Set<String> keys = redisTemplate.keys(redisKeyPattern); // Find keys matching the pattern

		if (keys != null && !keys.isEmpty()) {
			redisTemplate.delete(keys); // Delete all matching keys
			log.info("Deleted cache keys: {}", keys);
		} else {
			log.info("No cache keys found for pattern: {}", redisKeyPattern);
		}

		// Check if the mentionId list is not empty
		if (post.getMentionId() != null && !post.getMentionId().isEmpty()) {
			// Fetch the mentioned users based on the provided IDs
			List<User> mentionedUsers = userRepo.findAllById(post.getMentionId());
			// Iterate over the list of mentioned users and create PostMention entities
			for (User mentionedUser : mentionedUsers) {
				System.out.println("Mentioned User ID: " + mentionedUser.getUserId());
				// Create a PostMention entity for each mentioned user
				PostMention postMention = new PostMention();
				postMention.setPost(postData); // Set the current post to this mention
				postMention.setMentionedUser(mentionedUser); // Set the mentioned user
				// Save the PostMention entity to establish the relationship
				postMentionRepo.save(postMention);
				// Optionally, send notifications to the mentioned users
				commonFunctions.mentionNotification(userdata, mentionedUser, postData);
			}
		} else {
			System.out.println("No mentions found.");
		}

		// Post index data
		postIndexData.setPostId(postEntity.getPostId());
		postIndexData.setUserId(postEntity.getUser().getUserId());
		postIndexData.setUserProfileImageUrl(postEntity.getUser().getUserProfileImagePath());
		postIndexData
				.setUserName(postEntity.getUser().getUserFirstName() + " " + postEntity.getUser().getUserLastName());
		postIndexData.setPostImageURl(postEntity.getPostImageUrl());
		postIndexData.setPostCreatedAt(postEntity.getPostCreatedAt());
		postIndexData.setPostName(postEntity.getPostName());
		postIndexData.setPostType(postEntity.getPostType());
		postIndexData.setVideoThumbnailUrl(postEntity.getVideoThumbnailUrl());
		postIndexData.setPostLastReactedBy("No React");
		postIndexData.setTotalCountOFReact(0);
		postIndexData.setTotalCountOfComments(0);
		postIndexData.setCommentsAndReacts(null);
		postIndexData.setDescription(postEntity.getAdvertisementDescription());

		response.put(CommonMessages.STATUS, CommonMessages.SUCCESS);
		response.put(CommonMessages.MESSAGE, MSG.POST_ADD_SUCCESSFULL);
		response.put("homePostData", postIndexData);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@Transactional
	@Override
	public ResponseEntity<Object> updatePost(long postId, String postName) throws ResourceNotFoundException {
		log.info("***** Inside PostServiceImpl - updatePost *****");
		response = new HashMap<>();
		Posts postData = postRepo.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post with the Id " + postId + " not found"));
		postData.setPostName(postName);
		postRepo.save(postData);
		response.put("status", CommonMessages.SUCCESS);
		response.put("message", MSG.POST_UPDATE_SUCCESSFUL);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Transactional
	@Override
	public ResponseEntity<Object> deletePost(long postId) throws ResourceNotFoundException {
		log.info("***** Inside PostServiceImpl - deletePost *****");
		response = new HashMap<>();
		Posts postData = postRepo.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post with the Id " + postId + " not found"));
		if (!postData.getPostImageUrl().equals("emptyPostImageUrl")) {
			commonFunctions.deleteObject(postData.getPostImageUrl());
		}
		postRepo.delete(postData);
		response.put(CommonMessages.STATUS, CommonMessages.SUCCESS);
		response.put(CommonMessages.MESSAGE, MSG.POST_DELETE_SUCCESSFUL);
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

//	@Override
//	public ResponseEntity<Object> getAllPostDetails(long userId) {
//		log.info("***** Inside PostServiceImpl - getAllpostDetails");
//		response = new HashMap<>();
//		List<Posts> postData = postRepo.findByUserUserId(userId);
//		if (postData == null || postData.isEmpty()) {
//			response.put("status", MSG.FAILED);
//			response.put("message", MSG.POST_NOT_FOUND);
//			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
//		} else {
//			response.put("postsData", postData);
//			return new ResponseEntity<>(response, HttpStatus.OK);
//		}
//	}

	@Transactional
	@Override
	public ResponseEntity<Object> getAllPostData(long userId, int page, int size) {
		log.info("***** Inside PostServiceImpl - getAllPostData");
		response = new HashMap<>();
		Pageable pageable = PageRequest.of(page, size);
		// Page<Posts> postData =
		// pagePostRepo.findByUserUserIdOrderByPostCreatedAtDesc(userId,pageable);
		Page<PostIndexPage> postData = postRepo.findAllPostDataById(userId, pageable);

		if (postData == null || postData.isEmpty()) {
			response.put("status", CommonMessages.FAILED);
			response.put("message", MSG.POST_NO_MORE_POST);
			return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
		} else {
			// convert time to ISO
			commonFunctions.updatePostTimeAgo(postData.getContent());
			response.put("postsData", postData.getContent());
			response.put(MSG.TOTAL_PAGES, postData.getTotalPages());
			response.put(MSG.CURRENT_PAGE, postData.getNumber());
			response.put(MSG.TOTAL_ELEMENTS, postData.getTotalElements());
			response.put(MSG.PAGE_SIZE, postData.getSize());
			response.put(MSG.HAS_NEXT_PAGE, postData.hasNext());
			response.put(MSG.NEXT_PAGE_NO, page + 1);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@Transactional
	@Override
	public ResponseEntity<Object> getAllPostDetailsByDistricts(long districtId) {
		log.info("***** Inside PostServiceImpl - getAllpostDetails");
		response = new HashMap<>();
		/* get district details */
		districtRepo.findById(districtId)
				.orElseThrow(() -> new ResourceNotFoundException("District with the Id" + districtId + " not found"));
		List<Posts> getPostByDistrict = postRepo.findByDistrictDistrictIdAndPostOwnerType(districtId,
				PostOwnerType.ADMIN);
		
		if (getPostByDistrict == null || getPostByDistrict.isEmpty()) {
			response.put(CommonMessages.STATUS, CommonMessages.FAILED);
			response.put(CommonMessages.MESSAGE, MSG.POST_NOT_FOUND);
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		} else {
			List<AdminPostAdvertisementModel> postDTO = postMapper.convertToDto(getPostByDistrict);
			response.put("postData", postDTO);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@Transactional
	@Override
	public ResponseEntity<Object> savePost(long userId, long postId) {
		log.info("***** Inside PostServiceImpl - savePost");
		response = new HashMap<>();
		User user = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User with Id " + userId + " not found"));
		Posts pData = postRepo.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post with the Id " + postId + " not found"));
		if (postRepo.findByUserUserIdAndPostId(userId, postId).isPresent()) {
			throw new ResourceNotFoundException("You cannot save your own post");
		}
		UserSavedPost usData = userSavePostRepo.findByUserUserIdAndPostPostId(userId, postId);
		if (usData == null) {
			UserSavedPost uspData = new UserSavedPost();
			uspData.setUser(user);
			uspData.setPost(pData);
			userSavePostRepo.save(uspData);
			response.put(CommonMessages.STATUS, CommonMessages.SUCCESS);
			response.put(CommonMessages.MESSAGE, MSG.POST_SAVED_SUCCESSFULL);
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} else {
			response.put(CommonMessages.STATUS, CommonMessages.FAILED);
			response.put(CommonMessages.MESSAGE, MSG.POST_ALREADY_SAVED);
			return new ResponseEntity<>(response, HttpStatus.CONFLICT);
		}
	}

	@Transactional
	@Override
	public ResponseEntity<Object> getSavedPost(long userId, int size, int page) {
		log.info("***** Inside PostServiceImpl - getSavedPost");
		response = new HashMap<>();

		Pageable pageable = PageRequest.of(page, size);
		Page<PostIndexPage> indexDataPage = postRepo.findAllSavedPost(pageable, userId);
		if (indexDataPage.isEmpty()) {
			response.put(CommonMessages.STATUS, CommonMessages.FAILED);
			response.put(CommonMessages.MESSAGE, MSG.POST_NO_MORE_POST);
			return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
		} else {
			List<PostIndexPage> indexData = indexDataPage.getContent();
			for (PostIndexPage postIndex : indexData) {
				// Set all comments on particular post
				postIndex.setCommentsAndReacts(homeServiceDao.getPostIndexDataComments(postIndex.getPostId()));
			}
			response.put("savedPostData", indexData);
			response.put(MSG.TOTAL_PAGES, indexDataPage.getTotalPages());
			response.put(MSG.CURRENT_PAGE, indexDataPage.getNumber());
			response.put(MSG.TOTAL_ELEMENTS, indexDataPage.getTotalElements());
			response.put(MSG.PAGE_SIZE, indexDataPage.getSize());
			response.put(MSG.HAS_NEXT_PAGE, indexDataPage.hasNext());
			response.put(MSG.NEXT_PAGE_NO, page + 1);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@Transactional
	@Override
	public ResponseEntity<Object> deletSavedPost(long userId, long postId) {
		log.info("***** Inside PostServiceImpl - deletSavedPost");
		response = new HashMap<>();
		UserSavedPost usData = userSavePostRepo.findByUserUserIdAndPostPostId(userId, postId);
		if (usData != null) {
			userSavePostRepo.delete(usData);
			response.put(CommonMessages.STATUS, CommonMessages.SUCCESS);
			response.put(CommonMessages.MESSAGE, MSG.POST_SAVED_POST_DELETE_SUCCESSFULL);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			response.put(CommonMessages.STATUS, CommonMessages.FAILED);
			response.put(CommonMessages.MESSAGE, MSG.POST_NOT_FOUND);
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}

	}

//	@Override
//	public ResponseEntity<Object> getAllReelsData(long userId, int page, int size) {
//		log.info("***** Inside PostServiceImpl - getAllPostData");
//		response = new HashMap<>();
//		Pageable pageable = PageRequest.of(page, size);
//		// Page<Posts> postData =
//		// pagePostRepo.findByUserUserIdOrderByPostCreatedAtDesc(userId,pageable);
//		Page<PostIndexPage> postData = postRepo.findAllReelsDataById(userId, pageable);
//		if (postData == null || postData.isEmpty()) {
//			response.put("status", MSG.FAILED);
//			response.put("message", MSG.POST_NO_MORE_POST);
//			return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
//		} else {
//			// convert time to ISO
//			commonFunctions.updatePostTimeAgo(postData.getContent());
//			response.put("postsData", postData.getContent());
//			response.put(MSG.TOTAL_PAGES, postData.getTotalPages());
//			response.put(MSG.CURRENT_PAGE, postData.getNumber());
//			response.put(MSG.TOTAL_ELEMENTS, postData.getTotalElements());
//			response.put(MSG.PAGE_SIZE, postData.getSize());
//			response.put(MSG.HAS_NEXT_PAGE, postData.hasNext());
//			response.put(MSG.NEXT_PAGE_NO, page + 1);
//			return new ResponseEntity<>(response, HttpStatus.OK);
//		}
//	}
	
	
	
	@Override
	public ResponseEntity<Object> getAllReelsData(long userId, int page, int size) {
	    log.info("***** Inside PostServiceImpl - getAllPostData");
	    response = new HashMap<>();
	    Pageable pageable = PageRequest.of(page, size);

	    // Fetch posts using the repository method
	    Page<PostIndexPage> postData = postRepo.findAllPostDataById(userId, pageable);

	    if (postData == null || postData.isEmpty()) {
	        response.put("status", MSG.FAILED);
	        response.put("message", MSG.POST_NO_MORE_POST);
	        // Return 200 OK with a message indicating no posts were found
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    } else {
	        // Convert time to ISO
	        commonFunctions.updatePostTimeAgo(postData.getContent());
	        
	        
//	        // Fetch and set comment count for each post
//            postData.getContent().forEach(post -> {
//                Long commentCount = postCommentRepo.getCommentCountByPostId(post.getPostId());
//                post.setTotalComments(commentCount); // ✅ Dynamically set the count
//            });
	        
	        
	        postData.getContent().forEach(post -> {
	            // Fetch all comments
	            List<PostIndexComments> allComments = postCommentRepo.findAllCommentsByPostId(post.getPostId());

	            // Convert flat comment list to nested hierarchy
	            List<PostIndexComments> nestedComments = buildCommentHierarchy(allComments);

	            post.setTotalComments((long) allComments.size());// ✅ Set total comments count
	            post.getCommentsAndReacts().addAll(nestedComments); // ✅ Attach nested comments to the list
	        });

	        // Prepare the response
	        response.put("postsData", postData.getContent());
	        response.put(MSG.TOTAL_PAGES, postData.getTotalPages());
	        response.put(MSG.CURRENT_PAGE, postData.getNumber());
	        response.put(MSG.TOTAL_ELEMENTS, postData.getTotalElements());
	        response.put(MSG.PAGE_SIZE, postData.getSize());
	        response.put(MSG.HAS_NEXT_PAGE, postData.hasNext());
	        response.put(MSG.NEXT_PAGE_NO, page + 1);

	        return new ResponseEntity<>(response, HttpStatus.OK);
	    }
	}
	
	
	private List<PostIndexComments> buildCommentHierarchy(List<PostIndexComments> comments) {
	    Map<Long, PostIndexComments> commentMap = new HashMap<>();
	    List<PostIndexComments> rootComments = new ArrayList<>();

	    // Store all comments in a map for quick lookup
	    for (PostIndexComments comment : comments) {
	        comment.setChildren(new ArrayList<>()); // Initialize children list
	        commentMap.put(comment.getChildCommentId(), comment);
	    }

	    // Build hierarchy
	    for (PostIndexComments comment : comments) {
	        if (comment.getParentCommentId() == 0) {
	            rootComments.add(comment); // Top-level comment
	        } else {
	            PostIndexComments parent = commentMap.get(comment.getParentCommentId());
	            if (parent != null) {
	                parent.getChildren().add(comment); // Attach as child comment
	            }
	        }
	    }

	    return rootComments;
	}

	// Helper method to extract hashtags from the post name
	private List<String> extractHashtags(String postName) {
		List<String> hashtags = new ArrayList<>();
		Pattern hashtagPattern = Pattern.compile("#(\\w+)");
		Matcher matcher = hashtagPattern.matcher(postName);

		while (matcher.find()) {
			hashtags.add(matcher.group(1)); // Extract the hashtag (without #)
		}

		return hashtags;
	}

	@Transactional
	@Override
	public ResponseEntity<Object> retrieveAds(String postCategory) {
		log.info("***** Inside PostServiceImpl - getAllpostDetails");
		response = new HashMap<>();
		PostOwnerType categoryEnum = PostOwnerType.valueOf(postCategory);
		List<Posts> getPostByDistrict = postRepo.findByDistrictDistrictIdAndPostOwnerType(Long.valueOf(37),
				categoryEnum);
System.out.println(categoryEnum+"****************************");
		if (getPostByDistrict == null || getPostByDistrict.isEmpty()) {
			response.put(CommonMessages.STATUS, CommonMessages.FAILED);
			response.put(CommonMessages.MESSAGE, MSG.POST_NOT_FOUND);
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		} else {
			List<AdminPostAdvertisementModel> postDTO = postMapper.convertToDto(getPostByDistrict);
			response.put("postData", postDTO);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}
	
	
//	@Transactional
//	@Override
//	public ResponseEntity<Object> retrieveAds(String postCategory) {
//	    log.info("***** Inside PostServiceImpl - getAllpostDetails *****");
//
//	    response = new HashMap<>();
//	    
//	    // Validate and parse the post category enum
//	    PostOwnerType categoryEnum;
//	    try {
//	        categoryEnum = PostOwnerType.valueOf(postCategory);
//	    } catch (IllegalArgumentException e) {
//	        log.error("Invalid post category: " + postCategory, e);
//	        response.put(CommonMessages.STATUS, CommonMessages.FAILED);
//	        response.put(CommonMessages.MESSAGE, "Invalid post category: " + postCategory);
//	        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//	    }
//	    
//	    log.info("Fetching posts for category: " + categoryEnum);
//	    
//	    // Fetch posts by district and category
//	    List<Posts> getPostByDistrict = postRepo.findByDistrictDistrictIdAndPostOwnerType(37L, categoryEnum);
//	    log.info("Fetched Posts Count: " + getPostByDistrict.size());
//	    
//	    if (getPostByDistrict.isEmpty()) {
//	        response.put(CommonMessages.STATUS, CommonMessages.FAILED);
//	        response.put(CommonMessages.MESSAGE, MSG.POST_NOT_FOUND);
//	        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
//	    }
//	    
//	    // Convert posts to DTO
//	    List<AdminPostAdvertisementModel> postDTO = postMapper.convertToDto(getPostByDistrict);
//	    log.info("Converted Post DTOs: " + postDTO);
//	    
//	    // Log each video URL for debugging
//	    postDTO.forEach(post -> log.info("Video URL: " + post.getVideoThumbnailUrl()));
//	    
//	    response.put("postData", postDTO);
//	    return new ResponseEntity<>(response, HttpStatus.OK);
//	}

	@Transactional
	@Override
	public ResponseEntity<Object> updatePostDetails(long postId, PostUploadModel post) {
		log.info("***** Inside PostServiceImpl - updatePost *****");
		Map<String, Object> response = new HashMap<>();
		Posts postEntity = postRepo.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post with the Id " + postId + " not found"));
		// Update post name
		postEntity.setPostName(post.getPostName());

		// Extract and update hashtags
		if (post.getPostName() != null && !post.getPostName().isEmpty()) {
			List<String> hashtags = new ArrayList<>();
			String[] words = post.getPostName().split(" ");

			for (String word : words) {
				if (word.startsWith("#") && word.length() > 1) {
					String hashtagName = word.replaceAll("[^\\w#]", "");
					if (hashtagName.length() > 1 && !hashtags.contains(hashtagName)) {
						hashtags.add(hashtagName);
					}
				}
			}

			if (!hashtags.isEmpty()) {
				List<Hashtag> hashtagEntities = new ArrayList<>();
				for (String hashtagName : hashtags) {
					Optional<Hashtag> existingHashtag = hashtagRepository.findByName(hashtagName);
					Hashtag hashtag = existingHashtag.orElseGet(() -> {
						Hashtag newHashtag = new Hashtag();
						newHashtag.setName(hashtagName);
						newHashtag.setCount(0);
						return hashtagRepository.save(newHashtag);
					});

					hashtagEntities.add(hashtag);
					hashtag.setCount(hashtag.getCount() + 1);
					hashtagRepository.save(hashtag);
				}

				postEntity.setHashtags(hashtagEntities);
			}
		}

		// Update metadata if no image is provided
		if (post.getPostImage() == null || post.getPostImage().isEmpty()) {
			System.out.println("inside metadata");
			Pattern pattern = Pattern.compile(CommonMessages.METADATAURLPATTERN);
			Matcher matcher = pattern.matcher(post.getPostName());

			boolean metaData = matcher.find();

			if (metaData) {
				String extractedUrl = matcher.group(0);
				try {
					Document doc = Jsoup.connect(extractedUrl).get();
					String title = doc.select("meta[property=og:title]").attr("content");
					if (title.isEmpty()) {
						title = doc.title();
					}
					String description = doc.select("meta[property=og:description]").attr("content");
					if (description.isEmpty()) {
						description = doc.select("meta[name=description]").attr("content");
					}
					String imageUrl = doc.select("meta[property=og:image]").attr("content");

					postEntity.setAdvertisementDescription(description);
					postEntity.setPostImageUrl(imageUrl);
					postEntity.setVideoThumbnailUrl(title);
					postEntity.setPostType("link");

				} catch (IOException e) {
					response.put("error", "Failed to fetch metadata: " + e.getMessage());
				}
			} else {
//				postEntity.setPostName(post.getPostName());
			}
		}

		// Update mentions
		if (post.getMentionId() != null && !post.getMentionId().isEmpty()) {
			List<User> mentionedUsers = userRepo.findAllById(post.getMentionId());

			// Remove existing mentions to avoid duplication
			postMentionRepo.deleteByPost(postEntity);

			// Add new mentions
			List<PostMention> postMentions = new ArrayList<>();
			for (User user : mentionedUsers) {
				PostMention mention = new PostMention();
				mention.setPost(postEntity);
				mention.setMentionedUser(user);
				postMentions.add(mention);
			}

			// Save new mentions
			postMentionRepo.saveAll(postMentions);
		}

		// Update post type and video thumbnail URL
		postEntity.setPostType(post.getPostType());
		postEntity.setVideoThumbnailUrl(post.getVideoThumbnailUrl());

		// Update post image if provided
		if (post.getPostImage() != null && !post.getPostImage().isEmpty()) {
			try {
				String contentType = post.getPostImage().getContentType();

				if (contentType != null) {
					if (contentType.startsWith("video")) {
						String fileName = post.getPostImage().getOriginalFilename();
						if (fileName != null && (fileName.endsWith(".mp4") || fileName.endsWith(".avi")
								|| fileName.endsWith(".mov") || fileName.endsWith(".mkv"))) {
							String videoThumbnailUrl = commonFunctions.generateVideoThumbnail(post.getPostImage());
							postEntity.setVideoThumbnailUrl(videoThumbnailUrl);

							String imageUrl = commonFunctions.saveImageToServer(post.getPostImage());
							postEntity.setPostImageUrl(imageUrl);
						}
					} else if (contentType.startsWith("image")) {
						System.out.println("Inside image");
						String fileName = post.getPostImage().getOriginalFilename();
						if (fileName != null
								&& (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png")
										|| fileName.endsWith(".gif") || fileName.endsWith(".bmp"))) {
							String imageUrl = commonFunctions.saveImageToServer(post.getPostImage());
							postEntity.setPostImageUrl(imageUrl);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		postRepo.save(postEntity);
		response.put("status", "success");
		response.put("message", "Post updated successfully.");
		return ResponseEntity.ok(response);
	}

	@Transactional
	@Override
	public ResponseEntity<Object> getAnnouncementAtUserSide(String postCategory, long districtId) {
		log.info("***** Inside PostServiceImpl - getAnnouncementAtuserside");
		response = new HashMap<>();
//		PostOwnerType categoryEnum = PostOwnerType.valueOf(postCategory);
		Posts getPostByDistrict = postRepo.findLatestPostByDistrictAndOwnerType(districtId,postCategory);
//		System.out.println(getPostByDistrict.getPostName()+""+getPostByDistrict.getPostId()+"************");
//         System.out.println(categoryEnum+"****************************");
         System.out.println(districtId+"****************************");
		if (getPostByDistrict == null) {
			response.put(CommonMessages.STATUS, CommonMessages.FAILED);
			response.put(CommonMessages.MESSAGE, MSG.POST_NOT_FOUND);
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		} else {
			AdminPostAdvertisementModel postDTO = postMapper.convertToDto(getPostByDistrict);
			response.put("postData", postDTO);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@Override
	public ResponseEntity<Object> getAllReels(long userId, int page, int size) {
	    log.info("***** Inside PostServiceImpl - getAllReels *****");
	    Map<String, Object> response = new HashMap<>();
	    Pageable pageable = PageRequest.of(page, size, Sort.by("postCreatedAt").descending());

	    // Fetch reels for the grid layout
	    Page<Posts> reelsPage = postRepo.findByPostOwnerType(PostOwnerType.REEL, pageable);

	    if (reelsPage == null || reelsPage.isEmpty()) {
	        response.put("status", "FAILED");
	        response.put("message", "No reels found");
	        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
	    } else {
	        response.put("reels", reelsPage.getContent());
	        response.put("totalPages", reelsPage.getTotalPages());
	        response.put("currentPage", reelsPage.getNumber());
	        response.put("totalElements", reelsPage.getTotalElements());
	        response.put("pageSize", reelsPage.getSize());
	        response.put("hasNextPage", reelsPage.hasNext());
	        response.put("nextPageNo", page + 1);
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    }
	}

	
	@Override
	public ResponseEntity<Object> getReelFeed(long reelId, long userId, int size) {
	    log.info("***** Inside PostServiceImpl - getReelFeed *****");
	    Map<String, Object> response = new HashMap<>();

	    // Fetch the current reel
	    Posts currentReel = postRepo.findById(reelId)
	        .orElseThrow(() -> new ResourceNotFoundException("Reel not found"));

	    // Fetch the next set of reels (excluding the current reel)
	    Page<Posts> nextReels = postRepo.findByPostOwnerTypeAndPostIdNot(
	        PostOwnerType.REEL,
	        reelId,
	        PageRequest.of(0, size, Sort.by("postCreatedAt").descending())
	    );

	    response.put("currentReel", currentReel);
	    response.put("nextReels", nextReels.getContent());
	    response.put("hasNextPage", nextReels.hasNext());
	    return ResponseEntity.ok(response);
	}



	@Override
	public ResponseEntity<Object> getAllReelsAll(int page, int size) {
	    log.info("***** Inside PostServiceImpl - getAllReelsAll *****");
	    
	    Map<String, Object> response = new HashMap<>();
	    Pageable pageable = PageRequest.of(page, size, Sort.by("postCreatedAt").descending());

	    // Fetch all reels without filtering by userId
	    Page<Posts> reelsPage = postRepo.findByPostOwnerType(PostOwnerType.REEL, pageable);

	    if (reelsPage == null || reelsPage.isEmpty()) {
	        response.put("status", "FAILED");
	        response.put("message", "No reels found");
	        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
	    } else {
	        response.put("reels", reelsPage.getContent());
	        response.put("totalPages", reelsPage.getTotalPages());
	        response.put("currentPage", reelsPage.getNumber());
	        response.put("totalElements", reelsPage.getTotalElements());
	        response.put("pageSize", reelsPage.getSize());
	        response.put("hasNextPage", reelsPage.hasNext());
	        response.put("nextPageNo", page + 1);
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    }
	}
}

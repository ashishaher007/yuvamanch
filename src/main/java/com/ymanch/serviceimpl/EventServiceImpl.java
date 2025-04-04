package com.ymanch.serviceimpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.ymanch.dao.HomeServiceDao;
import com.ymanch.entity.AddParticipant;
import com.ymanch.entity.District;
import com.ymanch.entity.EventCategory;
import com.ymanch.entity.Events;
import com.ymanch.entity.Posts;
import com.ymanch.entity.User;
import com.ymanch.exception.ConflictException;
import com.ymanch.exception.ResourceNotFoundException;
import com.ymanch.helper.ApiResponse;
import com.ymanch.helper.CommonFunctions;
import com.ymanch.helper.CommonMessages;
import com.ymanch.helper.DateTimeUtil;
import com.ymanch.helper.Enums.PostOwnerType;
import com.ymanch.helper.Enums.Status;
import com.ymanch.model.CustomResponseModel;
import com.ymanch.model.EventModel;
import com.ymanch.model.PostIndexPage;
import com.ymanch.model.UserDetails;
import com.ymanch.repository.AddParticipantRepository;
import com.ymanch.repository.DistrictRepository;
import com.ymanch.repository.EventCategoryRepository;
import com.ymanch.repository.EventRepository;
import com.ymanch.repository.PostRepository;
import com.ymanch.repository.UserRepository;
import com.ymanch.service.EventService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EventServiceImpl implements EventService {

	private Map<Object, Object> response;

	private EventCategoryRepository eventCategoryRepo;
	private CommonMessages messages;
	private UserRepository userRepo;
	private DistrictRepository districtRepo;
	private EventRepository eventRepo;
	private PostRepository postRepo;
	private HomeServiceDao homeServiceDao;
	private CommonFunctions commonFunctions;
	private AddParticipantRepository addParticipantRepo;

	public EventServiceImpl(EventCategoryRepository eventCategoryRepo, CommonMessages messages, UserRepository userRepo,
			DistrictRepository districtRepo, EventRepository eventRepo, PostRepository postRepo,
			HomeServiceDao homeServiceDao, CommonFunctions commonFunctions,
			AddParticipantRepository addParticipantRepo) {
		super();
		this.eventCategoryRepo = eventCategoryRepo;
		this.messages = messages;
		this.userRepo = userRepo;
		this.districtRepo = districtRepo;
		this.eventRepo = eventRepo;
		this.postRepo = postRepo;
		this.homeServiceDao = homeServiceDao;
		this.commonFunctions = commonFunctions;
		this.addParticipantRepo = addParticipantRepo;
	}

	@Transactional
	@Override
	public ResponseEntity<Object> add(EventCategory evCatgeory) {
		log.info("***** Inside EventServiceImpl - add *****");
		response = new HashMap<>();
		Optional<EventCategory> eventCatData = eventCategoryRepo.findByCatName(evCatgeory.getCatName());
		if (eventCatData.isPresent()) {
			throw new ResourceNotFoundException("Category name '" + evCatgeory.getCatName() + "' already exist");
		}

		eventCategoryRepo.save(evCatgeory);
		response.put(CommonMessages.STATUS, CommonMessages.SUCCESS);
		response.put(CommonMessages.MESSAGE, messages.EVENT_CAT_ADD_SUCCESSFUL);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@Transactional
	@Override
	public ResponseEntity<Object> get() {
		log.info("***** Inside EventServiceImpl - get *****");
		response = new HashMap<>();
		List<EventCategory> eventCatData = eventCategoryRepo.findAll();
		if (eventCatData.isEmpty()) {
			throw new ResourceNotFoundException("There are currently no categories for events available");
		}

		response.put("eventCatgDetails", eventCatData);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Transactional
	@Override
	public ResponseEntity<Object> create(long hostUserId, long eventCatgId, long districtId, @Valid EventModel event) {
		log.info("***** Inside EventServiceImpl - create *****");
		response = new HashMap<>();
		User userData = userRepo.findById(hostUserId)
				.orElseThrow(() -> new ResourceNotFoundException("User with the Id " + hostUserId + " not found"));
		District diDetails = districtRepo.findById(districtId)
				.orElseThrow(() -> new ResourceNotFoundException("District with the Id" + districtId + " not found"));
		Optional<EventCategory> catDetails = eventCategoryRepo.findById(eventCatgId);

		if (catDetails.isEmpty() || eventCatgId == 0) {
			throw new ResourceNotFoundException("No such category found");
		}
		Optional<Events> eventData = eventRepo.findByUserUserIdAndEventNameAndEventStatus(hostUserId,
				event.getEventName(), Status.ACTIVE);
		if (eventData.isPresent()) {
			throw new ResourceNotFoundException("An event with the name '" + event.getEventName()
					+ "' is already in progress, please choose a different name");
		}

		if (event.getPostOwnerType().equals(PostOwnerType.PUBLIC_EVENT)
				|| event.getPostOwnerType().equals(PostOwnerType.PRIVATE_EVENT)) {
			Events ev = new Events();
			ev.setEventName(event.getEventName());
			ev.setEventDescription(event.getEventDescription());
			ev.setStartDate(event.getStartDate());
			ev.setStartTime(event.getStartTime());
			ev.setEndDate(event.getEndDate());
			ev.setEndTime(event.getEndTime());
			ev.setEventAddress(event.getEventAddress());
			ev.setEventMode(event.getEventMode());
			ev.setEventNotify(event.getEventNotify());
			ev.setVirtualEventLink(event.getVirtualEventLink());
			if (eventCatgId != 0) {
				ev.setEventCatg(catDetails.get());
			}
			ev.setUser(userData);
			ev.setDistrict(diDetails);
			Events data = eventRepo.save(ev);

			Posts p = new Posts();
			p.setPostName(event.getEventName());
			if (event.getEventImageUrl() != null && !event.getEventImageUrl().isEmpty()) {
				String imageUrl;
				try {
					imageUrl = commonFunctions.saveImageToServer(event.getEventImageUrl());
					p.setPostImageUrl(imageUrl);
				} catch (IOException e) {
					e.printStackTrace();

				}
			} else {
				p.setPostImageUrl(CommonMessages.WALL_PAPER);
			}
			p.setPostType(event.getEventPostType());
			p.setVideoThumbnailUrl(event.getEventVideoThumbnailUrl());
			if (event.getPostOwnerType().equals(PostOwnerType.PUBLIC_EVENT)) {
				p.setPostOwnerType(PostOwnerType.PUBLIC_EVENT);
			} else {
				p.setPostOwnerType(PostOwnerType.PRIVATE_EVENT);
			}
			p.setEvents(data);
			postRepo.save(p);
			response.put(CommonMessages.STATUS, CommonMessages.SUCCESS);
			response.put(CommonMessages.MESSAGE, messages.EVENT_CREATE_SUCCESSFUL);
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} else {
			response.put(CommonMessages.STATUS, CommonMessages.FAILED);
			response.put(CommonMessages.MESSAGE, messages.POST_OWN_TYPE_VALID);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

		}
	}

	@Transactional
	@Override
	public ResponseEntity<Object> delete(long hostUserId, long eventId) {
		log.info("***** Inside EventServiceImpl - create *****");
		response = new HashMap<>();
		Events evData = eventRepo.findByUserUserIdAndEventId(hostUserId, eventId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"We couldn't find the event you were looking for. It might have been deleted or may not exist"));
		eventRepo.delete(evData);
		response.put(CommonMessages.STATUS, CommonMessages.SUCCESS);
		response.put(CommonMessages.MESSAGE, messages.EVENT_DELETE_SUCCESSFUL);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Transactional
	@Override
	public ResponseEntity<Object> getEventDetails(String eventUUID, long userId) {
		log.info("***** Inside EventServiceImpl - getEventDetails *****");
		response = new HashMap<>();
		Optional<Events> results = eventRepo.findByUuid(eventUUID);
		List<Map<String, Object>> eventData = new ArrayList<>();
		if (results.isPresent()) {
			Map<String, Object> eventMap = new HashMap<>();
			eventMap.put("eventName", results.get().getEventName());
			eventMap.put("eventDescription", results.get().getEventDescription());
			eventMap.put("startDate", results.get().getStartDate());
			eventMap.put("startTime", results.get().getStartTime());
			eventMap.put("endDate", results.get().getEndDate());
			eventMap.put("endTime", results.get().getEndTime());
			eventMap.put("eventAddress", results.get().getEventAddress());
			eventMap.put("eventId", results.get().getEventId());
//		eventMap.put("totalInterest", results.get().getTotalInterest());
			eventMap.put("districtName", results.get().getDistrict().getDistrictName());
			eventMap.put("categoryName", results.get().getEventCatg().getCatName());
			eventMap.put("postName", results.get().getPosts().get(0).getPostName());
			eventMap.put("postImageUrl", results.get().getPosts().get(0).getPostImageUrl());
			eventMap.put("eventMode", results.get().getEventMode());
			eventMap.put("eventNotify", results.get().getEventNotify());
			eventMap.put("virtualEventLink", results.get().getVirtualEventLink());
			// Check if the user is a participant
			boolean isParticipant = addParticipantRepo.existsByParticipantUserUserIdAndEventEventId(userId,
					results.get().getEventId());
			eventMap.put("isParticipant", isParticipant);
			eventData.add(eventMap);
		} else {
			throw new ResourceNotFoundException("Event with the UUID '" + eventUUID + "' not found");
		}

		List<PostIndexPage> indexDataPage = eventRepo.findAllPost(results.get().getPosts().get(0).getPostId());
		for (PostIndexPage postIndex : indexDataPage) {
			// convert the time in ISO
			String timeAgo = DateTimeUtil.convertToTimeAgo(postIndex.getPostCreatedAt());
			postIndex.setPostUploadedAt(timeAgo);
			// Set all comments on particular post
			postIndex.setCommentsAndReacts(homeServiceDao.getPostIndexDataComments(postIndex.getPostId()));
		}
		response.put("about", eventData);
		response.put("discussion", indexDataPage);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Transactional
	@Override
	public ResponseEntity<Object> getAllEvents(int page, int size, long districtId) {
		log.info("***** Inside EventServiceImpl - create *****");
		response = new HashMap<>();
		// Update ended events
		// Update statuses before fetching events
		eventRepo.updateEndedEventsBatch();
		eventRepo.updateActiveEventsBatch();

		Pageable pageable = PageRequest.of(page, size);
		Page<Object[]> results = eventRepo.findAllEvents(pageable, districtId);
		List<Map<String, Object>> eventData = new ArrayList<>();
		for (Object[] row : results) {
			Map<String, Object> eventMap = new HashMap<>();
			eventMap.put("eventName", row[0]);
			eventMap.put("eventDescription", row[1]);
			eventMap.put("startDate", row[2]);
			eventMap.put("startTime", row[3]);
			eventMap.put("endDate", row[4]);
			eventMap.put("endTime", row[5]);
			eventMap.put("eventAddress", row[6]);
			eventMap.put("eventId", row[7]);
			eventMap.put("totalInterest", row[8]);
			eventMap.put("districtName", row[9]);
			eventMap.put("categoryName", row[10]);
			eventMap.put("postName", row[11]);
			eventMap.put("postImageUrl", row[12]);
			eventMap.put("hostUserId", row[13]);
			eventMap.put("uuid", row[14]);
			eventMap.put("eventMode", row[15]);
			eventMap.put("eventNotify", row[16]);
			eventMap.put("virtualEventLink", row[17]);
			eventData.add(eventMap);
		}
		response.put("allEventDetails", eventData);
		response.put(messages.TOTAL_PAGES, results.getTotalPages());
		response.put(messages.CURRENT_PAGE, results.getNumber());
		response.put(messages.TOTAL_ELEMENTS, results.getTotalElements());
		response.put(messages.PAGE_SIZE, results.getSize());
		response.put(messages.HAS_NEXT_PAGE, results.hasNext());
		response.put(messages.NEXT_PAGE_NO, page + 1);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Transactional
	@Override
	public ResponseEntity<Object> getHostEvents(long hostUserId, int page, int size) {
		log.info("***** Inside EventServiceImpl - getHostEvents *****");
		response = new HashMap<>();
		Pageable pageable = PageRequest.of(page, size);
		Page<Object[]> results = eventRepo.findHostEvents(pageable, hostUserId);
		List<Map<String, Object>> eventData = new ArrayList<>();
		for (Object[] row : results) {
			Map<String, Object> eventMap = new HashMap<>();
			eventMap.put("eventName", row[0]);
			eventMap.put("eventDescription", row[1]);
			eventMap.put("startDate", row[2]);
			eventMap.put("startTime", row[3]);
			eventMap.put("endDate", row[4]);
			eventMap.put("endTime", row[5]);
			eventMap.put("eventAddress", row[6]);
			eventMap.put("eventId", row[7]);
			eventMap.put("totalInterest", row[8]);
			eventMap.put("districtName", row[9]);
			eventMap.put("categoryName", row[10]);
			eventMap.put("postName", row[11]);
			eventMap.put("postImageUrl", row[12]);
			eventMap.put("hostUserId", row[13]);
			eventMap.put("uuid", row[14]);
			eventMap.put("eventMode", row[15]);
			eventMap.put("eventNotify", row[16]);
			eventMap.put("virtualEventLink", row[17]);
			eventData.add(eventMap);
		}
		response.put("allEventDetails", eventData);
		response.put(messages.TOTAL_PAGES, results.getTotalPages());
		response.put(messages.CURRENT_PAGE, results.getNumber());
		response.put(messages.TOTAL_ELEMENTS, results.getTotalElements());
		response.put(messages.PAGE_SIZE, results.getSize());
		response.put(messages.HAS_NEXT_PAGE, results.hasNext());
		response.put(messages.NEXT_PAGE_NO, page + 1);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public ResponseEntity<Object> edit(long hostUserId, long eventId, EventModel event) {
		log.info("***** Inside EventServiceImpl - edit *****");
		response = new HashMap<>();

		Events evData = eventRepo.findByUserUserIdAndEventId(hostUserId, eventId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"We couldn't find the event you were looking for. It might have been deleted or may not exist"));

		// Update event fields
		evData.setEventName(event.getEventName());
		evData.setEventDescription(event.getEventDescription());
		evData.setStartDate(event.getStartDate());
		evData.setStartTime(event.getStartTime());
		evData.setEndDate(event.getEndDate());
		evData.setEndTime(event.getEndTime());
		evData.setEventAddress(event.getEventAddress());

		// Create or update post associated with event
		if (event != null) {
			List<Posts> eventData = evData.getPosts();
			if (eventData == null || eventData.isEmpty()) {
				eventData = new ArrayList<>();
				evData.setPosts(eventData);
			}

			// Get the first post if it exists, or create a new one
			Posts post = eventData.isEmpty() ? new Posts() : eventData.get(0);

			post.setPostName(event.getEventName());
			post.setPostType(event.getEventPostType());
			post.setVideoThumbnailUrl(event.getEventVideoThumbnailUrl());
			post.setEvents(evData);

			// Save the image if eventImageUrl is not empty
			if (event.getEventImageUrl() != null && !event.getEventImageUrl().isEmpty()) {
				try {
					String imageUrl = commonFunctions.saveImageToServer(event.getEventImageUrl());
					post.setPostImageUrl(imageUrl); // Update the image URL in the post
				} catch (IOException e) {
					log.error("Error saving image", e);
					response.put(CommonMessages.STATUS, CommonMessages.FAILED);
					response.put(CommonMessages.MESSAGE, "Failed to save image: " + e.getMessage());
					return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
			// Add or replace the post in the event’s posts list
			if (eventData.isEmpty()) {
				eventData.add(post);
			}
		}
		eventRepo.save(evData);
		response.put(CommonMessages.STATUS, CommonMessages.SUCCESS);
		response.put(CommonMessages.MESSAGE, messages.EVENT_UPDATE_SUCCESSFUL);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Transactional
	@Override
	public ResponseEntity<Object> addParticipant(Long userId, Long eventId) {
		if (log.isInfoEnabled()) {
			log.info("***** Inside EventServiceImpl - addParticipant *****");
		}
		User userData = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User with the Id " + userId + " not found"));
		Events evData = eventRepo.findById(eventId).orElseThrow(() -> new ResourceNotFoundException(
				"We couldn't find the event you were looking for. It might have been deleted or may not exist"));

		if (evData.getUser().getUserId() == userId) {
			ApiResponse apiResponse = new ApiResponse(CommonMessages.FAILED,
					messages.EVENT_USER_ATTEMPT_TO_PARTICIPANT_OWN_EVENT);
			return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
		}
		try {
			System.out.println("Errorrrrr ******");
			AddParticipant ap = new AddParticipant();
			ap.setParticipantUser(userData);
			ap.setEvent(evData);
			System.out.println("Errorrrrr 2******");
			addParticipantRepo.save(ap);
			System.out.println("Errorrrrr 3******");
			ApiResponse apiResponse = new ApiResponse(CommonMessages.SUCCESS,
					messages.EVENT_ADD_PARTICIPANT_SUCCESSFUL);
			return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
		} catch (DataIntegrityViolationException e) {
			throw new ConflictException(messages.PARTICIPANT_ALREADY_PARTICIPATE);

		}

	}
	
	
//	@Transactional
//	@Override
//	public ResponseEntity<Object> addParticipant(Long userId, Long eventId) {
//	    log.info("***** Inside EventServiceImpl - addParticipant *****");
//
//	    // Fetch User and Event, throwing appropriate exceptions if not found
//	    User userData = userRepo.findById(userId)
//	            .orElseThrow(() -> new ResourceNotFoundException("User with the Id " + userId + " not found"));
//
//	    Events evData = eventRepo.findById(eventId).orElseThrow(() -> 
//	            new ResourceNotFoundException("We couldn't find the event you were looking for. It might have been deleted or may not exist"));
//
//	    // Prevent the event creator from participating in their own event
//	    if (userId != null && userId.equals(evData.getUser().getUserId())) 
//
// {
//	        ApiResponse apiResponse = new ApiResponse(CommonMessages.FAILED,
//	                messages.EVENT_USER_ATTEMPT_TO_PARTICIPANT_OWN_EVENT);
//	        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
//	    }
//
//	    // Check if the user is already registered for this specific event
//	    Optional<AddParticipant> existingParticipant = 
//	            addParticipantRepo.findByEventIdAndParticipantUserId(eventId, userId);
//	    
//	    if (existingParticipant.isPresent()) {
//	        throw new ConflictException(messages.PARTICIPANT_ALREADY_PARTICIPATE);
//	    }
//
//	    // Proceed with registration if validation passes
//	    AddParticipant ap = new AddParticipant();
//	    ap.setParticipantUser(userData);
//	    ap.setEvent(evData);
//
//	    addParticipantRepo.save(ap);
//
//	    ApiResponse apiResponse = new ApiResponse(CommonMessages.SUCCESS,
//	            messages.EVENT_ADD_PARTICIPANT_SUCCESSFUL);
//	    return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
//	}


	@Transactional
	@Override
	public ResponseEntity<Object> getParticipants(long hostUserId, String eventUUID, int page, int size) {
		if (log.isInfoEnabled()) {
			log.info("***** Inside EventServiceImpl - getParticipants *****");
		}
		Pageable pageable = PageRequest.of(page, size);
		// Fetch paginated list of user details for the competition
		Page<UserDetails> pageResult = addParticipantRepo.findAllUsersByHostId(pageable, hostUserId, eventUUID);

		// Check if the result page is empty
		if (pageResult.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}

		// Prepare the custom response model with basic pagination data
		CustomResponseModel<UserDetails> customResponse = new CustomResponseModel<>(pageResult.getContent(),
				pageResult.getTotalPages(), pageResult.getNumber(), pageResult.getTotalElements(), pageResult.getSize(),
				pageResult.hasNext(), pageResult.getNumber() + 1);

		// Return the response with the custom response model and status OK
		return new ResponseEntity<>(customResponse, HttpStatus.OK);
	}

	@Transactional
	@Override
	public ResponseEntity<Object> exitEvent(Long eventHostUserId, long userId, long eventId) {
		if (log.isInfoEnabled()) {
			log.info("***** Inside EventServiceImpl - exitEvent *****");
		}
		boolean isAdmin = userId != eventHostUserId;
		AddParticipant participantData = addParticipantRepo.findByParticipantUserUserIdAndEventEventId(userId, eventId)
				.orElseThrow(() -> new ResourceNotFoundException("You are not currently registered for this event"));
		addParticipantRepo.delete(participantData);
		String message = isAdmin ? CommonMessages.PARTICIPANT_ADMIN_REMOVED_SUCCESSFUL
				: CommonMessages.PARTICIPANT_REMOVED_SUCCESSFUL;
		ApiResponse apiResponse = new ApiResponse(CommonMessages.SUCCESS, message);
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

}

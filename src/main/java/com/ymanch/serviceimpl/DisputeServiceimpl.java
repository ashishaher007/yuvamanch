package com.ymanch.serviceimpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ymanch.dao.DisputeDao;
import com.ymanch.entity.DisputeTitles;
import com.ymanch.entity.Disputes;
import com.ymanch.entity.Posts;
import com.ymanch.entity.User;
import com.ymanch.exception.ResourceNotFoundException;
import com.ymanch.helper.CommonMessages;
import com.ymanch.helper.Enums.DisputeStatus;
import com.ymanch.model.AdminDisputeDetails;
import com.ymanch.repository.DisputeRepository;
import com.ymanch.repository.DisputeTitleRepository;
import com.ymanch.repository.PostRepository;
import com.ymanch.repository.UserRepository;
import com.ymanch.service.DisputeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DisputeServiceimpl implements DisputeService {

	private DisputeRepository disputeRepo;
	private DisputeTitleRepository disputeTitleRepo;
	private CommonMessages messages;
	private UserRepository userRepo;
	private PostRepository postRepo;
	private DisputeDao disputeDao;

	public DisputeServiceimpl(DisputeRepository disputeRepo, DisputeTitleRepository disputeTitleRepo,
			CommonMessages messages, UserRepository userRepo, PostRepository postRepo, DisputeDao disputeDao) {
		super();
		this.disputeRepo = disputeRepo;
		this.disputeTitleRepo = disputeTitleRepo;
		this.messages = messages;
		this.userRepo = userRepo;
		this.postRepo = postRepo;
		this.disputeDao = disputeDao;
	}

	@Override
	public ResponseEntity<Object> raiseDispute(long userId, long postId, long disputeTitleId, Disputes dispute) {
		log.info("***** Inside DisputeServiceimpl - raiseDispute *****");
		Map<Object, Object> response = new HashMap<>();
		User userdata = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User with the Id " + userId + " not found"));
		Posts postData = postRepo.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post with the Id " + postId + " not found"));
		DisputeTitles dTitleData = disputeTitleRepo.findById(disputeTitleId).orElseThrow(
				() -> new ResourceNotFoundException("Dispute title with the Id " + disputeTitleId + " not found"));

		Optional<Disputes> disputeDetails = disputeRepo.findByUserUserIdAndPostPostId(userId, postId);
		if (disputeDetails.isPresent()) {
			response.put(messages.STATUS, messages.FAILED);
			response.put(messages.MESSAGE, messages.DISPUTE_ALREADY_RAISED);
			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
		} else {
			dispute.setUser(userdata);
			dispute.setPost(postData);
			dispute.setDisputeTitle(dTitleData);
			dispute.setDisputeStatus(DisputeStatus.PENDING);
			disputeRepo.save(dispute);
			response.put(messages.STATUS, messages.SUCCESS);
			response.put(messages.MESSAGE, messages.DISPUTE_ALREADY_RAISED);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		}

	}

	@Override
	public ResponseEntity<Object> addDisputesTitles(@Valid DisputeTitles disputeTitles) {
		log.info("***** Inside DisputeServiceimpl - addDisputesTitles *****");
		Map<Object, Object> response = new HashMap<>();
		DisputeTitles dTitleData = disputeTitleRepo.findByDisputeTitle(disputeTitles.getDisputeTitle());

		if (dTitleData == null || !dTitleData.getDisputeTitle().equals(disputeTitles.getDisputeTitle())) {
			disputeTitleRepo.save(disputeTitles);
			response.put(messages.STATUS, messages.SUCCESS);
			response.put(messages.MESSAGE, messages.DISPUTE_DTITLE_ADD_SUCCESSFUL);
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} else {
			response.put(messages.STATUS, messages.FAILED);
			response.put(messages.MESSAGE, messages.DISPUTE_DTITLE_ALREADY_PRESENT);
			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
		}

	}

	@Override
	public ResponseEntity<Object> getDisputesTitles() {
		log.info("***** Inside DisputeServiceimpl - getDisputesTitles *****");
		Map<Object, Object> response = new HashMap<>();
		List<DisputeTitles> dTitleData = disputeTitleRepo.findAll();
		if (dTitleData.isEmpty() || dTitleData == null) {
			response.put(messages.STATUS, messages.FAILED);
			response.put(messages.MESSAGE, messages.DISPUTE_DTITLE_NOT_FOUND);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		} else {
			response.put("disputeTitles", dTitleData);
			return ResponseEntity.status(HttpStatus.OK).body(response);
		}
	}

	@Override
	public ResponseEntity<Object> getAllDisputedPost(Long adminId, int page, int size) throws Exception {
		log.info("***** Inside DisputeServiceimpl - getAllDisputedPost *****");
		Map<Object, Object> response = new HashMap<>();
		Pageable pageable = PageRequest.of(page, size);
		User userdata = userRepo.findById(adminId)
				.orElseThrow(() -> new ResourceNotFoundException("Admin with the Id " + adminId + " not found"));
		Page<AdminDisputeDetails> data = disputeRepo.getDisputeDetails(userdata.getDistrict().getDistrictId(),
				pageable);
		response.put("diputeDetails", data.getContent());
		response.put(messages.TOTAL_PAGES, data.getTotalPages());
		response.put(messages.CURRENT_PAGE, data.getNumber());
		response.put(messages.TOTAL_ELEMENTS, data.getTotalElements());
		response.put(messages.PAGE_SIZE, data.getSize());
		response.put(messages.HAS_NEXT_PAGE, data.hasNext());
		response.put(messages.NEXT_PAGE_NO, page + 1);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Override
	public ResponseEntity<Object> getAllDisPost(HttpServletRequest request, int page, int size) {
		log.info("***** Inside DisputeServiceimpl - getAllDisputedPost *****");
		Map<Object, Object> response = new HashMap<>();
		Pageable pageable = PageRequest.of(page, size);
		Page<AdminDisputeDetails> data = disputeRepo.getAllDisputeDetails(pageable);
		response.put("allDiputeDetails", data.getContent());
		response.put(messages.TOTAL_PAGES, data.getTotalPages());
		response.put(messages.CURRENT_PAGE, data.getNumber());
		response.put(messages.TOTAL_ELEMENTS, data.getTotalElements());
		response.put(messages.PAGE_SIZE, data.getSize());
		response.put(messages.HAS_NEXT_PAGE, data.hasNext());
		response.put(messages.NEXT_PAGE_NO, page + 1);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

}

package com.ymanch.serviceimpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ymanch.dao.GroupServiceDao;
import com.ymanch.entity.Group;
import com.ymanch.entity.Pages;
import com.ymanch.entity.User;
import com.ymanch.exception.ResourceNotFoundException;
import com.ymanch.helper.CommonFunctions;
import com.ymanch.helper.CommonMessages;
import com.ymanch.mapper.PagesMapper;
import com.ymanch.model.PageGroupUpdateModel;
import com.ymanch.model.PagesModel;
import com.ymanch.model.PagesModelV1;
import com.ymanch.model.PostIndexPage;
import com.ymanch.repository.PagesRepository;
import com.ymanch.repository.PostRepository;
import com.ymanch.repository.UserRepository;
import com.ymanch.service.PagesService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PagesServiceImpl implements PagesService {
	private Map<Object, Object> response;

	private CommonMessages commonMessages;
	private PagesRepository pageRepo;
	private UserRepository userRepo;
	private PagesMapper pagesMapper;
	private CommonFunctions commonFunctions;

	public PagesServiceImpl(CommonMessages commonMessages, PagesRepository pageRepo, UserRepository userRepo,
			PagesMapper pagesMapper, CommonFunctions commonFunctions) {
		super();
		this.commonMessages = commonMessages;
		this.pageRepo = pageRepo;
		this.userRepo = userRepo;
		this.pagesMapper = pagesMapper;
		this.commonFunctions = commonFunctions;
	}

	@Transactional
	@Override
	public ResponseEntity<Object> createPage(long adminUserId, @Valid PagesModel pages) {
		log.info("***** Inside - PagesServiceImpl - createPage *****");
		response = new HashMap<>();

		// Retrieve the admin user from the repository
		User admin = userRepo.findById(adminUserId).orElseThrow(
				() -> new ResourceNotFoundException("Admin user with the Id" + adminUserId + " not found"));
		/* Check for page name. Page nmae must be unique */
		Pages pageData = pageRepo.findByPageName(pages.getPageName());
		if (pageData != null) {
			response.put(CommonMessages.STATUS, CommonMessages.FAILED);
			response.put(CommonMessages.MESSAGE, commonMessages.PAGE_NAME_ALREADY_EXIT);
			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
		}
		Pages pageObj = new Pages();
		pageObj.setPageName(pages.getPageName());
		pageObj.setPageDescription(pages.getPageDescription());
//		pageObj.setPageCoverProfileImagePath(pages.getPageCoverProfileImagePath());
		pageObj.setLinkUrlName(pages.getLinkUrlName());
		pageObj.setLinkUrl(pages.getLinkUrl());
		pageObj.setUser(admin);
		pageRepo.save(pageObj);
		response.put(CommonMessages.STATUS, CommonMessages.SUCCESS);
		response.put(CommonMessages.MESSAGE, commonMessages.PAGE_CREATED_SUCCESSFUL);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

//	@Override
//	public ResponseEntity<Object> getPagesByUserId(long userId, int page, int size) {
//		log.info("***** Inside - PagesServiceImpl - getPagesByUserId *****");
//		response = new HashMap<>();
//		Pageable pageable = PageRequest.of(page, size);
//		User user = userRepo.findById(userId)
//				.orElseThrow(() -> new ResourceNotFoundException("User with Id " + userId + " not found"));
//
//		// Retrieve pages where the user is a member
//		Page<Pages> pageData = pageRepo.findByMembersContaining(userId, pageable);
//
//		List<Pages> pages = new ArrayList<>(pageData.getContent());
//
//		// Also add pages where the user is the admin
//		List<Pages> adminPage = pageRepo.findByUser(user);
//
//		for (Pages pageDetails : adminPage) {
//			if (!pages.contains(pageDetails)) {
//				pages.add(pageDetails);
//			}
//		}
//
//		if (pages.isEmpty()) {
//			response.put(commonMessages.STATUS, commonMessages.FAILED);
//			response.put(commonMessages.MESSAGE, commonMessages.PAGE_NO_PAGES);
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//		}
//
//		// Convert Pages entities to PageDTOs
//		List<PagesModelV1> pageDTOs = pages.stream().map(this::convertToDTO).collect(Collectors.toList());
//
//		response.put("pageDetails", pageDTOs);
//		return ResponseEntity.status(HttpStatus.OK).body(response);
//	}
//
//	private PagesModelV1 convertToDTO(Pages page) {
//		PagesModelV1 dto = new PagesModelV1();
//		dto.setPagesId(page.getPageId());
//		dto.setPageName(page.getPageName());
//		dto.setPageDescription(page.getPageDescription());
//		dto.setPageCreatedAt(page.getPageCreatedAt());
//		dto.setPageCoverProfileImagePath(page.getPageCoverProfileImagePath());
//
//		// Populate admin details
//		User admin = page.getUser();
//		if (admin != null) {
//			dto.setAdminId(admin.getUserId());
//			dto.setAdminUserFirstName(admin.getUserFirstName());
//			dto.setAdminUserLastName(admin.getUserLastName());
//			dto.setAdminUserProfileImagePath(admin.getUserProfileImagePath());
//		}
//
//		return dto;
//	}

	@Transactional
	@Override
	public ResponseEntity<Object> getPagesByUserId(long userId, int page, int size) {
		log.info("***** Inside - PagesServiceImpl - getPagesByUserId *****");
		response = new HashMap<>();
		Pageable pageable = PageRequest.of(page, size);
		userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User with Id " + userId + " not found"));

		Page<PagesModelV1> pagesData = pageRepo.findOwnPageDetails(userId, pageable);
		if (pagesData.isEmpty()) {
			response.put(CommonMessages.STATUS, CommonMessages.FAILED);
			response.put(CommonMessages.MESSAGE, commonMessages.PAGE_NO_PAGES);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}

		response.put("pagesDetail", pagesData.getContent());
		response.put(commonMessages.TOTAL_PAGES, pagesData.getTotalPages());
		response.put(commonMessages.CURRENT_PAGE, pagesData.getNumber());
		response.put(commonMessages.TOTAL_ELEMENTS, pagesData.getTotalElements());
		response.put(commonMessages.PAGE_SIZE, pagesData.getSize());
		response.put(commonMessages.HAS_NEXT_PAGE, pagesData.hasNext());
		response.put(commonMessages.NEXT_PAGE_NO, page + 1);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Transactional
	@Override
	public ResponseEntity<Object> getAllPages(long userId, int page, int size) {
		log.info("***** Inside - PagesServiceImpl - getAllPages *****");
		response = new HashMap<>();
		Pageable pageable = PageRequest.of(page, size);
		Page<Object[]> pagesData = pageRepo.findAllPageDetails(userId, pageable);
		if (pagesData == null || pagesData.isEmpty()) {
			response.put(CommonMessages.STATUS, CommonMessages.FAILED);
			response.put(CommonMessages.MESSAGE, commonMessages.PAGE_NO_PAGES);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}

		// Convert each Object[] to a map of key-value pairs
		List<Map<String, Object>> pagesDetails = new ArrayList<>();
		for (Object[] pageObj : pagesData.getContent()) {
			Map<String, Object> pageMap = new HashMap<>();
			pageMap.put("pagesId", pageObj[0]);
			pageMap.put("pageName", pageObj[1]);
			pageMap.put("pageDescription", pageObj[2]);
			pageMap.put("pageCoverProfileImagePath", pageObj[3]);
			pageMap.put("adminId", pageObj[4]);
			pageMap.put("adminUserFirstName", pageObj[5]);
			pageMap.put("adminUserLastName", pageObj[6]);
			pageMap.put("adminUserProfileImagePath", pageObj[7]);
			pageMap.put("pageCreatedAt", pageObj[8]);
			pageMap.put("isPageFollowed", pageObj[9]);
			pageMap.put("puuid", pageObj[10]);
			pageMap.put("linkUrlName", pageObj[11]);
			pageMap.put("linkUrl", pageObj[12]);
			pagesDetails.add(pageMap);
		}

		response.put("pagesDetail", pagesDetails);
		response.put(commonMessages.TOTAL_PAGES, pagesData.getTotalPages());
		response.put(commonMessages.CURRENT_PAGE, pagesData.getNumber());
		response.put(commonMessages.TOTAL_ELEMENTS, pagesData.getTotalElements());
		response.put(commonMessages.PAGE_SIZE, pagesData.getSize());
		response.put(commonMessages.HAS_NEXT_PAGE, pagesData.hasNext());
		response.put(commonMessages.NEXT_PAGE_NO, page + 1);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Transactional
	@Override
	public ResponseEntity<Object> followpage(long userId, long pageId) {
		log.info("***** Inside - PagesServiceImpl - followpage *****");
		response = new HashMap<>();
		User user = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User with Id " + userId + " not found"));
		Pages pagedata = pageRepo.findById(pageId)
				.orElseThrow(() -> new ResourceNotFoundException("Page with the Id " + pageId + " not found"));

		if (pagedata.getMembers().contains(user)) {
			response.put(CommonMessages.STATUS, CommonMessages.FAILED);
			response.put(CommonMessages.MESSAGE, commonMessages.PAGE_USER_ALREADY_FOLLOW_PAGE);
			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
		}

		pagedata.getMembers().add(user);
		pageRepo.save(pagedata);
		response.put(CommonMessages.STATUS, CommonMessages.SUCCESS);
		response.put(CommonMessages.MESSAGE, commonMessages.PAGE_USER_FOLLOW_PAGE_SUCCESSFUL);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);

	}

	@Transactional
	@Override
	public ResponseEntity<Object> getPages(long userId, int page, int size) {
		log.info("***** Inside - PagesServiceImpl - getPages *****");
		response = new HashMap<>();
		Pageable pageable = PageRequest.of(page, size);
		Page<PagesModelV1> pagesData = pageRepo.findByMembersContaining(userId, pageable);
		if (pagesData == null || pagesData.isEmpty()) {
			response.put(CommonMessages.STATUS, CommonMessages.FAILED);
			response.put(CommonMessages.MESSAGE, commonMessages.PAGE_NO_PAGES);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
		response.put("pagesDetail", pagesData.getContent());
		response.put(commonMessages.TOTAL_PAGES, pagesData.getTotalPages());
		response.put(commonMessages.CURRENT_PAGE, pagesData.getNumber());
		response.put(commonMessages.TOTAL_ELEMENTS, pagesData.getTotalElements());
		response.put(commonMessages.PAGE_SIZE, pagesData.getSize());
		response.put(commonMessages.HAS_NEXT_PAGE, pagesData.hasNext());
		response.put(commonMessages.NEXT_PAGE_NO, page + 1);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Transactional
	@Override
	public ResponseEntity<Object> getPageDetails(long userId, String pageUUID, int page, int size) {
		log.info("***** Inside - PagesServiceImpl - getPageDetails *****");
		response = new HashMap<>();
		Pageable pageable = PageRequest.of(page, size);
		List<Map<String, Object>> grpMembersDetails = new ArrayList<>();
		Pages pgDetails = pageRepo.findByPuuid(pageUUID)
				.orElseThrow(() -> new ResourceNotFoundException("Page with the UUID " + pageUUID + " not found"));
		PagesModelV1 convertedGroupDetails = pagesMapper.convertToDto(pgDetails);

		// Check if the user follows the page
		boolean isPageFollowed = pgDetails.getMembers().stream().anyMatch(member -> member.getUserId() == userId);
		convertedGroupDetails.setPageFollowed(isPageFollowed); // Set the follow status

		for (int i = 0; i < pgDetails.getMembers().size(); i++) {
			Map<String, Object> grpMembers = new HashMap<>();
			grpMembers.put("userId", pgDetails.getMembers().get(i).getUserId());
			grpMembers.put("userFirstName", pgDetails.getMembers().get(i).getUserFirstName());
			grpMembers.put("userlastName", pgDetails.getMembers().get(i).getUserLastName());
			grpMembers.put("userProfileImagePath", pgDetails.getMembers().get(i).getUserProfileImagePath());
			grpMembersDetails.add(grpMembers);

		}
		/* get posts from groups */
		Page<PostIndexPage> indexDataPage;
		if (userId == pgDetails.getUser().getUserId()) {
			indexDataPage = pageRepo.findAllPagePosts(pageable, pgDetails.getPageId(), pgDetails.getUser().getUserId(),
					pgDetails.getUser().getUserId());
		} else {
			indexDataPage = pageRepo.findAllPagePosts(pageable, pgDetails.getPageId(), pgDetails.getUser().getUserId(),
					userId);

		}
		response.put("pageAbout", convertedGroupDetails);
		response.put("currentPageMembers", grpMembersDetails);
		response.put("postDetails", indexDataPage.getContent());
		response.put(commonMessages.TOTAL_PAGES, indexDataPage.getTotalPages());
		response.put(commonMessages.CURRENT_PAGE, indexDataPage.getNumber());
		response.put(commonMessages.TOTAL_ELEMENTS, indexDataPage.getTotalElements());
		response.put(commonMessages.PAGE_SIZE, indexDataPage.getSize());
		response.put(commonMessages.HAS_NEXT_PAGE, indexDataPage.hasNext());
		response.put(commonMessages.NEXT_PAGE_NO, page + 1);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Transactional
	@Override
	public ResponseEntity<Object> updatePage(long pageAdminUserId, long pageId, PageGroupUpdateModel page) {
		log.info("***** Inside - PagesServiceImpl - updatePage *****");
		response = new HashMap<>();
		Pages pageData = pageRepo.findByUserUserIdAndPageId(pageAdminUserId, pageId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Unable to find the specified page or admin. Please check with the Page ID or Admin ID"));

		if (page.getName() != null && !page.getName().isEmpty() && !page.getName().isBlank()) {
			System.out.println("Enter");
			pageData.setPageName(page.getName());
		}
		if (page.getDescription() != null && !page.getDescription().isEmpty() && !page.getDescription().isBlank()) {
			pageData.setPageDescription(page.getDescription());
		}
		if (page.getCoverImage() != null && !page.getCoverImage().isEmpty()) {
			// Save the image file to the server (GoDaddy VPS in your case)
			try {
				String imageUrl = commonFunctions.saveImageToServer(page.getCoverImage());
				pageData.setPageCoverProfileImagePath(imageUrl);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (page.getLinkUrlName() != null && !page.getLinkUrlName().isEmpty() && !page.getLinkUrlName().isBlank()) {
			pageData.setLinkUrlName(page.getLinkUrlName());
		}
		if (page.getLinkUrl() != null && !page.getLinkUrl().isEmpty() && !page.getLinkUrl().isBlank()) {
			pageData.setLinkUrl(page.getLinkUrl());
		}
		pageRepo.save(pageData);
		response.put(CommonMessages.STATUS, CommonMessages.SUCCESS);
		response.put(CommonMessages.MESSAGE, commonMessages.PAGE_DETAILS_UPDATE_SUCCESSFUL);
		return ResponseEntity.status(HttpStatus.OK).body(response);

	}

	@Transactional
	@Override
	public ResponseEntity<Object> exitPage(long pageAdminUserId, long pageId, long userId) {
		log.info("***** Inside - PagesServiceImpl - exitPage *****");
		response = new HashMap<>();
		User user = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User with Id " + userId + " not found"));
		// find page details by page admin Id(user Id)
		Pages pageData = pageRepo.findByUserUserIdAndPageId(pageAdminUserId, pageId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Unable to find the specified page or admin. Please check with the Page ID or Admin ID"));

		if (!pageData.getMembers().contains(user)) {
			response.put(CommonMessages.STATUS, CommonMessages.FAILED);
			response.put(CommonMessages.MESSAGE, commonMessages.PAGE_NO_PAGES);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		pageData.getMembers().remove(user);
		pageRepo.save(pageData);
		response.put(CommonMessages.STATUS, CommonMessages.SUCCESS);
		response.put(CommonMessages.MESSAGE, commonMessages.PAGE_USER_REMOVED_FROM_PAGE);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Transactional
	@Override
	public ResponseEntity<Object> deletePageById(long pageAdminUserId, long pageId) {
		log.info("***** Inside - PagesServiceImpl - deletePageById *****");
		response = new HashMap<>();

		Pages pageData = pageRepo.findByUserUserIdAndPageId(pageAdminUserId, pageId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Unable to find the specified page or admin. Please check with the Page ID or Admin ID"));

		pageData.getMembers().clear();
		pageRepo.save(pageData);
		pageRepo.delete(pageData);
		response.put(CommonMessages.STATUS, CommonMessages.SUCCESS);
		response.put(CommonMessages.MESSAGE, commonMessages.PAGE_DELETE_SUCCESSFUL);
		return ResponseEntity.status(HttpStatus.OK).body(response);

	}

	@Override
	public ResponseEntity<Object> getAllPages() {
		response = new HashMap<>();
		List<Pages> indexData = pageRepo.findAll();

		if (indexData.isEmpty()) {
			response.put(CommonMessages.STATUS, "Failed");
			response.put(CommonMessages.MESSAGE, "You're all caught up! There are no more Pages to show at the moment");
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		} else {
			
			response.put("GroupsData", indexData);
			response.put(CommonMessages.TOTAL_ELEMENTS, indexData.size());
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

}

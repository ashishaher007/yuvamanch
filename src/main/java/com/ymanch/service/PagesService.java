package com.ymanch.service;

import org.springframework.http.ResponseEntity;

import com.ymanch.entity.Pages;
import com.ymanch.entity.Posts;
import com.ymanch.model.PageGroupUpdateModel;
import com.ymanch.model.PagesModel;

import jakarta.validation.Valid;

public interface PagesService {

	ResponseEntity<Object> createPage(long adminUserId, @Valid PagesModel pages);

	ResponseEntity<Object> getPagesByUserId(long userId, int page, int size);

	ResponseEntity<Object> getAllPages(long userId, int page, int size);

	ResponseEntity<Object> followpage(long userId, long pageId);

	ResponseEntity<Object> getPages(long userId, int page, int size);

	ResponseEntity<Object> getPageDetails(long pageId, String pageUUID, int page, int size);

	ResponseEntity<Object> updatePage(long pageAdminUserId, long pageId, PageGroupUpdateModel page);

	ResponseEntity<Object> exitPage(long pageAdminUserId, long pageId, long userId);

	ResponseEntity<Object> deletePageById(long pageAdminUserId, long pageId);
	
	ResponseEntity<Object> getAllPages();

}

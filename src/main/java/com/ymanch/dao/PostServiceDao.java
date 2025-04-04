package com.ymanch.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Repository
@Slf4j
public class PostServiceDao {
	@PersistenceContext
	private EntityManager entityManager;

	public PostServiceDao(EntityManager entityManager) {
		super();
		this.entityManager = entityManager;
	}

	private StringBuilder query;

	public List<Object[]> getAdminAdvertisementDetails(String district) {
		log.info("***** Inside - PostServiceDao - getAdminAdvertisementDetails *****");
		query = new StringBuilder();
		query.append(
				"select post_name,post_description,post_image_url,post_created_at,post_type,post_video_thumbnail from posts where district=:district");
		Query createNativeQuery = entityManager.createNativeQuery(query.toString());
		createNativeQuery.setParameter("district", district);
		return createNativeQuery.getResultList();

	}
}

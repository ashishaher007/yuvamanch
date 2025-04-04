package com.ymanch.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.ymanch.model.AdminDisputeDetails;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Repository
@Slf4j
public class DisputeDao {
	@PersistenceContext
	private EntityManager entityManager;
	private StringBuilder query;

//	@SuppressWarnings("unchecked")
//	public List<AdminDisputeDetails> getDisputeDetails(long districtId, int page, int size) throws Exception {
//		log.info("***** Inside DisputeDao - getDisputeDetails *****");
//		List<AdminDisputeDetails> data = new ArrayList<>();
//		StringBuilder query = new StringBuilder();
//
//		try {
//			query.append(
//					"SELECT new com.ymanch.model.AdminDisputeDetails(d.dispute_id, post_user.user_id AS post_user_id, post_user.user_first_name AS post_user_first_name, "
//							+ "post_user.user_last_name AS post_user_last_name, post_user.user_email AS post_user_email, "
//							+ "post_user.user_cover_profile_image_path, p.post_name, p.post_image_url, "
//							+ "(SELECT COUNT(d1.dispute_id) FROM disputes d1 WHERE d1.post.post_id = d.post.post_id)) AS total_disputes_for_post "
//							+ "FROM disputes d JOIN posts p ON d.post.post_id = p.post_id "
//							+ "JOIN user post_user ON p.user.user_id = post_user.user_id "
//							+ "WHERE post_user.district_id = :districtId");
//
//			Query createNativeQuery = entityManager.createNativeQuery(query.toString());
//			createNativeQuery.setParameter("districtId", districtId);
//
//			List<Object[]> resultList = createNativeQuery.getResultList(); // Fetch results once
//
//			for (Object[] resultArray : resultList) {
//				data.add(new AdminDisputeDetails(resultArray[0] != null ? ((Number) resultArray[0]).longValue() : 0,
//						resultArray[1] != null ? ((Number) resultArray[1]).longValue() : 0,
//						resultArray[2] != null ? resultArray[2].toString() : null,
//						resultArray[3] != null ? resultArray[3].toString() : null,
//						resultArray[4] != null ? resultArray[4].toString() : null,
//						resultArray[5] != null ? resultArray[5].toString() : null,
//						resultArray[6] != null ? resultArray[6].toString() : null,
//						resultArray[7] != null ? resultArray[7].toString() : null,
//						resultArray[8] != null ? ((Number) resultArray[8]).intValue() : 0 // Or use a default value as
//																							// needed
//				));
//			}
//			return data;
//		} catch (Exception e) {
//			log.error("Error occurred while fetching admin user details: ", e);
//			throw new Exception("Failed to fetch user details", e);
//		} finally {
//			// Ensure that the EntityManager is cleared, which will release the connection
//			if (entityManager != null && entityManager.isOpen()) {
//				entityManager.clear();
//			}
//		}
//	}
}
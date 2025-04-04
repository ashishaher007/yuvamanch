package com.ymanch.dao;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ymanch.model.AdminUserDetails;
import com.ymanch.model.PostIndex;
import com.ymanch.model.admin.AdminModel.DistrictStats;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Repository
@Slf4j
public class AdminServiceDao {
	@PersistenceContext
	private EntityManager entityManager;
	private StringBuilder query;
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

	@SuppressWarnings("unchecked")
	public List<AdminUserDetails> getAdminUserDetails() throws Exception {
		log.info(" Inside AdminServiceDao - getAdminUserDetails ");
		List<AdminUserDetails> data = new ArrayList<>();
		StringBuilder query = new StringBuilder();

		try {
			query.append(
					"SELECT u.user_id, u.user_first_name, u.user_last_name, u.user_email, u.user_profile_image_path, u.user_created_at, COUNT(p.post_id) "
							+ "FROM user u "
							+ "LEFT JOIN posts p ON u.user_id = p.user_id where u.user_role='ROLE_USER' "
							+ "GROUP BY u.user_id");

			Query createNativeQuery = entityManager.createNativeQuery(query.toString());

			createNativeQuery.getResultList().stream().forEach(obj -> {
				Object[] resultArray = (Object[]) obj;

				LocalDateTime userCreatedAt = null;
				try {
					userCreatedAt = LocalDateTime.parse(resultArray[5].toString(), formatter);
				} catch (DateTimeParseException e) {
					log.error("Date parsing error for postCreatedAt: " + e.getMessage());
				}

				data.add(AdminUserDetails.builder().userId(Long.valueOf(resultArray[0].toString()))
						.userFirstName(resultArray[1].toString()).userLastName(resultArray[2].toString())
						.userCreatedAt(userCreatedAt).userEmail(resultArray[3].toString())
						.userProfileImagePath(resultArray[4].toString())
						.totalPostCount(Integer.valueOf(resultArray[6].toString())).build());
			});

		} catch (Exception e) {
			log.error("Error occurred while fetching admin user details: ", e); // Handle
// the exception based on your application requirements // e.g., throw a custom exception, return an empty list, etc. 
			throw new Exception("Failed to fetch user details", e);
		} finally { // Ensure that the
//	 * EntityManager is cleared, which will release the connection
			if (entityManager != null && entityManager.isOpen()) {
				entityManager.clear();
			}
		}
		return data;
	}

}

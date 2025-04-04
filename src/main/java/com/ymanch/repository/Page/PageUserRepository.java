package com.ymanch.repository.Page;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.ymanch.entity.User;

@Repository
public interface PageUserRepository extends PagingAndSortingRepository<User, Long> {
//	@Query(value = "SELECT u.user_id, u.user_first_name, u.user_last_name, u.user_email, u.user_profile_image_path, u.user_created_at, COUNT(p.post_id) as total_post_count ,u.user_date_of_birth,u.user_address "
//			+ "FROM user u LEFT JOIN posts p ON u.user_id = p.user_id WHERE (:firstName IS NULL OR :firstName='' OR u.user_first_name LIKE CONCAT('%', :firstName, '%')) AND (:districtId=37  OR u.district_id=:districtId) AND u.user_role = 'ROLE_USER' "
//			+ "GROUP BY u.user_id", countQuery = "SELECT COUNT(*) FROM user u WHERE (u.user_first_name LIKE CONCAT('%', :firstName, '%')) AND (:districtId = 37 OR u.district_id = :districtId) AND u.user_role = 'ROLE_USER'", nativeQuery = true)
//	Page<Object[]> findAllUsersWithPagination(String firstName, long districtId, Pageable pageable);
	
	@Query(value = "SELECT u.user_id, u.user_first_name, u.user_last_name, u.user_email, "
	        + "u.user_profile_image_path, u.user_created_at, COUNT(p.post_id) as total_post_count, "
	        + "u.user_date_of_birth, u.user_address "
	        + "FROM user u LEFT JOIN posts p ON u.user_id = p.user_id "
	        + "WHERE (:firstName IS NULL OR :firstName='' OR u.user_first_name LIKE CONCAT('%', :firstName, '%')) "
	        + "AND (:districtId IS NULL OR u.district_id = :districtId) AND u.user_role = 'ROLE_USER' "
	        + "GROUP BY u.user_id, u.user_first_name, u.user_last_name, u.user_email, "
	        + "u.user_profile_image_path, u.user_created_at, u.user_date_of_birth, u.user_address",  
	        countQuery = "SELECT COUNT(*) FROM user u WHERE (:firstName IS NULL OR u.user_first_name LIKE CONCAT('%', :firstName, '%')) "
	        + "AND (:districtId IS NULL OR u.district_id = :districtId) AND u.user_role = 'ROLE_USER'",  
	        nativeQuery = true)  
	Page<Object[]> findAllUsersWithPagination(String firstName, Long districtId, Pageable pageable);


}

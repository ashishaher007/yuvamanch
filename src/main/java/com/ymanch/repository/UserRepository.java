package com.ymanch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ymanch.entity.User;
import com.ymanch.model.UserDetails;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	User findByUserEmail(String userEmail);

	@Query(value = "SELECT * FROM user WHERE full_name LIKE CONCAT(:userName, '%')", nativeQuery = true)
	List<User> findByUserFirstNameOrUserLastName(String userName);

	@Query("SELECT COUNT(u) FROM User u WHERE u.userStatus = 'ACTIVE' AND u.userRole = 'ROLE_USER'")
	long countActiveUsers();

	@Query("SELECT COUNT(u) FROM User u WHERE u.userCreatedAt >= CURRENT_DATE AND u.userRole = 'ROLE_USER'")
	long countUsersRegisteredSince();

	@Query(value = "SELECT DATE(u.user_created_at) AS date, d.district_name, COUNT(*) AS userCount " + "FROM user u "
			+ "JOIN district d ON u.district_id = d.district_id "
			+ "GROUP BY DATE(u.user_created_at), d.district_name", nativeQuery = true)
	List<Object[]> findUserRegistrationCountsByDateAndDistrictNative();

	User findByUserMobileNumber(String mobile);

	User findByUserEmailOrUserMobileNumber(String email, String mobile);

	@Query(value = "SELECT user_role from user where user_id=:userId", nativeQuery = true)
	String getUserByRole(Long userId);

	User findFirstByUserEmailOrUserMobileNumber(String userEmailOrMobileNumber, String userEmailOrMobileNumber2);

	User findFirstByUserEmail(String identifier);

	User findByUserMobileNumberOrUserpassword(String identifier, String identifier2);

	User findByUserEmailAndUserId(String currentUsername, Long requestedUserId);

	@Query("SELECT u FROM User u WHERE u.uuid IS NULL OR u.uuid = ''")
	List<User> findByUuidIsNullOrEmpty();

	Optional<User> findByUuid(String userId);

	@Query("SELECT u FROM User u WHERE u.fullName IS NULL OR u.fullName = ''")
	List<User> findByFullNameNullOrEmpty();

	@Modifying
	@Query("UPDATE User u SET u.fullName = CONCAT(u.userFirstName, ' ', u.userLastName) WHERE u.fullName IS NULL OR u.fullName = ''")
	void updateFullNames();

	@Query(value = "SELECT * FROM user u WHERE MONTH(u.user_date_of_birth) = MONTH(CURRENT_DATE) "
			+ "AND DAY(u.user_date_of_birth) = DAY(CURRENT_DATE)", nativeQuery = true)
	List<User> findByuserDateOfBirth();

//	@Query(value = "SELECT * FROM user u LEFT JOIN friend_request fr ON u.user_id = CASE WHEN fr.sender_id=:userId THEN fr.receiver_id WHEN fr.receiver_id=:userId THEN fr.sender_id END WHERE (fr.sender_id=:userId OR fr.receiver_id=:userId) AND fr.status='APPROVED' AND u.full_name LIKE CONCAT(:userName, '%')", nativeQuery = true)
//	List<User> findByUserFirstNameOrUserLastName(Long userId, String userName);
	
	//query change to find the user based on first name or last name 
	@Query(value = "SELECT * FROM user u LEFT JOIN friend_request fr ON u.user_id = CASE WHEN fr.sender_id = :userId THEN fr.receiver_id WHEN fr.receiver_id = :userId THEN fr.sender_id END WHERE (fr.sender_id = :userId OR fr.receiver_id = :userId) AND fr.status = 'APPROVED' AND (u.user_first_name LIKE CONCAT(:userName, '%') OR u.user_last_name LIKE CONCAT(:userName, '%'))", nativeQuery = true)
	List<User> findByUserFirstNameOrUserLastName(Long userId, String userName);


	@Query("SELECT new com.ymanch.model.UserDetails(u.userId, u.userFirstName, u.userLastName,u.userProfileImagePath,u.userEmail,u.userCreatedAt,u.uuid) FROM User u WHERE u.district.districtId = :districtId and u.userRole='ROLE_USER'")
	List<UserDetails> findUsersByDistrictId(@Param("districtId") long districtId);

	@Query(value = "SELECT * FROM user u WHERE u.user_status = 'ACTIVE'and u.user_role='ROLE_USER'", nativeQuery = true)
	List<User> getAllActiveUsers();

	@Query("SELECT u.district.districtName, COUNT(u) FROM User u GROUP BY u.district.districtName")
	List<Object[]> getDistrictWiseUserCount();

}

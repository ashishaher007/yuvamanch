package com.ymanch.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ymanch.entity.Posts;
import com.ymanch.helper.Enums.PostOwnerType;
import com.ymanch.model.PostIndexPage;
import com.ymanch.model.PostModel;

@Repository
public interface PostRepository extends JpaRepository<Posts, Long> {

	List<Posts> findByUserUserId(long userId);

	@Query(value = "Select count(p.post_id) from posts p where p.user_id=:userId AND p.post_owner_type = 'PUBLIC' ", nativeQuery = true)
	long countPostByUserId(long userId);

	@Query("SELECT COUNT(p) FROM Posts p")
	long countPostAddedSince();

	@Query(value = "SELECT new com.ymanch.model.PostIndexPage("
			+ "p.postId, u.userId, u.userProfileImagePath, CONCAT(u.userFirstName, ' ', u.userLastName), "
			+ "p.postImageUrl, p.postType, p.videoThumbnailUrl, p.postCreatedAt, p.postName, "
			+ "COUNT(DISTINCT pr.postReactId), "
			+ "CASE WHEN EXISTS (SELECT 1 FROM PostReact pr2 WHERE pr2.posts.postId = p.postId AND pr2.user.userId = :userId AND pr2.isPostReactDeleted = false) "
			+ "THEN true ELSE false END AS userReactStatus, "
			+ "CASE WHEN EXISTS (SELECT 1 FROM UserSavedPost usp WHERE usp.post.postId = p.postId AND usp.user.userId = :userId) "
			+ "THEN true ELSE false END AS isPostSaved, u.uuid,p.advertisementDescription) " + "FROM Posts p "
			+ "JOIN User u ON p.user.userId = u.userId "
			+ "LEFT JOIN PostReact pr ON p.postId = pr.posts.postId AND pr.isPostReactDeleted = false "
			+ "WHERE p.isPostDeleted = false and u.userRole='ROLE_USER' and p.postOwnerType='PUBLIC' "
			+ "GROUP BY p.postId, u.userId, u.userProfileImagePath, u.userFirstName, u.userLastName, "
			+ "p.postImageUrl, p.postType, p.videoThumbnailUrl, p.postCreatedAt, p.postName "
			+ "ORDER BY p.postCreatedAt DESC")
	Page<PostIndexPage> findAllPostIndexData(Pageable pageable);

//	@Query(value = "SELECT new com.ymanch.model.PostIndexPage("
//			+ "p.postId, u.userId, u.userProfileImagePath, CONCAT(u.userFirstName, ' ', u.userLastName), "
//			+ "p.postImageUrl, p.postType, p.videoThumbnailUrl, p.postCreatedAt, p.postName, "
//			+ "COUNT(DISTINCT pr.postReactId), "
//			+ "EXISTS (SELECT 1 FROM PostReact pr2 WHERE pr2.posts.postId = p.postId AND pr2.user.userId = :userId AND pr2.isPostReactDeleted = false), "
//			+ "EXISTS (SELECT 1 FROM UserSavedPost usp WHERE usp.post.postId = p.postId AND usp.user.userId = :userId), "
//			+ "u.uuid,p.advertisementDescription) " + "FROM Posts p " + "JOIN User u ON p.user.userId = u.userId "
//			+ "LEFT JOIN PostReact pr ON p.postId = pr.posts.postId AND pr.isPostReactDeleted = false WHERE p.user.userId=:userId "
//			+ "GROUP BY p.postId " + "ORDER BY p.postCreatedAt DESC ")
//	Page<PostIndexPage> findAllPostDataById(long userId, Pageable pageable);
	
	@Query(value = "SELECT new com.ymanch.model.PostIndexPage("
			+ "p.postId, u.userId, u.userProfileImagePath, CONCAT(u.userFirstName, ' ', u.userLastName), "
			+ "p.postImageUrl, p.postType, p.videoThumbnailUrl, p.postCreatedAt, p.postName, "
			+ "COUNT(DISTINCT pr.postReactId), "
			+ "CASE WHEN EXISTS (SELECT 1 FROM PostReact pr2 WHERE pr2.posts.postId = p.postId AND pr2.user.userId = :userId AND pr2.isPostReactDeleted = false) "
			+ "THEN true ELSE false END AS userReactStatus, "
			+ "CASE WHEN EXISTS (SELECT 1 FROM UserSavedPost usp WHERE usp.post.postId = p.postId AND usp.user.userId = :userId) "
			+ "THEN true ELSE false END as isPostSaved, u.uuid) " + "FROM Posts p "
			+ "JOIN User u ON p.user.userId = u.userId "
			+ "LEFT JOIN PostReact pr ON p.postId = pr.posts.postId AND pr.isPostReactDeleted = false "
			+ "WHERE p.postOwnerType = com.ymanch.helper.Enums.PostOwnerType.REEL "
			+ "GROUP BY p.postId, u.userId, u.userProfileImagePath, u.userFirstName, u.userLastName, "
			+ "p.postImageUrl, p.postType, p.videoThumbnailUrl, p.postCreatedAt, p.postName "
			+ "ORDER BY p.postCreatedAt DESC")
	Page<PostIndexPage> findAllPostDataById(long userId, Pageable pageable);
	

//	Posts findByDistrict(String district);

//	@Query("SELECT p FROM Posts p WHERE p.user.userRole = :role")
//	List<Posts> findPostsByUserRole(String role);

	@Query(value = "SELECT new com.ymanch.model.PostIndexPage("
			+ "p.postId, u.userId, u.userProfileImagePath, u.userFirstName, "
			+ "p.postImageUrl, p.postType, p.videoThumbnailUrl, p.postCreatedAt, p.postName, "
			+ "COUNT(DISTINCT pr.postReactId), "
			+ "CASE WHEN EXISTS (SELECT 1 FROM PostReact pr2 WHERE pr2.posts.postId = p.postId AND pr2.user.userId = :userId AND pr2.isPostReactDeleted = false) "
			+ "THEN true ELSE false END AS userReactStatus, "
			+ "CASE WHEN EXISTS (SELECT 1 FROM UserSavedPost usp WHERE usp.post.postId = p.postId AND usp.user.userId = :userId) "
			+ "THEN true ELSE false END AS isPostSaved, u.uuid,p.advertisementDescription) " + "FROM Posts p "
			+ "JOIN User u ON p.user.userId = u.userId "
			+ "LEFT JOIN PostReact pr ON p.postId = pr.posts.postId AND pr.isPostReactDeleted = false "
			+ "JOIN Group g ON p.group.groupId = g.groupId " + "WHERE p.isPostDeleted = false "
			+ "AND g.user.userId = :adminUserId AND p.group.groupId = :groupId "
			+ "GROUP BY p.postId, u.userId, u.userProfileImagePath, u.userFirstName, "
			+ "p.postImageUrl, p.postType, p.videoThumbnailUrl, p.postCreatedAt, p.postName "
			+ "ORDER BY p.postCreatedAt DESC")
	Page<PostIndexPage> findAllGroupPosts(Pageable pageable, long groupId, long adminUserId, long userId);

	@Query("SELECT p FROM Posts p WHERE (:districtId=37 OR p.district.districtId = :districtId) AND p.postOwnerType = :postCategory")
	List<Posts> findByDistrictDistrictIdAndPostOwnerType(long districtId, String postCategory);

//	Optional<Posts> findByPostIdAndPostOwnerType(long postId, PostOwnerType public1);

	Optional<Posts> findByUserUserIdAndPostId(long userId, long postId);

	@Query(value = "SELECT new com.ymanch.model.PostIndexPage("
			+ "p.postId, u.userId, u.userProfileImagePath, CONCAT(u.userFirstName, ' ', u.userLastName), "
			+ "p.postImageUrl, p.postType, p.videoThumbnailUrl, p.postCreatedAt, p.postName, "
			+ "COUNT(DISTINCT pr.postReactId), "
			+ "CASE WHEN EXISTS (SELECT 1 FROM PostReact pr2 WHERE pr2.posts.postId = p.postId AND pr2.user.userId = :userId AND pr2.isPostReactDeleted = false) "
			+ "THEN true ELSE false END AS userReactStatus, "
			+ "CASE WHEN EXISTS (SELECT 1 FROM UserSavedPost usp WHERE usp.post.postId = p.postId AND usp.user.userId = :userId) "
			+ "THEN true ELSE false END AS isPostSaved, u.uuid,p.advertisementDescription) " + "FROM Posts p "
			+ "JOIN User u ON p.user.userId = u.userId "
			+ "LEFT JOIN PostReact pr ON p.postId = pr.posts.postId AND pr.isPostReactDeleted = false "
			+ "LEFT JOIN Disputes d ON d.post.postId = p.postId AND d.user.userId = :userId "
			+ "JOIN UserSavedPost usp ON usp.post.postId = p.postId AND usp.user.userId = :userId "
			+ "WHERE p.isPostDeleted = false AND u.userRole = 'ROLE_USER' AND p.postOwnerType = 'PUBLIC' "
			+ "AND d.disputeId IS NULL "
			+ "GROUP BY p.postId, u.userId, u.userProfileImagePath, u.userFirstName, u.userLastName, "
			+ "p.postImageUrl, p.postType, p.videoThumbnailUrl, p.postCreatedAt, p.postName "
			+ "ORDER BY p.postCreatedAt DESC")
	Page<PostIndexPage> findAllSavedPost(Pageable pageable, long userId);

	Optional<Posts> findByPostIdAndPostOwnerTypeIn(long postId, List<PostOwnerType> allowedTypes);

	@Query(value = "SELECT new com.ymanch.model.PostIndexPage("
			+ "p.postId, u.userId, u.userProfileImagePath, u.userFirstName, "
			+ "p.postImageUrl, p.postType, p.videoThumbnailUrl, p.postCreatedAt, p.postName, "
			+ "COUNT(DISTINCT pr.postReactId), "
			+ "EXISTS (SELECT 1 FROM PostReact pr2 WHERE pr2.posts.postId = p.postId AND pr2.user.userId = :userId AND pr2.isPostReactDeleted = false), "
			+ "EXISTS (SELECT 1 FROM UserSavedPost usp WHERE usp.post.postId = p.postId AND usp.user.userId = :userId), "
			+ "u.uuid,p.advertisementDescription) " + "FROM Posts p " + "JOIN User u ON p.user.userId = u.userId "
			+ "LEFT JOIN PostReact pr ON p.postId = pr.posts.postId AND pr.isPostReactDeleted = false "
			+ "WHERE p.isPostDeleted = false " + "AND u.userId = :targetUserId " + "AND p.postOwnerType = 'PUBLIC' "
			+ "AND (:cursor = 0 OR p.postId < :cursor) " + "GROUP BY p.postId " + "ORDER BY p.postCreatedAt DESC")
	Page<PostIndexPage> findAllPostDataById(long targetUserId, long cursor, Pageable pageable, long userId);

	@Query("SELECT new com.ymanch.model.PostModel(p.postId, p.postName, p.postImageUrl, u.userProfileImagePath, u.userFirstName, u.userLastName, u.userEmail,p.postType) "
			+ "FROM Posts p JOIN p.user u WHERE u.userId = :userId")
	Page<PostModel> findPostsByUserUserId(long userId, Pageable pageable);

	@Query("SELECT new com.ymanch.model.PostModel(p.postId, p.postName, p.postImageUrl, u.userProfileImagePat"
			+ "h, u.userFirstName, u.userLastName, u.userEmail,p.postType) "
			+ "FROM Posts p JOIN p.user u WHERE u.userRole = 'ROLE_USER'")
	Page<PostModel> findPosts(Pageable pageable);

	@Query(value = "SELECT new com.ymanch.model.PostIndexPage("
			+ "p.postId, u.userId, u.userProfileImagePath, u.userFirstName, "
			+ "p.postImageUrl, p.postType, p.videoThumbnailUrl, p.postCreatedAt, p.postName, "
			+ "COUNT(DISTINCT pr.postReactId), "
			+ "EXISTS (SELECT 1 FROM PostReact pr2 WHERE pr2.posts.postId = p.postId AND pr2.user.userId = :userId AND pr2.isPostReactDeleted = false), "
			+ "EXISTS (SELECT 1 FROM UserSavedPost usp WHERE usp.post.postId = p.postId AND usp.user.userId = :userId), "
			+ "u.uuid,p.advertisementDescription) " + "FROM Posts p " + "JOIN User u ON p.user.userId = u.userId "
			+ "LEFT JOIN PostReact pr ON p.postId = pr.posts.postId AND pr.isPostReactDeleted = false "
			+ "JOIN Boost b ON p.postId = b.post.postId AND b.boostStatus = 'ACTIVE' " + "JOIN b.districts d "
			+ "WHERE p.isPostDeleted = false AND u.userRole = 'ROLE_USER' AND p.postOwnerType = 'PUBLIC' "
			+ "AND EXISTS (SELECT 1 FROM Boost b2 WHERE b2.post.postId = p.postId AND b2.boostStatus = 'ACTIVE') "
			+ "AND d.districtId = (SELECT u2.district.id FROM User u2 WHERE u2.userId = :userId) "
			+ "GROUP BY b.post.postId,b.boostCreatedAt " + "ORDER BY b.boostCreatedAt DESC")
	Page<PostIndexPage> findBoostPosts(Pageable pageable, long userId);

	@Query(value = "SELECT new com.ymanch.model.PostIndexPage("
			+ "p.postId, u.userId, u.userProfileImagePath, u.userFirstName, "
			+ "p.postImageUrl, p.postType, p.videoThumbnailUrl, p.postCreatedAt, p.postName, "
			+ "COUNT(DISTINCT pr.postReactId), "
			+ "EXISTS (SELECT 1 FROM PostReact pr2 WHERE pr2.posts.postId = p.postId AND pr2.user.userId = :userId AND pr2.isPostReactDeleted = false), "
			+ "EXISTS (SELECT 1 FROM UserSavedPost usp WHERE usp.post.postId = p.postId AND usp.user.userId = :userId), "
			+ "u.uuid,p.advertisementDescription) " + "FROM Posts p " + "JOIN User u ON p.user.userId = u.userId "
			+ "LEFT JOIN PostReact pr ON p.postId = pr.posts.postId AND pr.isPostReactDeleted = false "
			+ "WHERE p.isPostDeleted = false AND u.userRole = 'ROLE_USER' AND p.postOwnerType='PUBLIC' "
			+ "AND p.postId < :cursor " + "GROUP BY p.postId " + "ORDER BY p.postCreatedAt DESC")
	List<PostIndexPage> findByIdLessThan(long cursor, Pageable pageable, long userId);

	@Query(value = "SELECT new com.ymanch.model.PostIndexPage("
			+ "p.postId, u.userId, u.userProfileImagePath, CONCAT(u.userFirstName, ' ', u.userLastName), "
			+ "p.postImageUrl, p.postType, p.videoThumbnailUrl, p.postCreatedAt, p.postName, "
			+ "COUNT(DISTINCT pr.postReactId), "
			+ "CASE WHEN EXISTS (SELECT 1 FROM PostReact pr2 WHERE pr2.posts.postId = p.postId AND pr2.user.userId = :userId AND pr2.isPostReactDeleted = false) "
			+ "THEN true ELSE false END AS userReactStatus, "
			+ "CASE WHEN EXISTS (SELECT 1 FROM UserSavedPost usp WHERE usp.post.postId = p.postId AND usp.user.userId = :userId) "
			+ "THEN true ELSE false END as isPostSaved, u.uuid,p.advertisementDescription) " + "FROM Posts p "
			+ "JOIN User u ON p.user.userId = u.userId "
			+ "LEFT JOIN PostReact pr ON p.postId = pr.posts.postId AND pr.isPostReactDeleted = false "
			+ "WHERE p.postOwnerType = 'REEL' "
			+ "AND u.district.id = (SELECT u2.district.id FROM User u2 WHERE u2.userId = :userId) "
			+ "GROUP BY p.postId, u.userId, u.userProfileImagePath, u.userFirstName, u.userLastName, "
			+ "p.postImageUrl, p.postType, p.videoThumbnailUrl, p.postCreatedAt, p.postName "
			+ "ORDER BY p.postCreatedAt DESC")
	Page<PostIndexPage> findAllReelsDataById(long userId, Pageable pageable);

	Posts findByPostId(long postId);

//	@Query(value = "SELECT new com.ymanch.model.PostIndexPage("
//			 + "p.postId, u.userId, u.userProfileImagePath, u.userFirstName, "
//				+ "p.postImageUrl, p.postType, p.videoThumbnailUrl, p.postCreatedAt, p.postName, "
//				+ "COUNT(DISTINCT pr.postReactId), "
//				+ "EXISTS (SELECT 1 FROM PostReact pr2 WHERE pr2.posts.postId = p.postId AND pr2.user.userId = :userId AND pr2.isPostReactDeleted = false), "
//				+ "EXISTS (SELECT 1 FROM UserSavedPost usp WHERE usp.post.postId = p.postId AND usp.user.userId = :userId), "
//				+ "u.uuid,p.advertisementDescription) " + "FROM Posts p " + "JOIN User u ON p.user.userId = u.userId "
//				+ "LEFT JOIN PostReact pr ON p.postId = pr.posts.postId AND pr.isPostReactDeleted = false "
//				+ "WHERE p.isPostDeleted = false AND u.userRole = 'ROLE_USER' AND p.postOwnerType = 'PUBLIC' "
//				+ "AND p.postId < :cursor "
//				+ "AND (u.district.id = (SELECT u2.district.id FROM User u2 WHERE u2.userId = :userId) "
//				+ "      OR u.district.id != (SELECT u2.district.id FROM User u2 WHERE u2.userId = :userId)) "
//				+ "GROUP BY p.postId " + "ORDER BY "
//				+ "  CASE WHEN u.district.id = (SELECT u2.district.id FROM User u2 WHERE u2.userId = :userId) THEN 1 ELSE 2 END, "
//				+ "  p.postUpdateAt DESC")
//	List<PostIndexPage> findByDistrictFirstThenRegular(long cursor, Pageable pageable, long userId);
	
	
	@Query(value = "SELECT new com.ymanch.m"
			+ ""
			+ "odel.PostIndexPage(" +
	        "p.postId, u.userId, u.userProfileImagePath, u.userFirstName, " +
	        "p.postImageUrl, p.postType, p.videoThumbnailUrl, p.postCreatedAt, p.postName, " +
	        "COUNT(DISTINCT pr.postReactId), " +
	        "CASE WHEN COUNT(pr2.postReactId) > 0 THEN true ELSE false END, " +
	        "CASE WHEN COUNT(usp.post) > 0 THEN true ELSE false END, " + // FIXED
	        "u.uuid, p.advertisementDescription) " +
	        "FROM Posts p " +
	        "JOIN User u ON p.user.userId = u.userId " +
	        "LEFT JOIN PostReact pr ON p.postId = pr.posts.postId AND pr.isPostReactDeleted = false " +
	        "LEFT JOIN PostReact pr2 ON pr2.posts.postId = p.postId AND pr2.user.userId = :userId AND pr2.isPostReactDeleted = false " +
	        "LEFT JOIN UserSavedPost usp ON usp.post.postId = p.postId AND usp.user.userId = :userId " + // FIXED
	        "WHERE p.isPostDeleted = false " +
	        "AND u.userRole = 'ROLE_USER' " +
	        "AND p.postOwnerType = 'PUBLIC' " +
	        "AND p.postId < :cursor " +
	        "GROUP BY p.postId, u.userId, u.userProfileImagePath, u.userFirstName, " +
	        "p.postImageUrl, p.postType, p.videoThumbnailUrl, p.postCreatedAt, p.postName, " +
	        "u.uuid, p.advertisementDescription, u.district.id " +
	        "ORDER BY " +
	        "CASE WHEN u.district.id = (SELECT u2.district.id FROM User u2 WHERE u2.userId = :userId) THEN 1 ELSE 2 END, " +
	        "p.postId DESC, " +  
	        "p.postUpdateAt DESC")
	List<PostIndexPage> findByDistrictFirstThenRegular(long cursor, Pageable pageable, long userId);

	@Query("SELECT p FROM Posts p WHERE p.postId = :postId")
	Posts findPostById(@Param("postId") Long postId);

	@Query("SELECT p FROM Posts p WHERE p.district.districtId = :districtId AND p.postOwnerType = :supAdmin")
	List<Posts> findByDistrictDistrictIdAndPostOwnerType(Long districtId, PostOwnerType supAdmin);
	
	// index working
	@Query(value = "select d.district_name,count(p.post_id),d.district_id FROM posts p INNER JOIN district d ON d.district_id=p.district_id WHERE p.post_created_at >= CURDATE() AND p.post_created_at < CURDATE() + INTERVAL 1 DAY GROUP by p.district_id", nativeQuery = true)
	List<Object[]> fetchAllTodyaPost();

	@Query("SELECT new com.ymanch.model.PostModel(p.postId, p.postName, p.postImageUrl, u.userProfileImagePath, u.userFirstName, u.userLastName, u.userEmail,p.postType) "
			+ "FROM Posts p JOIN p.user u " + "WHERE p.postCreatedAt >= :startOfDay AND p.postCreatedAt < :endOfDay "
			+ "AND p.district.districtId = :districtId ORDER BY p.postId DESC")
	Page<PostModel> findPostsByDistrictId(LocalDateTime startOfDay, LocalDateTime endOfDay, long districtId,
			Pageable pageable);

	@Query(value = "Select count(p.post_id) from posts p where DATE(p.post_created_at) = CURDATE()", nativeQuery = true)
	long countTodaysPost();

	@Query(value = "SELECT new com.ymanch.model.PostIndexPage("
			+ "p.postId, u.userId, u.userProfileImagePath, u.userFirstName, "
			+ "p.postImageUrl, p.postType, p.videoThumbnailUrl, p.postCreatedAt, p.postName, "
			+ "COUNT(DISTINCT pr.postReactId), "
			+ "EXISTS (SELECT 1 FROM PostReact pr2 WHERE pr2.posts.postId = p.postId AND pr2.user.userId = :userId AND pr2.isPostReactDeleted = false), "
			+ "EXISTS (SELECT 1 FROM UserSavedPost usp WHERE usp.post.postId = p.postId AND usp.user.userId = :userId), "
			+ "u.uuid, p.advertisementDescription) " + "FROM Posts p " + "JOIN p.user u "
			+ "LEFT JOIN PostReact pr ON p.postId = pr.posts.postId AND pr.isPostReactDeleted = false "
			+ "JOIN p.hashtags h " + "WHERE p.isPostDeleted = false " + "AND p.postOwnerType = 'PUBLIC' "
			+ "AND h.name = :hashtagName " + // <-- Changed from h.id to h.name
			"GROUP BY p.postId " + "ORDER BY p.postCreatedAt DESC")
	Page<PostIndexPage> findPostByHashName(Pageable pageable, @Param("hashtagName") String hashtagName,
			@Param("userId") Long userId);
	
	@Query(value = "SELECT * FROM posts p WHERE p.district_id = :districtId AND p.post_owner_type = :supAdmin ORDER BY p.post_created_at DESC LIMIT 1", nativeQuery = true)
	Posts findLatestPostByDistrictAndOwnerType(long districtId, String supAdmin);

	Page<Posts> findByPostOwnerType(PostOwnerType reel, Pageable pageable);

	Page<Posts> findByPostOwnerTypeAndPostIdNot(PostOwnerType reel, long reelId, PageRequest of);

}

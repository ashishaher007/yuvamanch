package com.ymanch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ymanch.entity.District;
import com.ymanch.entity.Pages;
import com.ymanch.entity.User;
import com.ymanch.model.PagesModelV1;
import com.ymanch.model.PostIndexPage;

@Repository
public interface PagesRepository extends JpaRepository<Pages, Long> {

//	List<Pages> findByMembersContaining(User user);

	List<Pages> findByUser(User user);

	Pages findByPageName(String pageName);

	@Query("SELECT p.pageId, p.pageName, p.pageDescription, p.pageCoverProfileImagePath, "
			+ "u.userId, u.userFirstName, u.userLastName, u.userProfileImagePath, p.pageCreatedAt, "
			+ "CASE WHEN m.userId IS NOT NULL THEN true ELSE false END, p.puuid,p.linkUrlName,p.linkUrl "
			+ "FROM Pages p JOIN User u ON p.user.userId = u.userId " + "LEFT JOIN p.members m ON m.userId = :userId")
	Page<Object[]> findAllPageDetails(long userId, Pageable page);

//	@Query("SELECT p FROM Pages p JOIN p.members m WHERE m.userId = :userId")
//	Page<Pages> findByMembersContaining(@Param("userId") long userId, Pageable pageable);

	@Query("SELECT new com.ymanch.model.PagesModelV1(p.pageId, p.pageName, p.pageDescription, p.pageCoverProfileImagePath, "
			+ "p.user.userId, p.user.userFirstName, p.user.userLastName, p.user.userProfileImagePath, p.pageCreatedAt,p.puuid,p.linkUrlName,p.linkUrl) "
			+ "FROM Pages p WHERE p.user.userId = :userId")
	Page<PagesModelV1> findOwnPageDetails(long userId, Pageable pageable);

	@Query("SELECT new com.ymanch.model.PagesModelV1(p.pageId, p.pageName, p.pageDescription, p.pageCoverProfileImagePath, "
			+ "p.user.userId, p.user.userFirstName, p.user.userLastName, p.user.userProfileImagePath, p.pageCreatedAt,p.puuid,p.linkUrlName,p.linkUrl) "
			+ "FROM Pages p JOIN p.members m WHERE m.userId = :userId")
	Page<PagesModelV1> findByMembersContaining(long userId, Pageable pageable);

	Optional<Pages> findByUserUserIdAndPageId(long pageAdminUserId, long pageId);

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
			+ "JOIN Pages pg ON p.pages.pageId = pg.pageId " + "WHERE p.isPostDeleted = false "
			+ "AND pg.pageId = :pageId AND pg.user.userId = :pageAdminUserId "
			+ "GROUP BY p.postId, u.userId, u.userProfileImagePath, u.userFirstName, u.userLastName, "
			+ "p.postImageUrl, p.postType, p.videoThumbnailUrl, p.postCreatedAt, p.postName "
			+ "ORDER BY p.postCreatedAt DESC")
	Page<PostIndexPage> findAllPagePosts(Pageable pageable, long pageId, long pageAdminUserId, long userId);

	Optional<Pages> findByUserUserId(long userId);

	@Query("SELECT p FROM Pages p WHERE p.puuid IS NULL OR p.puuid = ''")
	List<Pages> findByPuuidIsNullOrEmpty();

	Optional<Pages> findByPuuid(String pageUUID);

}

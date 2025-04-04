package com.ymanch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ymanch.entity.Events;
import com.ymanch.helper.Enums.Status;
import com.ymanch.model.PostIndexPage;

@Repository
public interface EventRepository extends JpaRepository<Events, Long> {

//	Optional<Events> findByEventName(String eventName);

	Optional<Events> findByUserUserIdAndEventId(long hostUserId, long eventId);

	@Query(value = "SELECT e.event_name, e.event_description, e.start_date, e.start_time, e.end_date, e.end_time, "
			+ "e.event_address, e.event_id, e.total_interest, d.district_name, ev.cat_name, p.post_name, "
			+ "p.post_image_url, e.user_id, e.uuid, e.event_mode, e.event_notify, e.virtual_event_link "
			+ "FROM events e "
			+ "LEFT JOIN posts p ON p.event_id = e.event_id "
			+ "LEFT JOIN district d ON e.district_id = d.district_id "
			+ "LEFT JOIN event_category ev ON e.cat_id = ev.cat_id "
			+ "WHERE (e.district_id = :districtId OR e.district_id = 37) AND e.event_status = 'ACTIVE'", 
			
	countQuery = "SELECT COUNT(e.event_id) FROM events e "
			+ "WHERE (e.district_id = :districtId OR e.district_id = 37) AND e.event_status = 'ACTIVE'", 
	
	nativeQuery = true)
	Page<Object[]> findAllEvents(Pageable pageable, long districtId);


//	Optional<Events> findByUserUserIdAndEventName(long hostUserId, String eventName);

	@Query(value = "SELECT new com.ymanch.model.PostIndexPage("
			+ "p.postId, u.userId, u.userProfileImagePath, CONCAT(u.userFirstName, ' ', u.userLastName), "
			+ "p.postImageUrl, p.postType, p.videoThumbnailUrl, p.postCreatedAt, p.postName, "
			+ "COUNT(DISTINCT pr.postReactId), "
			+ "CASE WHEN COUNT(DISTINCT pr.postReactId) > 0 THEN true ELSE false END AS userReactStatus, "
			+ "CASE WHEN COUNT(DISTINCT usp.user.userId) > 0 THEN true ELSE false END as isPostSaved, u.uuid,p.advertisementDescription) "
			+ "FROM Posts p " + "LEFT JOIN Events e ON p.events.eventId = e.eventId "
			+ "LEFT JOIN User u ON e.user.userId = u.userId "
			+ "LEFT JOIN PostReact pr ON p.postId = pr.posts.postId AND pr.isPostReactDeleted = false "
			+ "LEFT JOIN UserSavedPost usp ON p.postId = usp.post.postId "
			+ "WHERE p.isPostDeleted = false AND p.postId = :postId "
			+ "AND p.postOwnerType IN ('PUBLIC_EVENT', 'PRIVATE_EVENT') "
			+ "GROUP BY p.postId, u.userId, u.userProfileImagePath, u.userFirstName, u.userLastName, "
			+ "p.postImageUrl, p.postType, p.videoThumbnailUrl, p.postCreatedAt, p.postName "
			+ "ORDER BY p.postCreatedAt DESC")
	List<PostIndexPage> findAllPost(long postId);

	@Query(value = "SELECT e.event_name, e.event_description, e.start_date, e.start_time, e.end_date, e.end_time, e.event_address, e.event_id, e.total_interest, d.district_name, ev.cat_name, p.post_name, p.post_image_url, e.user_id,e.uuid,e.event_mode,e.event_notify,e.virtual_event_link "
			+ "FROM events e " + "LEFT JOIN posts p ON p.event_id = e.event_id "
			+ "LEFT JOIN district d ON e.district_id = d.district_id "
			+ "LEFT JOIN event_category ev ON e.cat_id = ev.cat_id "
			+ "WHERE e.user_id = :hostUserId AND e.event_status = 'ACTIVE'", countQuery = "SELECT COUNT(e.event_id) FROM events e WHERE e.user_id = :hostUserId AND e.event_status = 'ACTIVE'", nativeQuery = true)
	Page<Object[]> findHostEvents(Pageable pageable, long hostUserId);

	@Modifying
	@Query(value = "UPDATE events SET event_status = 'ENDED' WHERE CONCAT(end_date, ' ', end_time) <= NOW()", nativeQuery = true)
	void updateEndedEventsBatch();

	@Modifying
	@Query(value = "UPDATE events SET event_status = 'ACTIVE' WHERE CONCAT(end_date, ' ', end_time) > NOW()", nativeQuery = true)
	void updateActiveEventsBatch();

	Optional<Events> findByUserUserIdAndEventNameAndEventStatus(long hostUserId, String eventName, Status active);

	Optional<Events> findByUuid(String eventUUID);



}

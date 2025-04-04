package com.ymanch.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ymanch.entity.AddParticipant;
import com.ymanch.entity.EventCategory;
import com.ymanch.model.UserDetails;

@Repository
public interface AddParticipantRepository extends JpaRepository<AddParticipant, Long> {
	@Query("SELECT new com.ymanch.model.UserDetails(u.userId, u.userFirstName, u.userLastName, u.userProfileImagePath, u.userEmail, u.userCreatedAt,u.uuid) "
			+ "FROM AddParticipant ap " + "JOIN ap.participantUser u " + "WHERE ap.event.uuid = :eventUUID "
			+ "AND ap.event.user.userId = :hostUserId")
	Page<UserDetails> findAllUsersByHostId(Pageable pageable, long hostUserId, String eventUUID);

	Optional<AddParticipant> findByParticipantUserUserIdAndEventEventId(long userId, long eventId);

	boolean existsByParticipantUserUserIdAndEventEventId(long userId, long eventId);
	
//	 Optional<AddParticipant> findByEventIdAndParticipantUserId(Long eventId, Long userId);

}

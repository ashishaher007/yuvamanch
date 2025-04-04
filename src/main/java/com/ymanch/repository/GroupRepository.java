package com.ymanch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ymanch.entity.Group;
import com.ymanch.entity.User;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

	List<Group> findByMembersContaining(User user);

	List<Group> findByUser(User user);

//	Group findByUserAdminUserIdAndGroupId(long adminUserId, long groupId);

	Group findByUserUserId(long userId);

	Group findByUserUserIdAndGroupId(long adminUserId, long groupId);

	@Query("SELECT g FROM Group g WHERE g.guuid IS NULL OR g.guuid = ''")
	List<Group> findByGuuidIsNullOrEmpty();

	Optional<Group> findByGuuid(String groupUUID);

	@Query("SELECT g FROM Group g LEFT JOIN FETCH g.user")
	Page<Group> findAllWithUserDetails(Pageable pageable);
	
	

}

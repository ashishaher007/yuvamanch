package com.ymanch.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ymanch.entity.ChatMessage;
import com.ymanch.entity.User;
import com.ymanch.model.GroupChatModel;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

	List<ChatMessage> findBySender(User sender);

	List<ChatMessage> findByReceiver(User receiver);

	@Query("SELECT m FROM ChatMessage m WHERE (m.sender.userId = :userId1 AND m.receiver.userId = :userId2) OR (m.sender.userId = :userId2 AND m.receiver.userId = :userId1) ORDER BY m.id ASC")
	List<ChatMessage> findBySenderAndReceiver(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

	@Query("SELECT new com.ymanch.model.GroupChatModel(m.id, g.groupId, g.groupName, m.content, m.timestamp, "
			+ "m.sender.userId, m.sender.userFirstName) " + "FROM Group g " + "JOIN g.groupChat m " + "WHERE g.groupId = :groupId "
			+ "ORDER BY m.timestamp DESC")
	Page<GroupChatModel> findMessagesInGroup(long groupId, Pageable pageable);

}

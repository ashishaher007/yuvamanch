package com.ymanch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ymanch.entity.FriendRequest;
import com.ymanch.entity.User;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

	Optional<FriendRequest> findBySenderAndReceiver(User senderData, User receiverData);

	@Query(value = "Select count(fr.user_friend_request_id) from friend_request fr where fr.receiver_id=:userId or fr.sender_id=:userId and fr.status='APPROVED'", nativeQuery = true)
	long countFriendsByUserId(long userId);

	// old query
//	@Query("SELECT fr FROM FriendRequest fr WHERE (fr.sender = :sender AND fr.receiver = :receiver OR fr.sender = :receiver AND fr.receiver = :sender) AND fr.status IN ('PENDING', 'APPROVED')")
	@Query("SELECT fr FROM FriendRequest fr WHERE "
			+ "(fr.sender = :sender AND fr.receiver = :receiver AND fr.status IN ('PENDING', 'APPROVED')) " + "OR "
			+ "(fr.sender = :receiver AND fr.receiver = :sender AND fr.status IN ('PENDING', 'APPROVED'))")
	Optional<FriendRequest> findBidirectionalRequest(@Param("sender") User sender, @Param("receiver") User receiver);

	Optional<FriendRequest> findBySenderUserIdAndReceiverUserIdOrSenderUserIdAndReceiverUserId(long currentUserId,
			long targetUserId, long targetUserId2, long currentUserId2);

	@Query("SELECT COUNT(fr) FROM FriendRequest fr WHERE (fr.sender.id = :userId OR fr.receiver.id = :userId) AND fr.status = 'APPROVED'")
	long countApprovedFriends(@Param("userId") long userId);

	@Query("SELECT u FROM User u WHERE u.userId IN (" + "SELECT CASE "
			+ "WHEN fr.sender.id = :userId THEN fr.receiver.id " + "WHEN fr.receiver.id = :userId THEN fr.sender.id "
			+ "END " + "FROM FriendRequest fr " + "WHERE (fr.receiver.id = :userId OR fr.sender.id = :userId) "
			+ "AND fr.status = 'approved')")
	List<User> findFriendsBySenderReceiverApproved(@Param("userId") long userId);

	@Query(value = "SELECT * FROM user u LEFT JOIN friend_request fr ON u.user_id = CASE WHEN fr.sender_id=:userId THEN fr.receiver_id WHEN fr.receiver_id=:userId THEN fr.sender_id END WHERE (fr.sender_id=:userId OR fr.receiver_id=:userId) AND fr.status='APPROVED' AND u.full_name LIKE CONCAT(:userName, '%')", nativeQuery = true)
	List<User> findByUserFirstNameOrUserLastName(Long userId, String userName);

}

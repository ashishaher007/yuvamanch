package com.ymanch.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.ymanch.model.FriendsList;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Repository
@Slf4j
public class GroupServiceDao {

	private StringBuilder query;

	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	@SuppressWarnings("unchecked")
	public List<FriendsList> getRemainingFriendsList(long groupId, long userID) {
		log.info("***** Inside - GroupServiceDao - getRemainingFriendsList *****");
		query = new StringBuilder();
		query.append(
				"SELECT u.user_id, u.user_first_name, u.user_last_name, u.user_profile_image_path, fr.user_friend_request_id, fr.status FROM friend_request fr JOIN user u ON u.user_id = fr.sender_id OR u.user_id = fr.receiver_id LEFT JOIN group_members gm ON gm.user_id = u.user_id AND gm.group_id =:groupId WHERE gm.user_id IS NULL AND (fr.sender_id = :userID OR fr.receiver_id = :userID) AND (fr.sender_id = u.user_id OR fr.receiver_id = u.user_id)AND fr.status='APPROVED' AND u.user_id !=:userID");
		List<FriendsList> data = new ArrayList<>();
		try {
			Query createNativeQuery = entityManager.createNativeQuery(query.toString());
			createNativeQuery.setParameter("groupId", groupId);
			createNativeQuery.setParameter("userID", userID);

			List<Object[]> results = createNativeQuery.getResultList();
			for (Object[] resultArray : results) {
				FriendsList.FriendsListBuilder builder = FriendsList.builder();
				builder.userId(resultArray[0] != null ? Long.valueOf(resultArray[0].toString()) : null);
				builder.userFirstName(resultArray[1] != null ? resultArray[1].toString() : null);
				builder.userLastName(resultArray[2] != null ? resultArray[2].toString() : null);
				builder.userProfileImagePath(resultArray[3] != null ? resultArray[3].toString() : null);
				builder.friendRequestId(resultArray[4] != null ? Long.valueOf(resultArray[4].toString()) : null);
				builder.status(resultArray[5] != null ? resultArray[5].toString() : null);

				data.add(builder.build());
			}
		} catch (Exception e) {
			log.error("Error occurred while fetching remaining friends list: ", e);
		} finally {
			// Ensure the EntityManager is cleared to prevent connection leaks
			if (entityManager != null && entityManager.isOpen()) {
				entityManager.clear();
			}
		}

		return data;
	}

	@Transactional
	@SuppressWarnings("unchecked")
	public List<FriendsList> getRemainingFriendsList(long groupId, long userID, int page, int size) {
		log.info("***** Inside - GroupServiceDao - getRemainingFriendsList *****");
		query = new StringBuilder();
		query.append("SELECT u.user_id, u.user_first_name, u.user_last_name, u.user_profile_image_path, "
				+ "fr.user_friend_request_id, fr.status " + "FROM friend_request fr "
				+ "JOIN user u ON u.user_id = fr.sender_id OR u.user_id = fr.receiver_id "
				+ "LEFT JOIN group_members gm ON gm.user_id = u.user_id AND gm.group_id = :groupId "
				+ "WHERE gm.user_id IS NULL AND (fr.sender_id = :userID OR fr.receiver_id = :userID) "
				+ "AND (fr.sender_id = u.user_id OR fr.receiver_id = u.user_id) "
				+ "AND fr.status = 'APPROVED' AND u.user_id != :userID " + "LIMIT :size OFFSET :offset");

		List<FriendsList> data = new ArrayList<>();
		try {
			Query createNativeQuery = entityManager.createNativeQuery(query.toString());
			createNativeQuery.setParameter("groupId", groupId);
			createNativeQuery.setParameter("userID", userID);
			createNativeQuery.setParameter("size", size);
			createNativeQuery.setParameter("offset", page * size);

			List<Object[]> results = createNativeQuery.getResultList();
			for (Object[] resultArray : results) {
				FriendsList.FriendsListBuilder builder = FriendsList.builder();
				builder.userId(resultArray[0] != null ? Long.valueOf(resultArray[0].toString()) : null);
				builder.userFirstName(resultArray[1] != null ? resultArray[1].toString() : null);
				builder.userLastName(resultArray[2] != null ? resultArray[2].toString() : null);
				builder.userProfileImagePath(resultArray[3] != null ? resultArray[3].toString() : null);
				builder.friendRequestId(resultArray[4] != null ? Long.valueOf(resultArray[4].toString()) : null);
				builder.status(resultArray[5] != null ? resultArray[5].toString() : null);

				data.add(builder.build());
			}
		} catch (Exception e) {
			log.error("Error occurred while fetching remaining friends list: ", e);
		} finally {
			if (entityManager != null && entityManager.isOpen()) {
				entityManager.clear();
			}
		}

		return data;
	}

	public long getRemainingFriendsCount(long groupId, long userID) {
		log.info("***** Inside - GroupServiceDao - getRemainingFriendsCount *****");
		query = new StringBuilder();
		query.append("SELECT COUNT(*) " + "FROM friend_request fr "
				+ "JOIN user u ON u.user_id = fr.sender_id OR u.user_id = fr.receiver_id "
				+ "LEFT JOIN group_members gm ON gm.user_id = u.user_id AND gm.group_id = :groupId "
				+ "WHERE gm.user_id IS NULL AND (fr.sender_id = :userID OR fr.receiver_id = :userID) "
				+ "AND (fr.sender_id = u.user_id OR fr.receiver_id = u.user_id) "
				+ "AND fr.status = 'APPROVED' AND u.user_id != :userID");

		Query createNativeQuery = entityManager.createNativeQuery(query.toString());
		createNativeQuery.setParameter("groupId", groupId);
		createNativeQuery.setParameter("userID", userID);
		return ((Number) createNativeQuery.getSingleResult()).longValue();
	}

}

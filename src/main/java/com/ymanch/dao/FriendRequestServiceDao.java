package com.ymanch.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ymanch.model.FriendRequestList;
import com.ymanch.model.FriendsList;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Repository
@Slf4j
public class FriendRequestServiceDao {
	@PersistenceContext
	private EntityManager entitymanager;

	private StringBuilder query;

	@SuppressWarnings("unchecked")
	public List<FriendRequestList> getRequestList(long receiverId) {
		log.info("***** Inside - FriendRequestServiceDao - getRequestList *****");
		query = new StringBuilder();
		query.append(
				"select fr.sender_id,u.user_first_name,u.user_last_name,u.user_profile_image_path,u.user_email,fr.friend_request_created_at,fr.user_friend_request_id from user u left join friend_request fr ON u.user_id = fr.sender_id where fr.receiver_id=:receiverId and fr.status='PENDING';");
		ArrayList<FriendRequestList> data = new ArrayList<>();
		try {
			Query createNativeQuery = entitymanager.createNativeQuery(query.toString());
			createNativeQuery.setParameter("receiverId", receiverId);
			createNativeQuery.getResultList().stream().forEach(obj -> {
				Object resultArray[] = (Object[]) obj;
				String frDate = resultArray[5].toString();
				data.add(FriendRequestList.builder().userId(Long.valueOf(resultArray[0].toString()))
						.userFirstName(resultArray[1].toString()).userLastName(resultArray[2].toString())
						.userProfileImagePath(resultArray[3].toString())
						.friendRequestSentDate(
								frDate.indexOf('.') < 0 ? frDate : frDate.substring(0, frDate.indexOf('.')))
						.friendRequestId(Long.valueOf(resultArray[6].toString())).build());
			});
		} catch (Exception e) {
			log.error("Error occurred while fetching friend request list: ", e);
		} finally {
			// Ensure that the EntityManager is cleared, which will release the connection
			if (entitymanager != null && entitymanager.isOpen()) {
				entitymanager.clear();
			}
		}
		return data;
	}

	@Transactional
	@SuppressWarnings("unchecked")
	public List<FriendsList> getFriendsList(long userId) {
		log.info("***** Inside - FriendRequestServiceDao - getRequestList *****");
		query = new StringBuilder();
//		query.append(
//				"SELECT u.user_id AS userId, u.user_first_name AS userFirstName, u.user_last_name AS userLastName, u.user_profile_image_path AS userProfileImagePath, fr.user_friend_request_id AS friendRequestId, fr.status, u.uuid, COUNT(CASE WHEN fr.status = 'APPROVED' THEN 1 END) AS approvedCount FROM friend_request fr LEFT JOIN user u ON u.user_id = fr.receiver_id AND fr.sender_id = :userId LEFT JOIN user u2 ON u2.user_id = fr.sender_id AND fr.receiver_id = :userId WHERE (fr.sender_id = :userId OR fr.receiver_id = :userId) AND fr.status IN ('PENDING', 'APPROVED') AND u.user_id IS NOT NULL GROUP BY u.user_id, u.user_first_name, u.user_last_name, u.user_profile_image_path, fr.user_friend_request_id, fr.status, u.uuid ORDER BY fr.friend_request_created_at DESC");
		query.append(
			    "SELECT u.user_id AS userId, \r\n" +
			    "       u.user_first_name AS userFirstName, \r\n" +
			    "       u.user_last_name AS userLastName, \r\n" +
			    "       u.user_profile_image_path AS userProfileImagePath, \r\n" +
			    "       fr.user_friend_request_id AS friendRequestId, \r\n" +
			    "       fr.status, \r\n" +
			    "       u.uuid, \r\n" +
			    "       MAX(cm.timestamp) AS lastMessageTimestamp \r\n" +
			    "FROM friend_request fr \r\n" +
			    "LEFT JOIN user u ON u.user_id = CASE \r\n" +
			    "    WHEN fr.sender_id = :userId THEN fr.receiver_id \r\n" +
			    "    WHEN fr.receiver_id = :userId THEN fr.sender_id \r\n" +
			    "END \r\n" +
			    "LEFT JOIN chat_message cm ON ((cm.sender_id = u.user_id AND cm.receiver_id = :userId) \r\n" +
			    "                              OR (cm.receiver_id = u.user_id AND cm.sender_id = :userId)) \r\n" +
			    "WHERE (fr.sender_id = :userId OR fr.receiver_id = :userId) \r\n" +
			    "AND fr.status IN ('PENDING', 'APPROVED') \r\n" +
			    "AND u.user_id IS NOT NULL \r\n" +
			    "GROUP BY u.user_id, u.user_first_name, u.user_last_name, u.user_profile_image_path, \r\n" +
			    "         fr.user_friend_request_id, fr.status, u.uuid \r\n" +
			    "ORDER BY MAX(cm.timestamp) IS NULL, MAX(cm.timestamp) DESC");

		ArrayList<FriendsList> data = new ArrayList<>();
		try {
			Query createNativeQuery = entitymanager.createNativeQuery(query.toString());
			createNativeQuery.setParameter("userId", userId);

			createNativeQuery.getResultList().stream().forEach(obj -> {
				Object resultArray[] = (Object[]) obj;
				data.add(FriendsList.builder().userId(Long.valueOf(resultArray[0].toString()))
						.userFirstName(resultArray[1].toString()).userLastName(resultArray[2].toString())
						.userProfileImagePath(resultArray[3].toString())
						.friendRequestId(Long.valueOf(resultArray[4].toString())).status(resultArray[5].toString())
						.userUUID(resultArray[6].toString()).build());
			});
		} catch (Exception e) {
			log.error("Error occurred while fetching friends list: ", e);
		} finally {
			// Ensure that the EntityManager is cleared, which will release the connection
			if (entitymanager != null && entitymanager.isOpen()) {
				entitymanager.clear();
			}
		}
		return data;
	}

}

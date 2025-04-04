package com.ymanch.dao;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ymanch.model.PostIndex;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Repository
@Slf4j
public class UserServiceDao {

	@PersistenceContext
	private EntityManager entitymanager;

	private StringBuilder query;
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

	@SuppressWarnings("unchecked")
	public List<PostIndex> getPostIndexData(long userId) {
		log.info("***** Inside - getPostIndexData *****");
		query = new StringBuilder();
		query.append(
				"SELECT u.user_profile_image_path AS userProfileImageUrl, p.post_image_url AS postImageUrl, p.post_name AS postName,p.post_created_at AS postCreatedAt,u2.user_first_name AS lastReactUserName,COUNT(DISTINCT pr.post_react_id) -1  AS numberOfReacts,COUNT(DISTINCT pc.post_comment_id) AS numberOfComments,CONCAT(u.user_first_name, ' ', u.user_last_name) AS userName,p.post_id,u.user_id "
						+ "FROM posts p JOIN user u ON p.user_id=u.user_id LEFT JOIN post_react pr ON p.post_id=pr.post_id LEFT JOIN user u2 ON pr.user_id=u2.user_id LEFT JOIN post_comment pc ON p.post_id=pc.post_id WHERE u.user_id =:userId GROUP BY p.post_id ORDER BY p.post_created_at DESC");
		ArrayList<PostIndex> data = new ArrayList<>();
		try {
			Query createNativeQuery = entitymanager.createNativeQuery(query.toString());
			createNativeQuery.setParameter("userId", userId);
			createNativeQuery.getResultList().stream().forEach(obj -> {
				Object resultArray[] = (Object[]) obj;
				String pDate = resultArray[3].toString();
				Integer reactCount = resultArray[5] != null && Integer.parseInt(resultArray[5].toString()) >= 0
						? Integer.parseInt(resultArray[5].toString())
						: 0;
				String lastReactUserName = resultArray[4] != null ? resultArray[4].toString() : "No React";
				LocalDateTime postCreatedAt = null;
				try {
					postCreatedAt = LocalDateTime.parse(resultArray[3].toString(), formatter);
				} catch (DateTimeParseException e) {
					log.error("Date parsing error for postCreatedAt: " + e.getMessage());
				}
				data.add(PostIndex.builder().userProfileImageUrl(resultArray[0].toString())
						.postImageURl(resultArray[1].toString()).postName(resultArray[2].toString())
						.postCreatedAt(postCreatedAt).postLastReactedBy(lastReactUserName).totalCountOFReact(reactCount)
						.totalCountOfComments(Integer.valueOf(resultArray[6].toString()))
						.userName(resultArray[7].toString()).postId(Long.valueOf(resultArray[8].toString()))
						.userId(Long.valueOf(resultArray[9].toString())).build());
			});
		} catch (Exception e) {
			log.error("Error occurred while fetching getPostIndexData: ", e);
		}
		return data;
	}

}

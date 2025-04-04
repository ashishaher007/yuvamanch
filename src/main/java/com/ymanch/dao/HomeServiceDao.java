package com.ymanch.dao;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.ymanch.helper.DateTimeUtil;
import com.ymanch.model.PostIndex;
import com.ymanch.model.PostIndexComments;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Repository
@Slf4j
public class HomeServiceDao {

	@PersistenceContext
	private EntityManager entityManager;

	private StringBuilder query;

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

//	@SuppressWarnings("unchecked")
//	public List<PostIndex> getPostIndexData() {
//		log.info("***** Inside - HomeServiceImpl - getPostIndexData *****");
//		query = new StringBuilder();
//		query.append(
//				"SELECT u.user_profile_image_path AS userProfileImageUrl, p.post_image_url AS postImageUrl, p.post_name AS postName, p.post_created_at AS postCreatedAt, u2.user_first_name AS lastReactUserName, COUNT(DISTINCT pr.post_react_id) AS numberOfReacts, COUNT(DISTINCT pc.post_comment_id) AS numberOfComments, CONCAT(u.user_first_name, ' ', u.user_last_name) AS userName, p.post_id, u.user_id, p.post_type, p.video_thumbnail_url "
//						+ "FROM posts p JOIN user u ON p.user_id = u.user_id LEFT JOIN post_react pr ON p.post_id = pr.post_id AND pr.is_post_react_deleted = false LEFT JOIN user u2 ON pr.user_id = u2.user_id LEFT JOIN post_comment pc ON p.post_id = pc.post_id WHERE p.is_post_deleted = false GROUP BY p.post_id ORDER BY p.post_created_at DESC;");
//		ArrayList<PostIndex> data = new ArrayList<>();
//		try {
//			Query createNativeQuery = entityManager.createNativeQuery(query.toString());
//			createNativeQuery.getResultList().stream().forEach(obj -> {
//				Object resultArray[] = (Object[]) obj;
//
//				Integer reactCount = resultArray[5] != null && Integer.parseInt(resultArray[5].toString()) >= 0
//						? Integer.parseInt(resultArray[5].toString())
//						: 0;
//				String lastReactUserName = resultArray[4] != null ? resultArray[4].toString() : "No React";
//
//				// Parse the postCreatedAt with the correct format
//				LocalDateTime postCreatedAt = LocalDateTime.parse(resultArray[3].toString(), formatter);
//
//				data.add(PostIndex.builder().userProfileImageUrl(resultArray[0].toString())
//						.postImageURl(resultArray[1].toString()).postName(resultArray[2].toString())
//						.postCreatedAt(postCreatedAt).postLastReactedBy(lastReactUserName).totalCountOFReact(reactCount)
//						.totalCountOfComments(Integer.valueOf(resultArray[6].toString()))
//						.userName(resultArray[7].toString()).postId(Long.valueOf(resultArray[8].toString()))
//						.userId(Long.valueOf(resultArray[9].toString())).postType(resultArray[10].toString())
//						.videoThumbnailUrl(resultArray[11].toString()).build());
//			});
//		} catch (Exception e) {
//			log.error("Error occurred while fetching getPostIndexData: ", e);
//		}
//
//		return data;
//	}

	@SuppressWarnings("unchecked")
	public List<PostIndexComments> getPostIndexDataComments(long postId) {
		log.info("***** Inside - HomeServiceImpl - getPostIndexDataComments *****");
		StringBuilder query = new StringBuilder();
		query.append(
				"WITH RECURSIVE comments_cte AS (SELECT pc.post_comment_id AS parentCommentId,pc.comment,pc.comment_created_at,pc.parent_comment_id AS childCommentId,CONCAT(u.user_first_name, ' ', u.user_last_name) AS userName,u.user_profile_image_path,pc.post_id FROM post_comment pc JOIN user u ON pc.user_id = u.user_id WHERE pc.post_id IN (:postId) UNION ALL SELECT pc.post_comment_id AS parentCommentId,pc.comment,pc.comment_created_at,pc.parent_comment_id AS childCommentId,CONCAT(u.user_first_name, ' ', u.user_last_name) AS userName,u.user_profile_image_path,pc.post_id FROM post_comment pc JOIN user u ON pc.user_id = u.user_id JOIN comments_cte c ON pc.parent_comment_id = c.parentCommentId) SELECT parentCommentId,comment,comment_created_at,childCommentId,userName,user_profile_image_path,post_id FROM comments_cte ORDER BY comment_created_at DESC,parentCommentId,childCommentId;");
		Map<Long, PostIndexComments> commentMap = new HashMap<>();
		try {
			Query createNativeQuery = entityManager.createNativeQuery(query.toString());
			createNativeQuery.setParameter("postId", postId);
			List<Object[]> results = createNativeQuery.getResultList();
			// Convert results to PostIndexComments
			results.forEach(obj -> {

				String timeAgo = DateTimeUtil.convertUtcToLocalTimeAgo(((Timestamp) obj[2]).toLocalDateTime());
				long commentId = ((Number) obj[0]).longValue();
				String comment = (String) obj[1];
				long parentCommentId = obj[3] != null ? ((Number) obj[3]).longValue() : 0;
				String userName = (String) obj[4];
				String userProfileImageUrl = (String) obj[5];

				PostIndexComments postComment = PostIndexComments.builder().userProfileImageUrl(userProfileImageUrl)
						.commentTime(timeAgo).comment(comment).userName(userName).parentCommentId(parentCommentId)
						.childCommentId(commentId).children(new ArrayList<>()).build();

				commentMap.put(commentId, postComment);
			});
		} catch (Exception e) {
			log.error("Error occurred while fetching getPostIndexDataComments: ", e);

		}


		// Nest comments
		List<PostIndexComments> topLevelComments = new ArrayList<>();
		commentMap.values().forEach(comment -> {
			if (comment.getParentCommentId() == 0) {
				topLevelComments.add(comment);
			} else {
				PostIndexComments parentComment = commentMap.get(comment.getParentCommentId());
				if (parentComment != null) {
					parentComment.getChildren().add(comment);
				}
			}
		});

		// Sort top-level comments by commentTime in descending order
		topLevelComments.sort((c1, c2) -> c2.getCommentTime().compareTo(c1.getCommentTime()));

		// Sort children of each top-level comment
		for (PostIndexComments topComment : topLevelComments) {
			if (topComment.getChildren() != null) {
				topComment.getChildren().sort((c1, c2) -> c2.getCommentTime().compareTo(c1.getCommentTime()));
			}
		}

		return topLevelComments;
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> retrievelastActivityData(long userId) {
	    log.info("***** Inside - HomeServiceImpl - retrievelastActivityData *****");
	    query = new StringBuilder();
	    query.append("SELECT " +
	            "u.user_profile_image_path, " +
	            "u.user_first_name, " +
	            "u.user_last_name, " +
	            "activity.post_name, " +
	            "activity.created_at, " +
	            "activity.activity_type, " +
	            "activity.user_id " +  // Adding user_id to the select
	            "FROM (" +
	            "   SELECT user_id, post_name, post_created_at AS created_at, 'post' AS activity_type " +
	            "   FROM posts WHERE user_id = :userId " +
	            "   UNION " +
	            "   SELECT c.user_id, p.post_name, c.comment_created_at AS created_at, 'comment' AS activity_type " +
	            "   FROM post_comment c " +
	            "   JOIN posts p ON c.post_id = p.post_id WHERE c.user_id = :userId " +
	            "   UNION " +
	            "   SELECT r.user_id, p.post_name, r.post_react_created_at AS created_at, 'react' AS activity_type " +
	            "   FROM post_react r " +
	            "   JOIN posts p ON r.post_id = p.post_id WHERE r.user_id = :userId " +
	            ") activity " +
	            "JOIN user u ON activity.user_id = u.user_id " +
	            "WHERE u.user_role = 'ROLE_USER' " +
	            "ORDER BY activity.created_at DESC " +
	            "LIMIT 5");
	    Query createNativeQuery = entityManager.createNativeQuery(query.toString());
	    createNativeQuery.setParameter("userId", userId);
	    return createNativeQuery.getResultList();
	}

		

	}


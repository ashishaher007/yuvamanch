package com.ymanch.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ymanch.entity.Disputes;
import com.ymanch.model.AdminDisputeDetails;

@Repository
public interface DisputeRepository extends JpaRepository<Disputes, Long> {

	Optional<Disputes> findByUserUserIdAndPostPostId(long userId, long postId);

	@Query(value = "SELECT new com.ymanch.model.AdminDisputeDetails(MAX(d.disputeId), post_user.userId, p.postId, post_user.userFirstName, post_user.userLastName, post_user.userEmail, post_user.userProfileImagePath, p.postName, p.postImageUrl, COUNT(DISTINCT d1.disputeId)) "
			+ "FROM Disputes d " + "JOIN d.post p " + "JOIN p.user post_user "
			+ "LEFT JOIN Disputes d1 ON d1.post.postId = p.postId AND d1.user.userId = d.user.userId WHERE post_user.district.districtId = :districtId "
			 + "GROUP BY p.postId, post_user.userId, post_user.userFirstName, "
		        + "post_user.userLastName, post_user.userEmail, "
		        + "post_user.userProfileImagePath, p.postName, p.postImageUrl "  // Group by necessary fields
		        + "ORDER BY MAX(d.disputeId) DESC")
	Page<AdminDisputeDetails> getDisputeDetails(long districtId, Pageable pageable);

	@Query(value = "SELECT new com.ymanch.model.AdminDisputeDetails(MAX(d.disputeId), post_user.userId, p.postId, post_user.userFirstName, post_user.userLastName, post_user.userEmail, post_user.userProfileImagePath, p.postName, p.postImageUrl, COUNT(DISTINCT d1.disputeId)) "
			+ "FROM Disputes d " + "JOIN d.post p " + "JOIN p.user post_user "
			+ "LEFT JOIN Disputes d1 ON d1.post.postId = p.postId AND d1.user.userId = d.user.userId "
			 + "GROUP BY p.postId, post_user.userId, post_user.userFirstName, "
		        + "post_user.userLastName, post_user.userEmail, "
		        + "post_user.userProfileImagePath, p.postName, p.postImageUrl "  // Group by necessary fields
		        + "ORDER BY MAX(d.disputeId) DESC")
	Page<AdminDisputeDetails> getAllDisputeDetails(Pageable pageable);

}

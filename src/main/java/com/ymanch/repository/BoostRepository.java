package com.ymanch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ymanch.entity.Boost;
import com.ymanch.entity.Posts;
import com.ymanch.entity.User;

@Repository
public interface BoostRepository extends JpaRepository<Boost, Long> {

//	Boost findByPostId(Long postId);
	Optional<Boost> findByPostId(Long postId);

	@Query("SELECT b FROM Boost b WHERE b.post.id = :postId")
	Boost findByPostId2(Long postId);

	Boost findByUserAndPost(User user, Posts post);

	@Modifying
	@Query(value = "UPDATE boost SET boost_status = 'ENDED' WHERE CONCAT(end_date, ' ', end_time) <= NOW() AND boost_id=:boostId", nativeQuery = true)
	void updateEndedEventsBatch(long boostId);

	@Modifying
	@Query(value = "UPDATE boost SET boost_status = 'ACTIVE' WHERE CONCAT(end_date, ' ', end_time) > NOW() AND boost_id=:boostId", nativeQuery = true)
	void updateActiveEventsBatch(long boostId);

	@Query(value = "SELECT * FROM boost  WHERE boost_status = 'ACTIVE'", nativeQuery = true)
	List<Boost> findActiveBoosts();

//	Optional<Boost> findByUserId(Long userId); 
}

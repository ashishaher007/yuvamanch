package com.ymanch.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ymanch.entity.PostInsight;
import com.ymanch.entity.Posts;
import com.ymanch.entity.User;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface PostInsightRepository extends JpaRepository<PostInsight, Long> {
    
    // Find insight by post and user (to check if insight already exists)
    Optional<PostInsight> findByPostAndUser(Posts post, User user);

    @Query("SELECT p FROM PostInsight p WHERE p.post.postId = :postId")
   List< PostInsight> findAllByPostId(@Param("postId") long postId);

    @Query("SELECT d.districtName, SUM(p.viewCount) AS totalViews, SUM(p.reachCount) AS totalReach " +
    	       "FROM PostInsight p " +
    	       "LEFT JOIN p.district d " +  // Correcting the join condition here.
    	       "WHERE p.post.postId = :postId " +
    	       "GROUP BY d.districtId")  // You can group by districtId because it's associated with district.
    	List<Object[]> getDistrictAndPostWiseCounts(@Param("postId") Long postId);

}


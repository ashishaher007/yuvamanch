package com.ymanch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ymanch.entity.Hashtag;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
	
	Optional<Hashtag> findByName(String hashtagName);
	
	@Query(value = "SELECT * FROM hashtag WHERE name LIKE CONCAT(:userName, '%')", nativeQuery = true)
	List<Hashtag> findByHashtagName(@Param("userName") String userName);

}

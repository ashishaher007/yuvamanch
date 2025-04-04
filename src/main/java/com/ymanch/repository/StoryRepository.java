package com.ymanch.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ymanch.entity.Story;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {


	List<Story> findByStoryCreatedAtAfterOrderByStoryCreatedAtDesc(LocalDateTime cutoff);

    List<Story> findByUserUserIdAndStoryCreatedAtAfter(long userId,LocalDateTime cutoff);

}

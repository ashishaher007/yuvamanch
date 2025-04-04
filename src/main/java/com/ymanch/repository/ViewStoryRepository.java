package com.ymanch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ymanch.entity.Story;
import com.ymanch.entity.User;
import com.ymanch.entity.ViewStory;

import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
@Repository
public interface ViewStoryRepository extends JpaRepository<ViewStory,Long>{

    List<ViewStory> findByStoryAndViewCreatedAtAfter(Story story, LocalDateTime cutoff);

    @Query(value = "SELECT COUNT(*) FROM view_story WHERE user_id = :userId AND story_id = :storyId", nativeQuery = true)
    int countUserStoryViews(long userId, long storyId);
}

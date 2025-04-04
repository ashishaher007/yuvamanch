package com.ymanch.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ymanch.entity.EventCategory;

@Repository
public interface EventCategoryRepository extends JpaRepository<EventCategory, Long> {

	Optional<EventCategory> findByCatName(String catName);

}

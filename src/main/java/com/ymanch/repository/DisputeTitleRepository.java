package com.ymanch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ymanch.entity.DisputeTitles;

@Repository
public interface DisputeTitleRepository extends JpaRepository<DisputeTitles, Long> {

	DisputeTitles findByDisputeTitle(String disputeTitle);

}

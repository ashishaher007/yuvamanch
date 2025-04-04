package com.ymanch.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ymanch.entity.Organisation;

@Repository
public interface OrganisationRepository extends JpaRepository<Organisation, Long> {

	Optional<Organisation> findByOrgName(String orgName);

}

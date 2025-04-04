package com.ymanch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ymanch.entity.District;
import com.ymanch.model.admin.AdminModel.DistrictStats;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {

	@Query("SELECT u.district.districtName, " + "COUNT(DISTINCT p.postId), COUNT(DISTINCT u.userId), "
			+ "COUNT(DISTINCT d.disputeId), COUNT(DISTINCT g.groupId), COUNT(DISTINCT pg.pageId) " + "FROM User u "
			+ "LEFT JOIN u.posts p " + "LEFT JOIN u.dispute d " + "LEFT JOIN u.group g " + "LEFT JOIN u.pages pg "
			+ "GROUP BY u.district.districtName")
	List<Object[]> getStatsForAllDistricts();

	List<District> findAllByOrderByDistrictIdAsc();

}

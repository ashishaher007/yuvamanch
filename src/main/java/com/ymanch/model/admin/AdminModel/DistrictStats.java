package com.ymanch.model.admin.AdminModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DistrictStats {
	private long totalPosts;
	private long totalUsers;
	private long totalDisputes;
	private long totalGroups;
	private long totalPages;

	public DistrictStats(long totalPosts, long totalUsers, long totalDisputes, long totalGroups, long totalPages) {
		super();
		this.totalPosts = totalPosts;
		this.totalUsers = totalUsers;
		this.totalDisputes = totalDisputes;
		this.totalGroups = totalGroups;
		this.totalPages = totalPages;
	}

	public DistrictStats() {
		super();
	}

}

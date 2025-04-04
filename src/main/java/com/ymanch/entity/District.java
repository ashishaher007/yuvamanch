package com.ymanch.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
public class District {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long districtId;
	private String districtName;
}

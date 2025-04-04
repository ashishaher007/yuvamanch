package com.ymanch.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

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
public class ForgetPassword {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long forgetPasswordId;

	private String userEmail;

	private String otp;

	@CreationTimestamp
	private LocalDateTime forgetPasswordCreatedAt;
}

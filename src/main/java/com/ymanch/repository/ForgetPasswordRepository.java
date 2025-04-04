package com.ymanch.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ymanch.entity.ForgetPassword;

@Repository
public interface ForgetPasswordRepository extends JpaRepository<ForgetPassword, Long> {

	Optional<ForgetPassword> findByUserEmail(String emailId);

	Optional<ForgetPassword> findByOtp(String otp);

}

package com.satpall.crochet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.satpall.crochet.entity.OtpVerification;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

	Optional<OtpVerification> findByIdentifier(String identifier);

	void deleteByIdentifier(String identifier);

	void deleteByExpiryDateBefore(java.time.LocalDateTime expiryDate);

}

package com.satpall.crochet.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.PrePersist;

import lombok.Data;

@Entity
@Table(name = "otp_verification")
@Data
public class OtpVerification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String identifier;

	@Column(nullable = false)
	private String otp;

	@Column(nullable = false)
	private LocalDateTime expiryDate;

	@Column(nullable = false)
	private Boolean verified = false;

	@Column(nullable = false)
	private Integer attempts = 0;

	@Column(nullable = false)
	private Integer maxAttempts = 3;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onPersist() {
		createdAt = LocalDateTime.now();
	}

}

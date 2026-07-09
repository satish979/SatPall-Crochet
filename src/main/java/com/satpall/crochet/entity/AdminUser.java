package com.satpall.crochet.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "admin_user")
public class AdminUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Username is required")
	@Size(max = 50, message = "Username must be less than 50 characters")
	@Column(nullable = false, unique = true)
	private String username;

	@NotBlank(message = "Password is required")
	@Column(nullable = false)
	private String password;

	@NotBlank(message = "Email is required")
	@Email(message = "Email must be valid")
	@Column(nullable = false, unique = true)
	private String email;

	@Size(max = 100, message = "Full name must be less than 100 characters")
	@Column(name = "full_name")
	private String fullName;

	@Column(nullable = false)
	private Boolean active = true;

	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@Column(name = "last_login")
	private LocalDateTime lastLogin;

	@PrePersist
	protected void onPersist() {
		createdDate = LocalDateTime.now();
	}

}
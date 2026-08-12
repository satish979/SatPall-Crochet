package com.satpall.crochet.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "customer")
@Data
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Size(max = 50, message = "First name must be less than 50 characters")
	@Column(name = "first_name")
	private String firstName;

	@Size(max = 50, message = "Last name must be less than 50 characters")
	@Column(name = "last_name")
	private String lastName;

	@NotBlank(message = "Email is required")
	@Email(message = "Email must be valid")
	@Column(nullable = false, unique = true)
	private String email;

	@Size(max = 15, message = "Phone must be less than 15 characters")
	@Pattern(regexp = "^[0-9+\\-\\s()]+$", message = "Invalid phone number format")
	private String phone;

	@Column(columnDefinition = "TEXT")
	private String address;

	@Size(max = 50, message = "City must be less than 50 characters")
	private String city;

	@Size(max = 50, message = "State must be less than 50 characters")
	private String state;

	@Size(max = 10, message = "PIN code must be less than 10 characters")
	@Pattern(regexp = "^[0-9]{6}$", message = "Invalid PIN code format")
	@Column(name = "pin_code", length = 10)
	private String pinCode;

	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@Column(name = "last_login")
	private LocalDateTime lastLogin;

	@Column(nullable = false)
	private Boolean enabled = true;

	@Column(name = "password_hash")
	private String password;

	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<Order> orders = new ArrayList<>();

	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<WishlistItem> wishlistItems = new ArrayList<>();

	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<CustomerAddress> addresses = new ArrayList<>();

	@PrePersist
	protected void onPersist() {
		createdDate = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		if (createdDate == null) {
			createdDate = LocalDateTime.now();
		}
	}

}
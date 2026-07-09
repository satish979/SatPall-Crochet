package com.satpall.crochet.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Entity
@Table(name = "review")
@Data

public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@NotNull(message = "Rating is required")
	@Min(value = 1, message = "Rating must be at least 1")
	@Max(value = 5, message = "Rating must be at most 5")
	@Column(nullable = false)
	private Integer rating;

	@Size(max = 500, message = "Review must be less than 500 characters")
	@Column(columnDefinition = "TEXT")
	private String comment;

	@Size(max = 100, message = "Reviewer name must be less than 100 characters")
	@Column(name = "reviewer_name")
	private String reviewerName;

	@Column(name = "verified_purchase")
	private Boolean verifiedPurchase = false;

	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@Column(nullable = false)
	private Boolean active = true;

	@PrePersist
	protected void onPersist() {
		createdDate = LocalDateTime.now();
	}

}
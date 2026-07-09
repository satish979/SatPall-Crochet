package com.satpall.crochet.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

@Entity
@Table(name = "banner")
@Data
public class Banner {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Title is required")
	@Size(max = 200, message = "Title must be less than 200 characters")
	@Column(nullable = false)
	private String title;

	@Size(max = 1000, message = "Description must be less than 1000 characters")
	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(name = "image_url", nullable = false)
	private String imageUrl;

	@Column(name = "link_url")
	private String linkUrl;

	@Column(name = "display_order")
	private Integer displayOrder = 0;

	@Column(nullable = false)
	private Boolean active = true;

	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@PrePersist
	protected void onPersist() {
		createdDate = LocalDateTime.now();
	}

}

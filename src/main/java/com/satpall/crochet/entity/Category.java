package com.satpall.crochet.entity;

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
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

@Entity
@Table(name = "category")
@Data
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Category name is required")
	@Size(max = 100, message = "Category name must be less than 100 characters")
	@Column(nullable = false, unique = true)
	private String name;

	@Size(max = 500, message = "Description must be less than 500 characters")
	@Column(length = 500)
	private String description;

	@Column(name = "image_url")
	private String imageUrl;

	@Column(nullable = false)
	private Boolean active = true;

	@Column(name = "display_order")
	private Integer displayOrder = 0;

	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Product> products = new ArrayList<>();

}

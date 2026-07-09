package com.satpall.crochet.dto;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class ProductDTO {

	private Long id;

	@NotBlank(message = "Product name is required")
	@Size(max = 200, message = "Product name must be less than 200 characters")
	private String name;

	@Size(max = 5000, message = "Description must be less than 5000 characters")
	private String description;

	@NotNull(message = "Price is required")
	@DecimalMin(value = "0.01", message = "Price must be greater than 0")
	private BigDecimal price;

	@NotNull(message = "Stock quantity is required")
	@Min(value = 0, message = "Stock cannot be negative")
	private Integer stockQuantity;

	private Long categoryId;
	private String categoryName;

	private Boolean active = true;
	private Boolean featured = false;
	private Boolean newArrival = false;
	private Boolean bestSeller = false;

	private String imageUrl;

}

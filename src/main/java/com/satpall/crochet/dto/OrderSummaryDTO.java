package com.satpall.crochet.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderSummaryDTO {

	private Long productId;
	private String productName;
	private String imageUrl;
	private BigDecimal price;
	private Integer quantity;
	private BigDecimal subtotal;

}
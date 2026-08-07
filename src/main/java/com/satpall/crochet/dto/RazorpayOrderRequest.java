package com.satpall.crochet.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class RazorpayOrderRequest {

	private BigDecimal amount;
	private String currency;
	private String receipt;

}
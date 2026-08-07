package com.satpall.crochet.dto;

import lombok.Data;

@Data
public class RazorpayOrderResponse {

	private String razorpayOrderId;
	private String currency;
	private Integer amount;

}
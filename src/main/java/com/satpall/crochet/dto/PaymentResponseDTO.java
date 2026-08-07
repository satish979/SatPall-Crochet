package com.satpall.crochet.dto;

import com.satpall.crochet.enums.PaymentStatus;

import lombok.Data;

@Data
public class PaymentResponseDTO {

	private String razorpayOrderId;
	private String razorpayPaymentId;
	private String razorpaySignature;
	private PaymentStatus paymentStatus;
	private String message;

}
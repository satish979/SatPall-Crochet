package com.satpall.crochet.service;

import java.math.BigDecimal;

import com.razorpay.RazorpayException;
import com.satpall.crochet.dto.RazorpayOrderRequest;
import com.satpall.crochet.dto.RazorpayOrderResponse;
import com.satpall.crochet.entity.Order;

public interface PaymentService {

	RazorpayOrderResponse createRazorpayOrder(RazorpayOrderRequest request);

	boolean verifyPayment(String orderId, String paymentId, String signature);

	Order processCodOrder(Order order);

}
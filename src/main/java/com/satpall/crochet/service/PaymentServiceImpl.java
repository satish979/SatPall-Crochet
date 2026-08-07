package com.satpall.crochet.service;

import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import com.razorpay.RazorpayException;
import com.satpall.crochet.dto.RazorpayOrderRequest;
import com.satpall.crochet.dto.RazorpayOrderResponse;
import com.satpall.crochet.entity.Order;
import com.satpall.crochet.enums.PaymentStatus;
import com.satpall.crochet.exception.PaymentException;
import com.satpall.crochet.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final RazorpayService razorpayService;
	private final OrderRepository orderRepository;

	@Override
	public RazorpayOrderResponse createRazorpayOrder(RazorpayOrderRequest request) {
		try {
			com.razorpay.Order razorpayOrder = razorpayService.createRazorpayOrder(request.getAmount(), request.getReceipt());

			RazorpayOrderResponse response = new RazorpayOrderResponse();
			response.setRazorpayOrderId(razorpayOrder.get("id"));
			response.setCurrency(razorpayOrder.get("currency"));
			response.setAmount(Integer.parseInt(razorpayOrder.get("amount")));

			return response;
		} catch (RazorpayException e) {
			throw new PaymentException("Failed to create Razorpay order: " + e.getMessage());
		}
	}

	@Override
	public boolean verifyPayment(String orderId, String paymentId, String signature) {
		return razorpayService.verifyPaymentSignature(orderId, paymentId, signature);
	}

	@Override
	public Order processCodOrder(Order order) {
		order.setPaymentStatus(PaymentStatus.PENDING);
		order.setStatus(com.satpall.crochet.enums.OrderStatus.PLACED);
		return orderRepository.save(order);
	}

}
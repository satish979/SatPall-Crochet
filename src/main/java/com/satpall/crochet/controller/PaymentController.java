package com.satpall.crochet.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.razorpay.RazorpayException;
import com.satpall.crochet.dto.RazorpayOrderRequest;
import com.satpall.crochet.dto.RazorpayOrderResponse;
import com.satpall.crochet.entity.Order;
import com.satpall.crochet.enums.OrderStatus;
import com.satpall.crochet.enums.PaymentStatus;
import com.satpall.crochet.exception.PaymentException;
import com.satpall.crochet.repository.OrderRepository;
import com.satpall.crochet.service.EmailService;
import com.satpall.crochet.service.OrderService;
import com.satpall.crochet.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;
	private final OrderService orderService;
	private final OrderRepository orderRepository;
	private final EmailService emailService;

	@PostMapping("/razorpay/create-order")
	public ResponseEntity<?> createRazorpayOrder(@RequestBody RazorpayOrderRequest request) {
		try {
			RazorpayOrderResponse response = paymentService.createRazorpayOrder(request);
			return ResponseEntity.ok(response);
		} catch (PaymentException e) {
			return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
		}
	}

	@PostMapping("/razorpay/verify")
	public ResponseEntity<?> verifyRazorpayPayment(@RequestBody Map<String, String> payload) {
		try {
			String orderId = payload.get("razorpay_order_id");
			String paymentId = payload.get("razorpay_payment_id");
			String signature = payload.get("razorpay_signature");
			String orderNumber = payload.get("order_number");

			boolean valid = paymentService.verifyPayment(orderId, paymentId, signature);
			if (!valid) {
				return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Invalid payment signature"));
			}

			Order order = orderService.getOrderByOrderNumber(orderNumber);
			if (order == null) {
				return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Order not found"));
			}

			order.setRazorpayOrderId(orderId);
			order.setRazorpayPaymentId(paymentId);
			order.setRazorpaySignature(signature);
			order.setPaymentStatus(PaymentStatus.SUCCESS);
			order.setStatus(OrderStatus.PLACED);
			order.setTransactionDate(java.time.LocalDateTime.now());

			Order saved = orderRepository.save(order);
			orderService.updateOrderStatus(saved.getId(), OrderStatus.PLACED);

			emailService.sendPaymentSuccessEmail(saved);
			emailService.sendOrderConfirmation(saved);

			return ResponseEntity.ok(Map.of("success", true, "orderNumber", saved.getOrderNumber()));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
		}
	}

}
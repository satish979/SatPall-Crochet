package com.satpall.crochet.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.razorpay.RazorpayException;
import com.satpall.crochet.dto.CheckoutRequest;
import com.satpall.crochet.dto.OrderSummaryDTO;
import com.satpall.crochet.dto.RazorpayOrderRequest;
import com.satpall.crochet.dto.RazorpayOrderResponse;
import com.satpall.crochet.entity.Cart;
import com.satpall.crochet.entity.CartItem;
import com.satpall.crochet.entity.Order;
import com.satpall.crochet.entity.OrderItem;
import com.satpall.crochet.entity.Product;
import com.satpall.crochet.exception.PaymentException;
import com.satpall.crochet.repository.CartItemRepository;
import com.satpall.crochet.repository.CartRepository;
import com.satpall.crochet.repository.OrderRepository;
import com.satpall.crochet.repository.ProductRepository;
import com.satpall.crochet.service.EmailService;
import com.satpall.crochet.service.OrderService;
import com.satpall.crochet.service.PaymentService;
import com.satpall.crochet.service.RazorpayService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderApiController {

	private final OrderService orderService;
	private final PaymentService paymentService;
	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final ProductRepository productRepository;
	private final OrderRepository orderRepository;
	private final EmailService emailService;
	private final RazorpayService razorpayService;

	@PostMapping("/create-cod")
	public ResponseEntity<?> createCodOrder(@Valid @RequestBody CheckoutRequest request, HttpSession session) {
		try {
			List<OrderItem> items = getOrderItems(session);
			if (items.isEmpty()) {
				return ResponseEntity.badRequest().body(Map.of("success", false, "message", "No items in order"));
			}

			Order order = orderService.createOrder(request, items);
			emailService.sendOrderConfirmation(order);

			clearSessionItems(session);

			return ResponseEntity.ok(Map.of("success", true, "orderNumber", order.getOrderNumber()));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
		}
	}

	@PostMapping("/create-razorpay")
	public ResponseEntity<?> createRazorpayOrder(@Valid @RequestBody CheckoutRequest request, HttpSession session) {
		try {
			List<OrderItem> items = getOrderItems(session);
			if (items.isEmpty()) {
				return ResponseEntity.badRequest().body(Map.of("success", false, "message", "No items in order"));
			}

			Order order = orderService.createOrder(request, items);

			BigDecimal total = order.getTotal();
			RazorpayOrderRequest razorpayRequest = new RazorpayOrderRequest();
			razorpayRequest.setAmount(total);
			razorpayRequest.setCurrency("INR");
			razorpayRequest.setReceipt(order.getOrderNumber());

			RazorpayOrderResponse razorpayResponse = paymentService.createRazorpayOrder(razorpayRequest);

			order.setRazorpayOrderId(razorpayResponse.getRazorpayOrderId());
			orderRepository.save(order);

			return ResponseEntity.ok(Map.of("success", true, "orderNumber", order.getOrderNumber(), "razorpayOrderId",
					razorpayResponse.getRazorpayOrderId(), "amount", razorpayResponse.getAmount(), "currency",
					razorpayResponse.getCurrency(), "keyId", razorpayService.getRazorpayKeyId(), "customerName",
					order.getCustomerName(), "email", order.getEmail(), "phone", order.getPhone()));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
		}
	}

	private List<OrderItem> getOrderItems(HttpSession session) {
		Object buyNowItemObj = session.getAttribute("buyNowItem");
		if (buyNowItemObj instanceof OrderSummaryDTO) {
			OrderSummaryDTO dto = (OrderSummaryDTO) buyNowItemObj;
			Product product = productRepository.findById(dto.getProductId()).orElse(null);
			if (product == null) {
				return List.of();
			}

			OrderItem item = new OrderItem();
			item.setProduct(product);
			item.setProductName(product.getName());
			item.setPrice(product.getPrice());
			item.setQuantity(dto.getQuantity());
			item.setImageUrl(product.getImageUrl());
			return List.of(item);
		}

		Cart cart = cartRepository.findBySessionId(session.getId()).orElse(null);
		if (cart == null) {
			return List.of();
		}

		List<CartItem> cartItems = cartItemRepository.findByCart(cart);
		return cartItems.stream().map(cartItem -> {
			OrderItem item = new OrderItem();
			item.setProduct(cartItem.getProduct());
			item.setProductName(cartItem.getProduct().getName());
			item.setPrice(cartItem.getPrice());
			item.setQuantity(cartItem.getQuantity());
			item.setImageUrl(cartItem.getProduct().getImageUrl());
			return item;
		}).toList();
	}

	private void clearSessionItems(HttpSession session) {
		session.removeAttribute("buyNowItem");
		Cart cart = cartRepository.findBySessionId(session.getId()).orElse(null);
		if (cart != null) {
			cartItemRepository.deleteByCart(cart);
			cartRepository.delete(cart);
		}
	}

}
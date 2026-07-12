package com.satpall.crochet.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.satpall.crochet.dto.CartRequest;
import com.satpall.crochet.service.ProductService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

	private final ProductService productService;

	CartController(ProductService productService) {
		this.productService = productService;
	}

	@PostMapping("/buy-now")
	public ResponseEntity<Map<String, Object>> buyNow(@RequestBody CartRequest request) {

		// TODO: Save temporary cart/order

		Map<String, Object> response = new HashMap<>();

		response.put("success", true);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/cart")
	public String cart(Model model) {
		model.addAttribute("pageTitle", "Cart");
		return "cart";
	}

	@GetMapping("/checkout")
	public String checkout(Model model) {
		model.addAttribute("pageTitle", "Checkout");
		return "checkout";
	}

	@GetMapping("/order-success")
	public String orderSuccess(Model model) {
		model.addAttribute("pageTitle", "Order Success");
		return "order-success";
	}

	@PostMapping("/add")
	public ResponseEntity<?> addToCart(@RequestBody CartRequest request, HttpSession session) {

		productService.addToCart(request.getProductId(), request.getQuantity(), session.getId());

		Map<String, Object> response = new HashMap<>();

		response.put("success", true);
		response.put("message", "Product added to cart.");

		return ResponseEntity.ok(response);

	}
}
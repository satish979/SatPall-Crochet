package com.satpall.crochet.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.satpall.crochet.dto.CartRequest;
import com.satpall.crochet.entity.Product;
import com.satpall.crochet.service.ProductService;

@RestController
@Validated
@RequestMapping("/api/cart")
public class CartController {

	private final ProductService productService;

	CartController(ProductService productService) {
		this.productService = productService;
	}

	@PostMapping("/buy-now")
	public ResponseEntity<Map<String, Object>> buyNow(@Valid @RequestBody CartRequest request, HttpSession session) {

		Product product = productService.getProduct(request.getProductId());

		if (product == null) {
			Map<String, Object> response = new HashMap<>();
			response.put("success", false);
			response.put("message", "Product not found");
			return ResponseEntity.badRequest().body(response);
		}

		com.satpall.crochet.dto.OrderSummaryDTO item = new com.satpall.crochet.dto.OrderSummaryDTO();
		item.setProductId(product.getId());
		item.setProductName(product.getName());
		item.setImageUrl(product.getImageUrl());
		item.setPrice(product.getPrice());
		item.setQuantity(request.getQuantity());
		item.setSubtotal(product.getPrice().multiply(java.math.BigDecimal.valueOf(request.getQuantity())));

		session.setAttribute("buyNowItem", item);

		Map<String, Object> response = new HashMap<>();
		response.put("success", true);

		System.out.println("item " + item);
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
	public ResponseEntity<Map<String, Object>> addToCart(@Valid @RequestBody CartRequest request, HttpSession session) {
		Map<String, Object> response = new HashMap<>();

		try {
			int cartCount = productService.addToCart(request.getProductId(), request.getQuantity(),
					session.getId());

			response.put("success", true);
			response.put("message", "Product added to cart.");
			response.put("cartCount", cartCount);

			return ResponseEntity.ok(response);

		} catch (IllegalArgumentException ex) {
			response.put("success", false);
			response.put("message", ex.getMessage());
			return ResponseEntity.badRequest().body(response);
		} catch (Exception ex) {
			response.put("success", false);
			response.put("message", "Failed to add product to cart. Please try again.");
			return ResponseEntity.status(500).body(response);
		}
	}
}
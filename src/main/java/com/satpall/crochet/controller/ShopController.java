package com.satpall.crochet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ShopController {

	@GetMapping("/product-details/{id}")
	public String productDetails(@PathVariable Long id, Model model) {
		model.addAttribute("pageTitle", "Product Details");
		model.addAttribute("productId", id);
		return "product-details";
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
}
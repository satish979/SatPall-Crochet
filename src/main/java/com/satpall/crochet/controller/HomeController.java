package com.satpall.crochet.controller;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.satpall.crochet.entity.Product;
import com.satpall.crochet.service.ProductService;

@Controller
public class HomeController {

	@Autowired
	private ProductService productService;

	@GetMapping("/")
	public String home(Model model) {

		model.addAttribute("pageTitle", "Home");

		model.addAttribute("categories", Arrays.asList("Flowers", "Bouquets", "Teddy Bears", "Dolls", "Keychains",
				"Bags", "Baby Items", "Home Decor"));

		model.addAttribute("bestSellers", productService.getBestSellerProducts());

		return "index";
	}

	@GetMapping("/shop")
	public String shop(Model model) {

		model.addAttribute("pageTitle", "Shop");

		model.addAttribute("products", productService.getAllProducts());

		return "shop";
	}

	@GetMapping("/about")
	public String about(Model model) {
		model.addAttribute("pageTitle", "About");
		return "about";
	}

	@GetMapping("/contact")
	public String contact(Model model) {
		model.addAttribute("pageTitle", "Contact");
		return "contact";
	}

	@GetMapping("/faq")
	public String faq(Model model) {
		model.addAttribute("pageTitle", "FAQ");
		return "faq";
	}

	@GetMapping("/privacy-policy")
	public String privacyPolicy(Model model) {
		model.addAttribute("pageTitle", "Privacy Policy");
		return "privacy-policy";
	}

	@GetMapping("/terms")
	public String terms(Model model) {
		model.addAttribute("pageTitle", "Terms");
		return "terms";
	}

	@GetMapping("/cart")
	public String cart(Model model) {

		model.addAttribute("pageTitle", "Shopping Cart");

		return "cart";
	}

	@GetMapping("/checkout")
	public String checkout(Model model) {
		model.addAttribute("pageTitle", "Checkout");
		return "checkout";
	}

	@GetMapping("/403")
	public String notAccessbile(Model model) {
		model.addAttribute("pageTitle", "403");
		return "403";
	}

	@GetMapping("/product-details/{id}")
	public String productDetails(@PathVariable Long id, Model model) {

		Product product = productService.getProduct(id);

		if (product == null) {
			return "redirect:/shop";
		}

		model.addAttribute("pageTitle", product.getName());
		model.addAttribute("product", product);

		return "product-details";
	}
}
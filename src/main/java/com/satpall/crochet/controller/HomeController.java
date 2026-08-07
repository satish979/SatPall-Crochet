package com.satpall.crochet.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.satpall.crochet.dto.OrderSummaryDTO;
import com.satpall.crochet.entity.Cart;
import com.satpall.crochet.entity.CartItem;
import com.satpall.crochet.entity.Product;
import com.satpall.crochet.repository.CartItemRepository;
import com.satpall.crochet.repository.CartRepository;
import com.satpall.crochet.service.OrderService;
import com.satpall.crochet.service.ProductService;

@Controller
public class HomeController {

	@Autowired
	private ProductService productService;
	@Autowired
	private CartRepository cartRepository;
	@Autowired
	private CartItemRepository cartItemRepository;
	@Autowired
	private OrderService orderService;

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
	public String cart(Model model, HttpSession session) {

		model.addAttribute("pageTitle", "Shopping Cart");

		Cart cart = cartRepository.findBySessionId(session.getId()).orElse(null);
		if (cart != null) {
			List<CartItem> cartItems = cartItemRepository.findByCart(cart);
			List<OrderSummaryDTO> items = orderService.buildOrderSummary(cartItems);
			model.addAttribute("items", items);
			model.addAttribute("subtotal", items.stream()
					.map(item -> item.getSubtotal() != null ? item.getSubtotal() : java.math.BigDecimal.ZERO)
					.reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
		} else {
			model.addAttribute("items", new java.util.ArrayList<>());
			model.addAttribute("subtotal", java.math.BigDecimal.ZERO);
		}

		return "cart";
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
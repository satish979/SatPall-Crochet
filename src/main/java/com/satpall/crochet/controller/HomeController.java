package com.satpall.crochet.controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.satpall.crochet.dto.OrderSummaryDTO;
import com.satpall.crochet.entity.Cart;
import com.satpall.crochet.entity.CartItem;
import com.satpall.crochet.entity.Category;
import com.satpall.crochet.entity.Product;
import com.satpall.crochet.repository.CartItemRepository;
import com.satpall.crochet.repository.CartRepository;
import com.satpall.crochet.repository.CategoryRepository;
import com.satpall.crochet.service.EmailService;
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
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private EmailService emailService;

	@GetMapping("/")
	public String home(Model model) {

		model.addAttribute("pageTitle", "Home");

		model.addAttribute("categories", categoryRepository.findByActiveOrderByDisplayOrderAscNameAsc(true));

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

	@PostMapping("/contact")
	public String handleContactForm(
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String email,
			@RequestParam(required = false) String inquiryType,
			@RequestParam(required = false) String message,
			RedirectAttributes redirectAttributes) {

		if (name == null || name.trim().isEmpty() || email == null || email.trim().isEmpty() || message == null || message.trim().isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "Please fill in all required fields (Name, Email, Message).");
			return "redirect:/contact";
		}

		try {
			emailService.sendContactUsEmail(name.trim(), email.trim(), inquiryType, message.trim());
			redirectAttributes.addFlashAttribute("success", "Thank you for reaching out! Your message has been sent to our support team.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Could not send your message at this moment. Please try again or reach out directly.");
		}

		return "redirect:/contact";
	}

	@PostMapping("/api/contact")
	@ResponseBody
	public Map<String, Object> handleContactApi(
			@RequestBody(required = false) Map<String, String> payload,
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String email,
			@RequestParam(required = false) String inquiryType,
			@RequestParam(required = false) String message) {

		Map<String, Object> response = new HashMap<>();

		String senderName = name;
		String senderEmail = email;
		String senderInquiry = inquiryType;
		String senderMsg = message;

		if (payload != null && !payload.isEmpty()) {
			if (payload.containsKey("name")) senderName = payload.get("name");
			if (payload.containsKey("email")) senderEmail = payload.get("email");
			if (payload.containsKey("inquiryType")) senderInquiry = payload.get("inquiryType");
			if (payload.containsKey("message")) senderMsg = payload.get("message");
		}

		if (senderName == null || senderName.trim().isEmpty() || senderEmail == null || senderEmail.trim().isEmpty() || senderMsg == null || senderMsg.trim().isEmpty()) {
			response.put("success", false);
			response.put("message", "Please fill in all required fields (Name, Email, and Message).");
			return response;
		}

		try {
			emailService.sendContactUsEmail(senderName.trim(), senderEmail.trim(), senderInquiry, senderMsg.trim());
			response.put("success", true);
			response.put("message", "Thank you for reaching out! Your message has been sent to our artisan support team.");
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "Failed to send message. Please try again or email us directly at hello@loomellecrochet.in");
		}

		return response;
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
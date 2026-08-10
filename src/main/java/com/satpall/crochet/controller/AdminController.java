package com.satpall.crochet.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.satpall.crochet.dto.ProductForm;
import com.satpall.crochet.entity.Category;
import com.satpall.crochet.entity.Order;
import com.satpall.crochet.entity.Product;
import com.satpall.crochet.enums.OrderStatus;
import com.satpall.crochet.enums.PaymentStatus;
import com.satpall.crochet.exception.OrderException;
import com.satpall.crochet.repository.CategoryRepository;
import com.satpall.crochet.repository.OrderRepository;
import com.satpall.crochet.service.AdminService;
import com.satpall.crochet.service.OrderService;
import com.satpall.crochet.service.ProductService;

@Controller
public class AdminController {

	private final AdminService adminService;
	private final ProductService productService;
	private final CategoryRepository categoryRepository;

	private final OrderService orderService;
	private final OrderRepository orderRepository;

	public AdminController(AdminService adminService, ProductService productService,
			CategoryRepository categoryRepository, OrderService orderService, OrderRepository orderRepository) {
		this.adminService = adminService;
		this.productService = productService;
		this.categoryRepository = categoryRepository;
		this.orderService = orderService;
		this.orderRepository = orderRepository;
	}

	@ModelAttribute("categories")
	public List<Category> categories() {
		return categoryRepository.findAllByOrderByDisplayOrderAscNameAsc();
	}

	@GetMapping("/admin/login")
	public String login() {
		return "login";
	}

	@GetMapping("/logout")
	public String logout() {
		return "login";
	}

	@GetMapping("/admin/products")
	public String products(Model model) {
		model.addAttribute("pageTitle", "Manage Products");
		model.addAttribute("products", productService.getAllProducts());
		return "admin/products";
	}

	@GetMapping("/admin/products/new")
	public String newProduct(Model model) {
		model.addAttribute("pageTitle", "Add Product");
		model.addAttribute("productForm", new ProductForm());
		model.addAttribute("categories", productService.getAllProducts());
		return "admin/product-form";
	}

	@GetMapping("/admin/products/{id}/edit")
	public String editProduct(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
		Product product = productService.getProduct(id);
		if (product == null) {
			redirectAttributes.addFlashAttribute("error", true);
			return "redirect:/admin/products";
		}
		model.addAttribute("pageTitle", "Edit Product");
		model.addAttribute("productForm", ProductForm.fromProduct(product));
		return "admin/product-form";
	}

	@PostMapping("/admin/products/save")
	public String saveProduct(@Valid @ModelAttribute("productForm") ProductForm productForm,
			BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("pageTitle", productForm.getId() != null ? "Edit Product" : "Add Product");
			return "admin/product-form";
		}

		try {
			productService.saveProduct(productForm);
		} catch (IllegalArgumentException | IllegalStateException e) {
			bindingResult.reject("product.save.error", e.getMessage());
			model.addAttribute("pageTitle", productForm.getId() != null ? "Edit Product" : "Add Product");
			return "admin/product-form";
		}

		redirectAttributes.addFlashAttribute("success", true);
		return "redirect:/admin/products";
	}

	@PostMapping("/admin/products/{id}/delete")
	public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		productService.deleteProduct(id);
		redirectAttributes.addFlashAttribute("deleted", true);
		return "redirect:/admin/products";
	}

	@GetMapping("/admin/categories")
	public String categories(Model model) {
		model.addAttribute("pageTitle", "Manage Categories");
		model.addAttribute("categories", categoryRepository.findAllByOrderByDisplayOrderAscNameAsc());
		return "admin/categories";
	}

	@GetMapping("/admin/orders")
	public String orders(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) String search, @RequestParam(required = false) String status,
			@RequestParam(required = false) String paymentStatus, Model model) {

		Pageable pageable = PageRequest.of(page, size);
		Page<Order> ordersPage;

		if (search != null && !search.trim().isEmpty()) {
			ordersPage = orderService.searchOrders(search, pageable);
		} else if (status != null && !status.isEmpty()) {
			try {
				OrderStatus orderStatus = OrderStatus.valueOf(status);
				ordersPage = orderService.getOrdersByStatus(orderStatus, pageable);
			} catch (IllegalArgumentException e) {
				ordersPage = orderService.getAllOrders(pageable);
			}
		} else if (paymentStatus != null && !paymentStatus.isEmpty()) {
			try {
				PaymentStatus pStatus = PaymentStatus.valueOf(paymentStatus);
				ordersPage = orderRepository.findByPaymentStatusOrderByCreatedDateDesc(pStatus, pageable);
			} catch (IllegalArgumentException e) {
				ordersPage = orderService.getAllOrders(pageable);
			}
		} else {
			ordersPage = orderService.getAllOrders(pageable);
		}

		model.addAttribute("pageTitle", "Manage Orders");

		model.addAttribute("ordersPage", ordersPage);
		model.addAttribute("statuses", OrderStatus.values());
		model.addAttribute("paymentStatuses", PaymentStatus.values());
		return "admin/orders";
	}

	@GetMapping("/admin/orders/{id}")
	public String orderDetail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
		Order order;
		try {
			order = orderService.getOrderById(id);
		} catch (OrderException e) {
			redirectAttributes.addFlashAttribute("error", "Order not found");
			return "redirect:/admin/orders";
		}
		model.addAttribute("pageTitle", "Order " + order.getOrderNumber());
		model.addAttribute("order", order);
		model.addAttribute("statuses", OrderStatus.values());
		return "admin/order-detail";
	}

	@PostMapping("/admin/orders/{id}/status")
	public String updateOrderStatus(@PathVariable Long id, @RequestParam String status,
			RedirectAttributes redirectAttributes) {
		try {
			OrderStatus orderStatus = OrderStatus.valueOf(status);
			orderService.updateOrderStatus(id, orderStatus);
			redirectAttributes.addFlashAttribute("success", true);
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("error", "Invalid status");
		}
		return "redirect:/admin/orders/" + id;
	}

	@GetMapping("/admin/dashboard")
	public String dashboard(Model model) {

		adminService.loadDashboard(model);

		return "dashboard";
	}
}
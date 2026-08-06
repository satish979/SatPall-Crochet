package com.satpall.crochet.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.satpall.crochet.dto.ProductForm;
import com.satpall.crochet.entity.Category;
import com.satpall.crochet.entity.Product;
import com.satpall.crochet.repository.CategoryRepository;
import com.satpall.crochet.service.AdminService;
import com.satpall.crochet.service.ProductService;

@Controller
public class AdminController {

	private final AdminService adminService;
	private final ProductService productService;
	private final CategoryRepository categoryRepository;

	public AdminController(AdminService adminService, ProductService productService, CategoryRepository categoryRepository) {
		this.adminService = adminService;
		this.productService = productService;
		this.categoryRepository = categoryRepository;
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
		return "admin/categories";
	}

	@GetMapping("/admin/orders")
	public String orders(Model model) {
		model.addAttribute("pageTitle", "Manage Orders");
		return "admin/orders";
	}

	@GetMapping("/admin/dashboard")
	public String dashboard(Model model) {

		adminService.loadDashboard(model);

		return "dashboard";
	}
}
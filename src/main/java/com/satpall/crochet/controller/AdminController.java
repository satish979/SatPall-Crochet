package com.satpall.crochet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.satpall.crochet.service.AdminService;

@Controller
public class AdminController {

	@Autowired
	private AdminService adminService;

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
		return "admin/products";
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
package com.satpall.crochet.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/admin/login")
    public String login(Model model) {
        model.addAttribute("pageTitle", "Admin Login");
        return "admin/login";
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Admin Dashboard");
        return "admin/dashboard";
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
}
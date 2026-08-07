package com.satpall.crochet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.satpall.crochet.entity.Category;
import com.satpall.crochet.repository.CategoryRepository;

@Controller
public class CategoryAdminController {

    private final CategoryRepository categoryRepository;

    public CategoryAdminController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/admin/categories/new")
    public String newCategory(Model model) {
        model.addAttribute("pageTitle", "Add Category");
        model.addAttribute("category", new Category());
        return "admin/category-form";
    }

    @GetMapping("/admin/categories/{id}/edit")
    public String editCategory(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            redirectAttributes.addFlashAttribute("error", true);
            return "redirect:/admin/categories";
        }
        model.addAttribute("pageTitle", "Edit Category");
        model.addAttribute("category", category);
        return "admin/category-form";
    }

    @PostMapping("/admin/categories/save")
    public String saveCategory(@ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", true);
            return "redirect:/admin/categories";
        }

        if (category.getActive() == null) {
            category.setActive(true);
        }
        if (category.getDisplayOrder() == null) {
            category.setDisplayOrder(0);
        }
        if (category.getImageUrl() == null || category.getImageUrl().isBlank()) {
            category.setImageUrl("/images/product-placeholder.jpg");
        }

        categoryRepository.save(category);
        redirectAttributes.addFlashAttribute("success", true);
        return "redirect:/admin/categories";
    }

    @PostMapping("/admin/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        categoryRepository.findById(id).ifPresent(categoryRepository::delete);
        redirectAttributes.addFlashAttribute("deleted", true);
        return "redirect:/admin/categories";
    }
}

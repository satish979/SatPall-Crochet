package com.satpall.crochet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.satpall.crochet.entity.Category;
import com.satpall.crochet.repository.CategoryRepository;
import com.satpall.crochet.service.CategoryService;

@Controller
public class CategoryAdminController {

    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;

    public CategoryAdminController(CategoryRepository categoryRepository, CategoryService categoryService) {
        this.categoryRepository = categoryRepository;
        this.categoryService = categoryService;
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
    public String saveCategory(@ModelAttribute Category category,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "removeImage", required = false, defaultValue = "false") boolean removeImage,
            RedirectAttributes redirectAttributes) {
        try {
            categoryService.saveCategory(category, imageFile, removeImage);
            redirectAttributes.addFlashAttribute("success", true);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", true);
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/admin/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        categoryService.deleteCategory(id);
        redirectAttributes.addFlashAttribute("deleted", true);
        return "redirect:/admin/categories";
    }
}

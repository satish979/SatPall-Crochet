package com.satpall.crochet.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.satpall.crochet.entity.Category;
import com.satpall.crochet.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CloudinaryService cloudinaryService;

    public List<Category> getAllCategories() {
        return categoryRepository.findAllByOrderByDisplayOrderAscNameAsc();
    }

    public Category saveCategory(Category category, MultipartFile imageFile, boolean removeImage) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name is required");
        }

        Category existing = category.getId() != null ? categoryRepository.findById(category.getId()).orElse(null) : null;

        if (existing != null) {
            if (removeImage) {
                if (existing.getImageUrl() != null && !existing.getImageUrl().isBlank()) {
                    cloudinaryService.deleteImage(existing.getImageUrl());
                }
                category.setImageUrl(null);
            } else if (imageFile != null && !imageFile.isEmpty()) {
                if (existing.getImageUrl() != null && !existing.getImageUrl().isBlank()) {
                    cloudinaryService.deleteImage(existing.getImageUrl());
                }
                String imageUrl = cloudinaryService.uploadImage(imageFile);
                category.setImageUrl(imageUrl);
            } else {
                category.setImageUrl(existing.getImageUrl());
            }
        } else {
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(imageFile);
                category.setImageUrl(imageUrl);
            } else if (category.getImageUrl() == null || category.getImageUrl().isBlank()) {
                category.setImageUrl(null);
            }
        }

        if (category.getActive() == null) {
            category.setActive(true);
        }
        if (category.getDisplayOrder() == null) {
            category.setDisplayOrder(0);
        }

        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category != null) {
            if (category.getImageUrl() != null && !category.getImageUrl().isBlank()) {
                cloudinaryService.deleteImage(category.getImageUrl());
            }
            categoryRepository.deleteById(id);
        }
    }
}

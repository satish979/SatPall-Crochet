package com.satpall.crochet.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import com.satpall.crochet.entity.Product;

import lombok.Data;

@Data
public class ProductForm {

    private Long id;

    @NotBlank(message = "Product name is required")
    @Size(max = 200, message = "Product name must be less than 200 characters")
    private String name;

    @Size(max = 5000, message = "Description must be less than 5000 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stockQuantity;

    @NotNull(message = "Category is required")
    private Long categoryId;

    private Boolean active = true;
    private Boolean featured = false;
    private Boolean newArrival = false;
    private Boolean bestSeller = false;

    private String imageUrl;
    private boolean removeMainImage = false;

    private List<String> existingAdditionalImages = new ArrayList<>();
    private List<String> removeAdditionalImages = new ArrayList<>();

    private MultipartFile imageFile;
    private List<MultipartFile> additionalFiles = new ArrayList<>();

    public static ProductForm fromProduct(Product product) {
        ProductForm form = new ProductForm();
        form.setId(product.getId());
        form.setName(product.getName());
        form.setDescription(product.getDescription());
        form.setPrice(product.getPrice());
        form.setStockQuantity(product.getStockQuantity());
        form.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
        form.setActive(product.getActive());
        form.setFeatured(product.getFeatured());
        form.setNewArrival(product.getNewArrival());
        form.setBestSeller(product.getBestSeller());
        form.setImageUrl(product.getImageUrl());
        if (product.getAdditionalImages() != null) {
            form.setExistingAdditionalImages(new ArrayList<>(product.getAdditionalImages()));
        }
        return form;
    }
}

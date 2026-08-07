package com.satpall.crochet.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.satpall.crochet.dto.ProductForm;
import com.satpall.crochet.entity.Cart;
import com.satpall.crochet.entity.CartItem;
import com.satpall.crochet.entity.Category;
import com.satpall.crochet.entity.Product;
import com.satpall.crochet.repository.CartItemRepository;
import com.satpall.crochet.repository.CartRepository;
import com.satpall.crochet.repository.CategoryRepository;
import com.satpall.crochet.repository.ProductRepository;

@Service
public class ProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final CloudinaryService cloudinaryService;
	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;

	public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository,
			CloudinaryService cloudinaryService, CartRepository cartRepository, CartItemRepository cartItemRepository) {
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
		this.cloudinaryService = cloudinaryService;
		this.cartRepository = cartRepository;
		this.cartItemRepository = cartItemRepository;
	}

	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	public Product getProduct(Long id) {
		return productRepository.findById(id).orElse(null);
	}

	public List<Product> getBestSellerProducts() {
		return productRepository.findByBestSellerTrueAndActiveTrue();
	}

	public void saveProduct(ProductForm form) {
		Product product = Optional.ofNullable(form.getId()).flatMap(productRepository::findById)
				.orElseGet(Product::new);

		product.setName(form.getName());
		product.setDescription(form.getDescription());
		product.setPrice(form.getPrice());
		product.setStockQuantity(form.getStockQuantity());
		product.setActive(form.getActive());
		product.setFeatured(form.getFeatured());
		product.setNewArrival(form.getNewArrival());
		product.setBestSeller(form.getBestSeller());

		Category category = categoryRepository.findById(form.getCategoryId())
				.orElseThrow(() -> new IllegalArgumentException("Selected category does not exist"));
		product.setCategory(category);

		if (form.isRemoveMainImage() && StringUtils.hasText(product.getImageUrl())) {
			cloudinaryService.deleteImage(product.getImageUrl());
			product.setImageUrl(null);
		}

		if (form.getImageFile() != null && !form.getImageFile().isEmpty()) {
			String imageUrl = cloudinaryService.uploadImage(form.getImageFile());
			if (StringUtils.hasText(product.getImageUrl())) {
				cloudinaryService.deleteImage(product.getImageUrl());
			}
			product.setImageUrl(imageUrl);
		}

		List<String> currentAdditional = product.getAdditionalImages() != null
				? new ArrayList<>(product.getAdditionalImages())
				: new ArrayList<>();

		// Remove selected images
		if (!CollectionUtils.isEmpty(form.getRemoveAdditionalImages())) {
			for (String image : form.getRemoveAdditionalImages()) {
				cloudinaryService.deleteImage(image);
			}

			currentAdditional.removeAll(form.getRemoveAdditionalImages());
		}

		// Upload new images
		if (!CollectionUtils.isEmpty(form.getAdditionalFiles())) {
			for (MultipartFile file : form.getAdditionalFiles()) {
				if (!file.isEmpty()) {
					String imageUrl = cloudinaryService.uploadImage(file);
					currentAdditional.add(imageUrl);
				}
			}
		}

		product.setAdditionalImages(currentAdditional);

		product.setAdditionalImages(currentAdditional);
		productRepository.save(product);
	}

	public void deleteProduct(Long productId) {
		productRepository.findById(productId).ifPresent(product -> {
			if (StringUtils.hasText(product.getImageUrl())) {
				cloudinaryService.deleteImage(product.getImageUrl());
			}
			if (!CollectionUtils.isEmpty(product.getAdditionalImages())) {
				product.getAdditionalImages().forEach(cloudinaryService::deleteImage);
			}
			productRepository.delete(product);
		});
	}

	public void addToCart(Long productId, Integer quantity, String sessionId) {

		Cart cart = cartRepository.findBySessionId(sessionId).orElseGet(() -> {

			Cart c = new Cart();
			c.setSessionId(sessionId);

			return cartRepository.save(c);

		});

		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("Product not found"));

		CartItem item = new CartItem();

		item.setCart(cart);
		item.setProduct(product);
		item.setQuantity(quantity);
		item.setPrice(product.getPrice());

		cartItemRepository.save(item);

	}
}
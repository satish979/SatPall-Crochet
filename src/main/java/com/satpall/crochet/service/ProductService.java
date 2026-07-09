package com.satpall.crochet.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.satpall.crochet.entity.Product;
import com.satpall.crochet.repository.ProductRepository;

@Service
public class ProductService {

	private final ProductRepository productRepository;

	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
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
}
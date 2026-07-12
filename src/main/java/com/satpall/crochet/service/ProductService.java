package com.satpall.crochet.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.satpall.crochet.entity.Cart;
import com.satpall.crochet.entity.CartItem;
import com.satpall.crochet.entity.Product;
import com.satpall.crochet.repository.CartItemRepository;
import com.satpall.crochet.repository.CartRepository;
import com.satpall.crochet.repository.ProductRepository;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private CartItemRepository cartItemRepository;

	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	public Product getProduct(Long id) {
		return productRepository.findById(id).orElse(null);
	}

	public List<Product> getBestSellerProducts() {
		return productRepository.findByBestSellerTrueAndActiveTrue();
	}

	public void addToCart(Long productId, Integer quantity, String sessionId) {

		Cart cart = cartRepository.findBySessionId(sessionId).orElseGet(() -> {

			Cart c = new Cart();
			c.setSessionId(sessionId);

			return cartRepository.save(c);

		});

		Product product = productRepository.findById(productId).orElseThrow();

		CartItem item = new CartItem();

		item.setCart(cart);
		item.setProduct(product);
		item.setQuantity(quantity);
		item.setPrice(product.getPrice());

		cartItemRepository.save(item);

	}
}
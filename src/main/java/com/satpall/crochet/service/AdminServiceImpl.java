package com.satpall.crochet.service;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.satpall.crochet.enums.OrderStatus;
import com.satpall.crochet.repository.CategoryRepository;
import com.satpall.crochet.repository.OrderRepository;
import com.satpall.crochet.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

	private final ProductRepository productRepository;
	private final OrderRepository orderRepository;
	private final CategoryRepository categoryRepository;

	public AdminServiceImpl(ProductRepository productRepository, OrderRepository orderRepository,
			CategoryRepository categoryRepository) {
		this.productRepository = productRepository;
		this.orderRepository = orderRepository;
		this.categoryRepository = categoryRepository;
	}

	@Override
	public void loadDashboard(Model model) {

		model.addAttribute("totalProducts", productRepository.count());
		model.addAttribute("totalOrders", orderRepository.count());
		model.addAttribute("pendingOrders", orderRepository.countByStatus(OrderStatus.PENDING));
		model.addAttribute("completedOrders", orderRepository.countByStatus(OrderStatus.DELIVERED));
		model.addAttribute("totalCategories", categoryRepository.count());
		model.addAttribute("revenue", orderRepository.getTotalRevenue());
	}
}
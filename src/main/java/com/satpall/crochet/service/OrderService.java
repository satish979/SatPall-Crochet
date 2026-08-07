package com.satpall.crochet.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.satpall.crochet.dto.CheckoutRequest;
import com.satpall.crochet.dto.OrderSummaryDTO;
import com.satpall.crochet.entity.CartItem;
import com.satpall.crochet.entity.Order;
import com.satpall.crochet.enums.OrderStatus;

public interface OrderService {

	Order createOrder(CheckoutRequest request, List<com.satpall.crochet.entity.OrderItem> items);

	Order getOrderById(Long id);

	Order getOrderByOrderNumber(String orderNumber);

	Page<Order> getAllOrders(Pageable pageable);

	Page<Order> getOrdersByStatus(OrderStatus status, Pageable pageable);

	Page<Order> searchOrders(String keyword, Pageable pageable);

	Order updateOrderStatus(Long orderId, OrderStatus status);

	Order cancelOrder(Long orderId, String reason);

	List<OrderSummaryDTO> buildOrderSummary(List<CartItem> cartItems);

}
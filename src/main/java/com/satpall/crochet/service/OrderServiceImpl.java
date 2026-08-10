package com.satpall.crochet.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.satpall.crochet.dto.CheckoutRequest;
import com.satpall.crochet.dto.OrderSummaryDTO;
import com.satpall.crochet.entity.Order;
import com.satpall.crochet.entity.OrderItem;
import com.satpall.crochet.entity.Product;
import com.satpall.crochet.enums.OrderStatus;
import com.satpall.crochet.enums.PaymentMethod;
import com.satpall.crochet.enums.PaymentStatus;
import com.satpall.crochet.exception.OrderException;
import com.satpall.crochet.repository.OrderRepository;
import com.satpall.crochet.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepository;
	private final ProductRepository productRepository;
	private final PaymentService paymentService;
	private final EmailService emailService;

	@Value("${app.shipping.amount:99}")
	private BigDecimal shippingAmount;

	@Value("${app.tax.rate:0.05}")
	private BigDecimal taxRate;

	@Value("${app.discount.threshold:1000}")
	private BigDecimal discountThreshold;

	@Value("${app.discount.amount:100}")
	private BigDecimal discountAmount;

	@Override
	@Transactional
	public Order createOrder(CheckoutRequest request, List<OrderItem> items, Long customerId) {

		Order order = new Order();
		order.setOrderNumber(generateOrderNumber());
		order.setCustomerName(request.getCustomerName());
		order.setEmail(request.getEmail());
		order.setPhone(request.getPhone());
		order.setAddressLine1(request.getAddressLine1());
		order.setAddressLine2(request.getAddressLine2());
		order.setCity(request.getCity());
		order.setState(request.getState());
		order.setCountry(request.getCountry());
		order.setPinCode(request.getPinCode());
		order.setNotes(request.getNotes());

		if (customerId != null) {
			com.satpall.crochet.entity.Customer customer = new com.satpall.crochet.entity.Customer();
			customer.setId(customerId);
			order.setCustomer(customer);
		}

		String shippingAddress = buildAddressString(request);
		order.setShippingAddress(shippingAddress);
		order.setBillingAddress(shippingAddress);

		PaymentMethod paymentMethod = PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase());
		order.setPaymentMethod(paymentMethod);

		BigDecimal subtotal = BigDecimal.ZERO;
		for (OrderItem item : items) {
			item.setOrder(order);
			item.setTotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
			subtotal = subtotal.add(item.getTotal());
		}
		order.setOrderItems(items);
		order.setSubtotal(subtotal);

		BigDecimal tax = subtotal.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
		order.setTax(tax);

		BigDecimal discount = subtotal.compareTo(discountThreshold) >= 0 ? discountAmount : BigDecimal.ZERO;
		order.setDiscount(discount);

		order.setShipping(shippingAmount);
		order.setTotal(subtotal.add(tax).add(shippingAmount).subtract(discount));

		if (paymentMethod == PaymentMethod.COD) {
			order.setPaymentStatus(PaymentStatus.PENDING);
			order.setStatus(OrderStatus.PLACED);
		} else {
			order.setPaymentStatus(PaymentStatus.PENDING);
			order.setStatus(OrderStatus.PENDING);
		}

		Order savedOrder = orderRepository.save(order);

		for (OrderItem item : items) {
			Product product = item.getProduct();
			product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
			productRepository.save(product);
		}

		return savedOrder;
	}

	@Override
	public Order getOrderById(Long id) {
		return orderRepository.findById(id).orElseThrow(() -> new OrderException("Order not found with id: " + id));
	}

	@Override
	public Order getOrderByOrderNumber(String orderNumber) {
		return orderRepository.findByOrderNumber(orderNumber);
	}

	@Override
	public Page<Order> getAllOrders(Pageable pageable) {
		return orderRepository.findAllByOrderByCreatedDateDesc(pageable);
	}

	@Override
	public Page<Order> getOrdersByStatus(OrderStatus status, Pageable pageable) {
		return orderRepository.findByStatusOrderByCreatedDateDesc(status, pageable);
	}

	@Override
	public Page<Order> searchOrders(String keyword, Pageable pageable) {
		return orderRepository.findAll((root, query, cb) -> {
			if (keyword == null || keyword.trim().isEmpty()) {
				return cb.conjunction();
			}
			String like = "%" + keyword.toLowerCase() + "%";
			return cb.or(cb.like(cb.lower(root.get("orderNumber")), like),
					cb.like(cb.lower(root.get("customerName")), like), cb.like(cb.lower(root.get("phone")), like),
					cb.like(cb.lower(root.get("email")), like));
		}, pageable);
	}

	@Override
	public Page<Order> getOrdersByCustomer(Long customerId, Pageable pageable) {
		return orderRepository.findByCustomerIdOrderByCreatedDateDesc(customerId, pageable);
	}

	@Override
	@Transactional
	public Order updateOrderStatus(Long orderId, OrderStatus status) {
		Order order = getOrderById(orderId);
		order.setStatus(status);
		if (status == OrderStatus.SHIPPED) {
			order.setShippedDate(LocalDateTime.now());
		} else if (status == OrderStatus.DELIVERED) {
			order.setDeliveredDate(LocalDateTime.now());
		} else if (status == OrderStatus.CANCELLED) {
			order.setCancelledDate(LocalDateTime.now());
		}
		return orderRepository.save(order);
	}

	@Override
	@Transactional
	public Order cancelOrder(Long orderId, String reason) {
		Order order = getOrderById(orderId);
		order.setStatus(OrderStatus.CANCELLED);
		order.setCancellationReason(reason);
		order.setCancelledDate(LocalDateTime.now());
		return orderRepository.save(order);
	}

	private String generateOrderNumber() {
		return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
	}

	private String buildAddressString(CheckoutRequest request) {
		StringBuilder sb = new StringBuilder();
		if (request.getAddressLine1() != null && !request.getAddressLine1().isEmpty()) {
			sb.append(request.getAddressLine1());
		}
		if (request.getAddressLine2() != null && !request.getAddressLine2().isEmpty()) {
			if (sb.length() > 0)
				sb.append(", ");
			sb.append(request.getAddressLine2());
		}
		sb.append(", ").append(request.getCity()).append(", ").append(request.getState());
		if (request.getCountry() != null && !request.getCountry().isEmpty()) {
			sb.append(", ").append(request.getCountry());
		}
		sb.append(" - ").append(request.getPinCode());
		return sb.toString();
	}

	public List<OrderSummaryDTO> buildOrderSummary(List<com.satpall.crochet.entity.CartItem> cartItems) {
		List<OrderSummaryDTO> summary = new java.util.ArrayList<>();
		BigDecimal subtotal = BigDecimal.ZERO;

		for (com.satpall.crochet.entity.CartItem cartItem : cartItems) {
			OrderSummaryDTO dto = new OrderSummaryDTO();
			Product product = cartItem.getProduct();
			dto.setProductId(product.getId());
			dto.setProductName(product.getName());
			dto.setImageUrl(product.getImageUrl());
			dto.setPrice(cartItem.getPrice());
			dto.setQuantity(cartItem.getQuantity());
			BigDecimal itemTotal = cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
			dto.setSubtotal(itemTotal);
			subtotal = subtotal.add(itemTotal);
			summary.add(dto);
		}

		System.out.println("summary " + summary);
		return summary;
	}

}
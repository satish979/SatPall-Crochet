package com.satpall.crochet.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.satpall.crochet.dto.CheckoutRequest;
import com.satpall.crochet.dto.OrderSummaryDTO;
import com.satpall.crochet.entity.Cart;
import com.satpall.crochet.entity.CartItem;
import com.satpall.crochet.entity.Customer;
import com.satpall.crochet.entity.CustomerAddress;
import com.satpall.crochet.entity.Order;
import com.satpall.crochet.enums.OrderStatus;
import com.satpall.crochet.exception.OrderException;
import com.satpall.crochet.repository.CartItemRepository;
import com.satpall.crochet.repository.CartRepository;
import com.satpall.crochet.service.CustomerAddressService;
import com.satpall.crochet.service.CustomerService;
import com.satpall.crochet.service.EmailService;
import com.satpall.crochet.service.OrderService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;
	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final EmailService emailService;
	private final CustomerService customerService;
	private final CustomerAddressService customerAddressService;

	@GetMapping("/checkout")
	public String checkout(Model model, HttpSession session) {
		model.addAttribute("pageTitle", "Checkout");

		Long customerId = (Long) session.getAttribute("customerId");
		if (customerId != null) {
			Customer customer = customerService.getCurrentCustomer(customerId).orElse(null);
			if (customer != null) {
				model.addAttribute("loggedInCustomer", customer);
				List<CustomerAddress> addresses = customerAddressService.getAddresses(customerId);
				model.addAttribute("savedAddresses", addresses);
				CustomerAddress defaultAddress = customerAddressService.getDefaultAddress(customerId).orElse(null);
				model.addAttribute("defaultAddress", defaultAddress);
			}
		}

		Object buyNowItem = session.getAttribute("buyNowItem");
		if (buyNowItem != null) {
			model.addAttribute("buyNowMode", true);
			model.addAttribute("items", List.of(buyNowItem));
		} else {
			Cart cart = cartRepository.findBySessionId(session.getId()).orElse(null);
			if (cart != null) {
				List<CartItem> cartItems = cartItemRepository.findByCart(cart);
				List<OrderSummaryDTO> summaries = orderService.buildOrderSummary(cartItems);
				model.addAttribute("items", summaries);
				model.addAttribute("buyNowMode", false);
			} else {
				model.addAttribute("items", List.of());
				model.addAttribute("buyNowMode", false);
			}
		}

		model.addAttribute("checkoutRequest", new CheckoutRequest());

		System.out.println("model " + model);
		return "checkout";
	}

	@GetMapping("/my-orders")
	public String myOrders(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) String status, Model model, HttpSession session) {

		Pageable pageable = PageRequest.of(page, size);
		Page<Order> ordersPage;

		Long customerId = (Long) session.getAttribute("customerId");

		if (customerId != null) {
			if (status != null && !status.isEmpty()) {
				try {
					OrderStatus orderStatus = OrderStatus.valueOf(status);
					ordersPage = orderService.getOrdersByStatus(orderStatus, pageable);
				} catch (IllegalArgumentException e) {
					ordersPage = orderService.getOrdersByCustomer(customerId, pageable);
				}
			} else {
				ordersPage = orderService.getOrdersByCustomer(customerId, pageable);
			}
		} else {
			if (status != null && !status.isEmpty()) {
				try {
					OrderStatus orderStatus = OrderStatus.valueOf(status);
					ordersPage = orderService.getOrdersByStatus(orderStatus, pageable);
				} catch (IllegalArgumentException e) {
					ordersPage = orderService.getAllOrders(pageable);
				}
			} else {
				ordersPage = orderService.getAllOrders(pageable);
			}
		}

		model.addAttribute("pageTitle", "My Orders");
		model.addAttribute("ordersPage", ordersPage);
		model.addAttribute("statuses", OrderStatus.values());
		model.addAttribute("selectedStatus", status);
		return "my-orders";
	}

	@GetMapping("/my-orders/{orderNumber}")
	public String orderDetail(@PathVariable String orderNumber, Model model, HttpSession session) {
		Order order = orderService.getOrderByOrderNumber(orderNumber);
		if (order == null) {
			return "redirect:/my-orders";
		}

		Long customerId = (Long) session.getAttribute("customerId");
		if (customerId != null && order.getCustomer() != null && !customerId.equals(order.getCustomer().getId())) {
			return "redirect:/my-orders";
		}

		model.addAttribute("pageTitle", "Order " + orderNumber);
		model.addAttribute("order", order);
		return "order-detail";
	}

	@GetMapping("/order-success")
	public String orderSuccess(@RequestParam(required = false) String orderNumber, Model model) {
		Order order = null;
		if (orderNumber != null) {
			order = orderService.getOrderByOrderNumber(orderNumber);
		}
		model.addAttribute("pageTitle", "Order Placed Successfully");
		model.addAttribute("order", order);
		return "order-success";
	}

	@GetMapping("/order-payment")
	public String orderPayment(@RequestParam(required = false) String orderNumber, Model model) {
		Order order = null;
		if (orderNumber != null) {
			order = orderService.getOrderByOrderNumber(orderNumber);
		}
		if (order == null) {
			return "redirect:/checkout";
		}
		model.addAttribute("pageTitle", "Payment");
		model.addAttribute("order", order);
		return "order-payment";
	}

	@GetMapping("/order-failure")
	public String orderFailure(@RequestParam(required = false) String orderNumber,
			@RequestParam(required = false) String reason, Model model) {
		model.addAttribute("pageTitle", "Payment Failed");
		model.addAttribute("orderNumber", orderNumber);
		model.addAttribute("reason", reason);
		return "order-failure";
	}

	@GetMapping("/invoice/{orderNumber}")
	public String invoice(@PathVariable String orderNumber, Model model) {
		Order order = orderService.getOrderByOrderNumber(orderNumber);
		if (order == null) {
			return "redirect:/my-orders";
		}
		model.addAttribute("pageTitle", "Invoice - " + orderNumber);
		model.addAttribute("order", order);
		return "invoice";
	}
	

}
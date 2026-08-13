package com.satpall.crochet.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.satpall.crochet.dto.FeedbackForm;
import com.satpall.crochet.entity.Order;
import com.satpall.crochet.entity.OrderItem;
import com.satpall.crochet.entity.Review;
import com.satpall.crochet.exception.OrderException;
import com.satpall.crochet.repository.OrderRepository;
import com.satpall.crochet.service.OrderService;
import com.satpall.crochet.service.ReviewService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CustomerFeedbackController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final ReviewService reviewService;

    @Value("${server.servlet.context-path:/Loomellecrochet}")
    private String contextPath;

    @GetMapping("/customer/feedback/{orderNumber}")
    public String feedbackForm(@PathVariable String orderNumber, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Long customerId = (Long) session.getAttribute("customerId");
        if (customerId == null) {
            try {
                redirectAttributes.addFlashAttribute("error", "Please login to submit feedback");
                return "redirect:/customer/login?redirectAfterLogin=" + URLEncoder.encode(contextPath + "/customer/feedback/" + orderNumber, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return "redirect:/customer/login";
            }
        }

        Order order = orderService.getOrderByOrderNumber(orderNumber);
        if (order == null || order.getCustomer() == null || !customerId.equals(order.getCustomer().getId())) {
            redirectAttributes.addFlashAttribute("error", "Order not found");
            return "redirect:/my-orders";
        }

        if (order.getStatus() != com.satpall.crochet.enums.OrderStatus.DELIVERED) {
            redirectAttributes.addFlashAttribute("error", "Feedback can only be submitted for delivered orders");
            return "redirect:/my-orders";
        }

        if (reviewService.hasCustomerReviewedOrder(customerId, order.getId())) {
            Review existingReview = reviewService.getReviewByCustomerAndOrder(customerId, order.getId());
            model.addAttribute("pageTitle", "Share Feedback");
            model.addAttribute("order", order);
            OrderItem firstItem = order.getOrderItems() != null && !order.getOrderItems().isEmpty()
                    ? order.getOrderItems().get(0) : null;
            model.addAttribute("product", firstItem != null ? firstItem.getProduct() : null);
            model.addAttribute("feedbackSuccess", true);
            model.addAttribute("existingReview", existingReview);
            return "customer/feedback";
        }

        model.addAttribute("pageTitle", "Share Feedback");
        model.addAttribute("order", order);

        OrderItem firstItem = order.getOrderItems() != null && !order.getOrderItems().isEmpty()
                ? order.getOrderItems().get(0) : null;
        model.addAttribute("product", firstItem != null ? firstItem.getProduct() : null);

        if (!model.containsAttribute("feedbackForm")) {
            model.addAttribute("feedbackForm", new FeedbackForm());
        }

        return "customer/feedback";
    }

    @PostMapping("/customer/feedback/{orderNumber}")
    public String submitFeedback(@PathVariable String orderNumber,
                                 @Valid @ModelAttribute FeedbackForm feedbackForm,
                                 BindingResult bindingResult,
                                 HttpSession session,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        Long customerId = (Long) session.getAttribute("customerId");
        if (customerId == null) {
            redirectAttributes.addFlashAttribute("error", "Please login to submit feedback");
            return "redirect:/customer/login";
        }

        Order order = orderService.getOrderByOrderNumber(orderNumber);
        if (order == null || order.getCustomer() == null || !customerId.equals(order.getCustomer().getId())) {
            redirectAttributes.addFlashAttribute("error", "Order not found");
            return "redirect:/my-orders";
        }

        if (order.getStatus() != com.satpall.crochet.enums.OrderStatus.DELIVERED) {
            redirectAttributes.addFlashAttribute("error", "Feedback can only be submitted for delivered orders");
            return "redirect:/my-orders";
        }

        if (reviewService.hasCustomerReviewedOrder(customerId, order.getId())) {
            Review existingReview = reviewService.getReviewByCustomerAndOrder(customerId, order.getId());
            model.addAttribute("pageTitle", "Share Feedback");
            model.addAttribute("order", order);
            OrderItem firstItem = order.getOrderItems() != null && !order.getOrderItems().isEmpty()
                    ? order.getOrderItems().get(0) : null;
            model.addAttribute("product", firstItem != null ? firstItem.getProduct() : null);
            model.addAttribute("feedbackSuccess", true);
            model.addAttribute("existingReview", existingReview);
            return "customer/feedback";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Share Feedback");
            model.addAttribute("order", order);
            OrderItem firstItem = order.getOrderItems() != null && !order.getOrderItems().isEmpty()
                    ? order.getOrderItems().get(0) : null;
            model.addAttribute("product", firstItem != null ? firstItem.getProduct() : null);
            return "customer/feedback";
        }

        OrderItem firstItem = order.getOrderItems() != null && !order.getOrderItems().isEmpty()
                ? order.getOrderItems().get(0) : null;
        if (firstItem == null) {
            redirectAttributes.addFlashAttribute("error", "No product found in this order");
            return "redirect:/my-orders";
        }

        reviewService.submitReview(customerId, order.getId(), firstItem.getProduct().getId(),
                feedbackForm.getRating(), feedbackForm.getComment());

        return "redirect:/customer/feedback/" + orderNumber;
    }
}

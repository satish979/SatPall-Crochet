package com.satpall.crochet.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.satpall.crochet.entity.Customer;
import com.satpall.crochet.entity.Order;
import com.satpall.crochet.entity.Product;
import com.satpall.crochet.entity.Review;
import com.satpall.crochet.exception.OrderException;
import com.satpall.crochet.repository.OrderRepository;
import com.satpall.crochet.repository.ProductRepository;
import com.satpall.crochet.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Review submitReview(Long customerId, Long orderId, Long productId, Integer rating, String comment) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException("Order not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new OrderException("Product not found"));

        Optional<Review> existing = reviewRepository.findByCustomerIdAndOrderIdAndProductId(customerId, orderId, productId);
        if (existing.isPresent()) {
            throw new OrderException("You have already submitted feedback for this order/product");
        }

        Review review = new Review();
        review.setProduct(product);
        review.setCustomer(new Customer() {{ setId(customerId); }});
        review.setOrder(order);
        review.setRating(rating);
        review.setComment(comment != null ? comment.trim() : "");
        review.setVerifiedPurchase(true);
        review.setActive(true);

        return reviewRepository.save(review);
    }

    @Override
    public boolean hasCustomerReviewedOrder(Long customerId, Long orderId) {
        return reviewRepository.findByCustomerIdAndOrderId(customerId, orderId)
                .stream()
                .anyMatch(Review::getActive);
    }

    @Override
    public Review getReviewByCustomerAndOrder(Long customerId, Long orderId) {
        return reviewRepository.findByCustomerIdAndOrderId(customerId, orderId)
                .stream()
                .findFirst()
                .orElse(null);
    }
}

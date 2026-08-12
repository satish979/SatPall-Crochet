package com.satpall.crochet.service;

import com.satpall.crochet.entity.Review;

public interface ReviewService {

    Review submitReview(Long customerId, Long orderId, Long productId, Integer rating, String comment);

    boolean hasCustomerReviewedOrder(Long customerId, Long orderId);

    Review getReviewByCustomerAndOrder(Long customerId, Long orderId);
}

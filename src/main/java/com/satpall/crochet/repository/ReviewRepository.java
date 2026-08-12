package com.satpall.crochet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.satpall.crochet.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    List<Review> findByProductIdOrderByCreatedDateDesc(Long productId);
    
    List<Review> findByProductIdAndActiveOrderByCreatedDateDesc(Long productId, Boolean active);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId AND r.active = true")
    Double findAverageRatingByProductId(@Param("productId") Long productId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId AND r.active = true")
    Long countReviewsByProductId(@Param("productId") Long productId);
    
    void deleteByProductId(Long productId);

    List<Review> findByOrderIdOrderByCreatedDateDesc(Long orderId);

    Optional<Review> findByCustomerIdAndOrderIdAndProductId(Long customerId, Long orderId, Long productId);

    List<Review> findByCustomerIdAndOrderId(Long customerId, Long orderId);
}
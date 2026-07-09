package com.satpall.crochet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.satpall.crochet.entity.WishlistItem;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    
    List<WishlistItem> findByCustomerIdOrderByCreatedDateDesc(Long customerId);
    
    Optional<WishlistItem> findByCustomerIdAndProductId(Long customerId, Long productId);
    
    void deleteByCustomerIdAndProductId(Long customerId, Long productId);
    
    @Query("SELECT COUNT(w) FROM WishlistItem w WHERE w.customer.id = :customerId")
    Long countByCustomerId(@Param("customerId") Long customerId);
    
    boolean existsByCustomerIdAndProductId(Long customerId, Long productId);
}
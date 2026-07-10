package com.satpall.crochet.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.satpall.crochet.entity.Order;
import com.satpall.crochet.enums.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

	Order findByOrderNumber(String orderNumber);

	Page<Order> findByCustomerIdOrderByCreatedDateDesc(Long customerId, Pageable pageable);

	Page<Order> findByStatusOrderByCreatedDateDesc(OrderStatus status, Pageable pageable);

	Page<Order> findAllByOrderByCreatedDateDesc(Pageable pageable);

	@Query("SELECT COUNT(o) FROM Order o WHERE o.createdDate BETWEEN :startDate AND :endDate")
	Long countOrdersInDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

	@Query("SELECT SUM(o.total) FROM Order o WHERE o.createdDate BETWEEN :startDate AND :endDate AND o.status != 'CANCELLED'")
	Double sumRevenueInDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

	List<Order> findByStatus(OrderStatus status);

	long countByStatus(OrderStatus status);

	@Query("SELECT SUM(o.total) FROM Order o")
	BigDecimal getTotalRevenue();
}
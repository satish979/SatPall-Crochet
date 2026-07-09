package com.satpall.crochet.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.satpall.crochet.entity.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	Page<Product> findByActiveAndCategoryOrderByCreatedDateDesc(Boolean active, Long categoryId, Pageable pageable);

	Page<Product> findByActiveOrderByCreatedDateDesc(Boolean active, Pageable pageable);

	List<Product> findByFeaturedAndActiveOrderByCreatedDateDesc(Boolean featured, Boolean active);

	List<Product> findByNewArrivalAndActiveOrderByCreatedDateDesc(Boolean newArrival, Boolean active);

	List<Product> findByBestSellerAndActiveOrderByCreatedDateDesc(Boolean bestSeller, Boolean active);

	Page<Product> findByActiveAndNameContainingIgnoreCase(Boolean active, String keyword, Pageable pageable);

	@Query("SELECT p FROM Product p WHERE p.active = true AND p.category.id = :categoryId ORDER BY p.createdDate DESC")
	Page<Product> findByCategoryOrderByCreatedDateDesc(@Param("categoryId") Long categoryId, Pageable pageable);

	@Query("SELECT p FROM Product p WHERE p.active = true AND p.name LIKE %:keyword% OR p.description LIKE %:keyword%")
	Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);

	@Query("SELECT p FROM Product p WHERE p.active = true AND p.category.id = :categoryId AND p.id != :productId ORDER BY p.createdDate DESC")
	List<Product> findRelatedProductsByCategory(@Param("categoryId") Long categoryId,
			@Param("productId") Long productId, Pageable pageable);

	List<Product> findByBestSellerTrueAndActiveTrue();

}

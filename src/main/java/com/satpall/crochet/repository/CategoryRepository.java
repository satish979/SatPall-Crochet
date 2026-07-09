package com.satpall.crochet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.satpall.crochet.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

	List<Category> findByActiveOrderByDisplayOrderAscNameAsc(Boolean active);

	List<Category> findAllByOrderByDisplayOrderAscNameAsc();

	@Query("SELECT c FROM Category c WHERE c.active = true AND c.products IS NOT EMPTY ORDER BY c.displayOrder ASC")
	List<Category> findCategoriesWithProducts();

	Category findByName(String name);
}

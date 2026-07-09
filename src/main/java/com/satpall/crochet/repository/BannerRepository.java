package com.satpall.crochet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.satpall.crochet.entity.Banner;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {

	List<Banner> findByActiveOrderByDisplayOrderAsc(Boolean active);
}
package com.satpall.crochet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.satpall.crochet.entity.Customer;
import com.satpall.crochet.entity.CustomerAddress;

@Repository
public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Long> {

	List<CustomerAddress> findByCustomerId(Long customerId);

	Optional<CustomerAddress> findByCustomerIdAndIsDefaultTrue(Long customerId);

	void deleteByCustomerId(Long customerId);

}

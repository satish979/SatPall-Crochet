package com.satpall.crochet.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.satpall.crochet.entity.Customer;
import com.satpall.crochet.entity.CustomerAddress;
import com.satpall.crochet.exception.OrderException;
import com.satpall.crochet.repository.CustomerAddressRepository;
import com.satpall.crochet.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {

	private final CustomerRepository customerRepository;
	private final CustomerAddressRepository customerAddressRepository;

	public Optional<Customer> getCurrentCustomer(Long customerId) {
		if (customerId == null) {
			return Optional.empty();
		}
		return customerRepository.findById(customerId);
	}

	public Customer updateProfile(Long customerId, String firstName, String lastName, String phone) {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new OrderException("Customer not found"));

		if (firstName != null && !firstName.trim().isEmpty()) {
			customer.setFirstName(firstName.trim());
		}
		if (lastName != null && !lastName.trim().isEmpty()) {
			customer.setLastName(lastName.trim());
		}
		if (phone != null) {
			customer.setPhone(phone.trim());
		}

		return customerRepository.save(customer);
	}

	public List<CustomerAddress> getAddresses(Long customerId) {
		return customerAddressRepository.findByCustomerId(customerId);
	}

	public Optional<CustomerAddress> getDefaultAddress(Long customerId) {
		return customerAddressRepository.findByCustomerIdAndIsDefaultTrue(customerId);
	}

}

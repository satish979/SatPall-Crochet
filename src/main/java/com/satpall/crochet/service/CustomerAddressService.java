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
public class CustomerAddressService {

	private final CustomerAddressRepository customerAddressRepository;
	private final CustomerRepository customerRepository;

	public List<CustomerAddress> getAddresses(Long customerId) {
		return customerAddressRepository.findByCustomerId(customerId);
	}

	public Optional<CustomerAddress> getDefaultAddress(Long customerId) {
		return customerAddressRepository.findByCustomerIdAndIsDefaultTrue(customerId);
	}

	public CustomerAddress addAddress(Long customerId, CustomerAddress address) {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new OrderException("Customer not found"));

		address.setCustomer(customer);

		if (Boolean.TRUE.equals(address.getIsDefault())) {
			customerAddressRepository.findByCustomerId(customerId).forEach(addr -> {
				addr.setIsDefault(false);
				customerAddressRepository.save(addr);
			});
		}

		return customerAddressRepository.save(address);
	}

	public CustomerAddress updateAddress(Long customerId, Long addressId, CustomerAddress updated) {
		CustomerAddress existing = customerAddressRepository.findById(addressId)
				.orElseThrow(() -> new OrderException("Address not found"));

		if (!existing.getCustomer().getId().equals(customerId)) {
			throw new OrderException("Unauthorized access to address");
		}

		if (updated.getFullName() != null) {
			existing.setFullName(updated.getFullName());
		}
		if (updated.getMobile() != null) {
			existing.setMobile(updated.getMobile());
		}
		if (updated.getAddressLine1() != null) {
			existing.setAddressLine1(updated.getAddressLine1());
		}
		if (updated.getAddressLine2() != null) {
			existing.setAddressLine2(updated.getAddressLine2());
		}
		if (updated.getCity() != null) {
			existing.setCity(updated.getCity());
		}
		if (updated.getState() != null) {
			existing.setState(updated.getState());
		}
		if (updated.getPinCode() != null) {
			existing.setPinCode(updated.getPinCode());
		}
		if (updated.getLandmark() != null) {
			existing.setLandmark(updated.getLandmark());
		}
		if (updated.getType() != null) {
			existing.setType(updated.getType());
		}

		if (Boolean.TRUE.equals(updated.getIsDefault())) {
			customerAddressRepository.findByCustomerId(customerId).forEach(addr -> {
				if (!addr.getId().equals(addressId)) {
					addr.setIsDefault(false);
					customerAddressRepository.save(addr);
				}
			});
			existing.setIsDefault(true);
		}

		return customerAddressRepository.save(existing);
	}

	public void deleteAddress(Long customerId, Long addressId) {
		CustomerAddress existing = customerAddressRepository.findById(addressId)
				.orElseThrow(() -> new OrderException("Address not found"));

		if (!existing.getCustomer().getId().equals(customerId)) {
			throw new OrderException("Unauthorized access to address");
		}

		customerAddressRepository.delete(existing);
	}

	public CustomerAddress setDefaultAddress(Long customerId, Long addressId) {
		CustomerAddress address = customerAddressRepository.findById(addressId)
				.orElseThrow(() -> new OrderException("Address not found"));

		if (!address.getCustomer().getId().equals(customerId)) {
			throw new OrderException("Unauthorized access to address");
		}

		customerAddressRepository.findByCustomerId(customerId).forEach(addr -> {
			addr.setIsDefault(false);
			customerAddressRepository.save(addr);
		});

		address.setIsDefault(true);
		return customerAddressRepository.save(address);
	}

}

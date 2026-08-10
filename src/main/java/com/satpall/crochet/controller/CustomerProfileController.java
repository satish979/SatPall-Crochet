package com.satpall.crochet.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.satpall.crochet.dto.AddressRequest;
import com.satpall.crochet.dto.AddressResponse;
import com.satpall.crochet.entity.Customer;
import com.satpall.crochet.entity.CustomerAddress;
import com.satpall.crochet.exception.OrderException;
import com.satpall.crochet.service.CustomerAddressService;
import com.satpall.crochet.service.CustomerService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerProfileController {

	private final CustomerService customerService;
	private final CustomerAddressService customerAddressService;

	private Long getCustomerId(HttpSession session) {
		Long customerId = (Long) session.getAttribute("customerId");
		if (customerId == null) {
			throw new OrderException("Please login to continue");
		}
		return customerId;
	}

	@GetMapping("/profile")
	public String profile(HttpSession session, Model model) {
		try {
			Long customerId = getCustomerId(session);
			Customer customer = customerService.getCurrentCustomer(customerId).orElse(null);
			if (customer == null) {
				session.removeAttribute("customerId");
				return "redirect:/";
			}
			model.addAttribute("pageTitle", "My Profile");
			model.addAttribute("customer", customer);
			return "customer/profile";
		} catch (OrderException e) {
			return "redirect:/";
		}
	}

	@GetMapping("/addresses")
	public String addresses(HttpSession session, Model model) {
		try {
			Long customerId = getCustomerId(session);
			Customer customer = customerService.getCurrentCustomer(customerId).orElse(null);
			if (customer == null) {
				session.removeAttribute("customerId");
				return "redirect:/";
			}
			List<CustomerAddress> addresses = customerAddressService.getAddresses(customerId);
			model.addAttribute("pageTitle", "My Addresses");
			model.addAttribute("customer", customer);
			model.addAttribute("addresses", addresses);
			return "customer/addresses";
		} catch (OrderException e) {
			return "redirect:/";
		}
	}

	@PostMapping("/profile/update")
	public String updateProfile(HttpSession session, RedirectAttributes redirectAttributes,
			@RequestParam String firstName, @RequestParam String lastName, @RequestParam(required = false) String phone) {
		try {
			Long customerId = getCustomerId(session);
			customerService.updateProfile(customerId, firstName, lastName, phone);
			redirectAttributes.addFlashAttribute("success", "Profile updated successfully");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/customer/profile";
	}

	@PostMapping("/addresses/add")
	public String addAddress(HttpSession session, RedirectAttributes redirectAttributes,
			@Valid AddressRequest request) {
		try {
			Long customerId = getCustomerId(session);
			CustomerAddress address = new CustomerAddress();
			address.setFullName(request.getFullName());
			address.setMobile(request.getMobile());
			address.setAddressLine1(request.getAddressLine1());
			address.setAddressLine2(request.getAddressLine2());
			address.setCity(request.getCity());
			address.setState(request.getState());
			address.setPinCode(request.getPinCode());
			address.setLandmark(request.getLandmark());
			address.setType(request.getType() != null ? request.getType() : "HOME");
			address.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : false);

			customerAddressService.addAddress(customerId, address);
			redirectAttributes.addFlashAttribute("success", "Address added successfully");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/customer/addresses";
	}

	@PostMapping("/addresses/update/{id}")
	public String updateAddress(HttpSession session, RedirectAttributes redirectAttributes,
			@PathVariable Long id, @Valid AddressRequest request) {
		try {
			Long customerId = getCustomerId(session);
			CustomerAddress updated = new CustomerAddress();
			updated.setFullName(request.getFullName());
			updated.setMobile(request.getMobile());
			updated.setAddressLine1(request.getAddressLine1());
			updated.setAddressLine2(request.getAddressLine2());
			updated.setCity(request.getCity());
			updated.setState(request.getState());
			updated.setPinCode(request.getPinCode());
			updated.setLandmark(request.getLandmark());
			updated.setType(request.getType());
			updated.setIsDefault(request.getIsDefault());

			customerAddressService.updateAddress(customerId, id, updated);
			redirectAttributes.addFlashAttribute("success", "Address updated successfully");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/customer/addresses";
	}

	@PostMapping("/addresses/delete/{id}")
	public String deleteAddress(HttpSession session, RedirectAttributes redirectAttributes, @PathVariable Long id) {
		try {
			Long customerId = getCustomerId(session);
			customerAddressService.deleteAddress(customerId, id);
			redirectAttributes.addFlashAttribute("success", "Address deleted successfully");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/customer/addresses";
	}

	@PostMapping("/addresses/set-default/{id}")
	public String setDefaultAddress(HttpSession session, RedirectAttributes redirectAttributes, @PathVariable Long id) {
		try {
			Long customerId = getCustomerId(session);
			customerAddressService.setDefaultAddress(customerId, id);
			redirectAttributes.addFlashAttribute("success", "Default address updated");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/customer/addresses";
	}

}

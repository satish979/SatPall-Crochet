package com.satpall.crochet.controller;

import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.satpall.crochet.entity.Customer;
import com.satpall.crochet.service.CustomerService;

import lombok.RequiredArgsConstructor;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

	private final CustomerService customerService;

	@ModelAttribute
	public void addCustomerToModel(HttpSession session, ModelMap model) {
		if (session == null) {
			return;
		}

		Long customerId = (Long) session.getAttribute("customerId");
		if (customerId == null) {
			return;
		}

		Customer customer = (Customer) session.getAttribute("customer");
		if (customer == null) {
			Optional<Customer> customerOpt = customerService.getCurrentCustomer(customerId);
			if (customerOpt.isPresent()) {
				customer = customerOpt.get();
				session.setAttribute("customer", customer);
			} else {
				session.removeAttribute("customerId");
				session.removeAttribute("customerEmail");
				session.removeAttribute("customer");
				return;
			}
		}

		model.addAttribute("navCustomer", customer);
		String fullName = customer.getFirstName();
		if (fullName == null || fullName.isEmpty()) {
			fullName = customer.getEmail();
		} else if (customer.getLastName() != null && !customer.getLastName().isEmpty()) {
			fullName += " " + customer.getLastName();
		}
		model.addAttribute("navCustomerName", fullName);
	}

}


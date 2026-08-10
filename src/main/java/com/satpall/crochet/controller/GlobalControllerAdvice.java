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
		Long customerId = (Long) session.getAttribute("customerId");
		if (customerId != null) {
			Optional<Customer> customerOpt = customerService.getCurrentCustomer(customerId);
			if (customerOpt.isPresent()) {
				Customer customer = customerOpt.get();
				model.addAttribute("navCustomer", customer);
				String fullName = customer.getFirstName();
				if (customer.getLastName() != null && !customer.getLastName().isEmpty()) {
					fullName += " " + customer.getLastName();
				}
				model.addAttribute("navCustomerName", fullName);
			}
		}
	}

}

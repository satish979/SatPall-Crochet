package com.satpall.crochet.controller;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.satpall.crochet.entity.Customer;
import com.satpall.crochet.service.CustomerService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CustomerSessionFilter extends OncePerRequestFilter {

	private final CustomerService customerService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		if (session != null) {
			Long customerId = (Long) session.getAttribute("customerId");
			if (customerId != null && session.getAttribute("customer") == null) {
				Optional<Customer> customerOpt = customerService.getCurrentCustomer(customerId);
				if (customerOpt.isPresent()) {
					session.setAttribute("customer", customerOpt.get());
				} else {
					session.removeAttribute("customerId");
					session.removeAttribute("customerEmail");
					session.removeAttribute("customer");
				}
			}
		}

		filterChain.doFilter(request, response);
	}

}

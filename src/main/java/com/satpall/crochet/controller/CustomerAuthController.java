package com.satpall.crochet.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.satpall.crochet.dto.CustomerAuthResponse;
import com.satpall.crochet.dto.OtpSendRequest;
import com.satpall.crochet.dto.OtpVerifyRequest;
import com.satpall.crochet.entity.Customer;
import com.satpall.crochet.exception.PaymentException;
import com.satpall.crochet.service.CustomerService;
import com.satpall.crochet.service.OtpService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customer/auth")
@RequiredArgsConstructor
public class CustomerAuthController {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CustomerAuthController.class);

	private final OtpService otpService;
	private final CustomerService customerService;

	@PostMapping("/send-otp")
	public ResponseEntity<Map<String, Object>> sendOtp(@Valid @RequestBody OtpSendRequest request,
			HttpSession session) {
		Map<String, Object> response = new HashMap<>();

		if (request.getIdentifier() == null || request.getIdentifier().trim().isEmpty()) {
			response.put("success", false);
			response.put("message", "Email address is required.");
			return ResponseEntity.badRequest().body(response);
		}

		try {
			otpService.sendOtp(request.getIdentifier(), request.getType());
			response.put("success", true);
			response.put("message", "OTP sent successfully to " + request.getIdentifier().trim().toLowerCase());
			return ResponseEntity.ok(response);
		} catch (PaymentException ex) {
			log.warn("PaymentException sending OTP to {}: {}", request.getIdentifier(), ex.getMessage());
			response.put("success", false);
			response.put("message", ex.getMessage());
			return ResponseEntity.badRequest().body(response);
		} catch (Exception ex) {
			log.error("Failed to send OTP to {}: {}", request.getIdentifier(), ex.getMessage(), ex);
			response.put("success", false);
			response.put("message", "Failed to send OTP email: " + (ex.getMessage() != null ? ex.getMessage() : "Please verify your email or try again."));
			return ResponseEntity.status(500).body(response);
		}
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<Map<String, Object>> verifyOtp(@Valid @RequestBody OtpVerifyRequest request,
			HttpSession session) {
		Map<String, Object> response = new HashMap<>();

		if (request.getIdentifier() == null || request.getIdentifier().trim().isEmpty()) {
			response.put("success", false);
			response.put("message", "Email address is required.");
			return ResponseEntity.badRequest().body(response);
		}

		if (request.getOtp() == null || request.getOtp().trim().isEmpty()) {
			response.put("success", false);
			response.put("message", "Please enter the 6-digit OTP.");
			return ResponseEntity.badRequest().body(response);
		}

		try {
			boolean valid = otpService.verifyOtp(request.getIdentifier(), request.getOtp().trim());
			if (!valid) {
				response.put("success", false);
				response.put("message", "Invalid or expired OTP. Please check and try again.");
				return ResponseEntity.badRequest().body(response);
			}

			Customer customer = otpService.loginOrCreateCustomer(request.getIdentifier());
			session.setAttribute("customerId", customer.getId());
			session.setAttribute("customerEmail", customer.getEmail());
			session.setAttribute("customer", customer);

			String redirectAfterLogin = (String) session.getAttribute("redirectAfterLogin");
			if (redirectAfterLogin != null) {
				session.removeAttribute("redirectAfterLogin");
				response.put("redirectAfterLogin", redirectAfterLogin);
			}

			CustomerAuthResponse authResponse = new CustomerAuthResponse();
			authResponse.setId(customer.getId());
			authResponse.setFirstName(customer.getFirstName());
			authResponse.setLastName(customer.getLastName());
			authResponse.setEmail(customer.getEmail());
			authResponse.setPhone(customer.getPhone());
			authResponse.setMessage("Login successful");

			response.put("success", true);
			response.put("customer", authResponse);
			return ResponseEntity.ok(response);
		} catch (Exception ex) {
			log.error("Verification error for {}: {}", request.getIdentifier(), ex.getMessage(), ex);
			response.put("success", false);
			response.put("message", "Verification failed. Please try again.");
			return ResponseEntity.status(500).body(response);
		}
	}

	@PostMapping("/logout")
	public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
		Map<String, Object> response = new HashMap<>();
		session.invalidate();
		response.put("success", true);
		response.put("message", "Logged out successfully");
		return ResponseEntity.ok(response);
	}

	@GetMapping("/me")
	public ResponseEntity<Map<String, Object>> me(HttpSession session) {
		Map<String, Object> response = new HashMap<>();
		Long customerId = (Long) session.getAttribute("customerId");
		if (customerId == null) {
			response.put("success", false);
			response.put("message", "Not logged in");
			return ResponseEntity.ok(response);
		}

		Customer customer = (Customer) session.getAttribute("customer");
		if (customer == null) {
			customer = customerService.getCurrentCustomer(customerId).orElse(null);
			if (customer == null) {
				session.removeAttribute("customerId");
				session.removeAttribute("customerEmail");
				session.removeAttribute("customer");
				response.put("success", false);
				response.put("message", "Customer not found");
				return ResponseEntity.ok(response);
			}
			session.setAttribute("customer", customer);
		}

		CustomerAuthResponse authResponse = new CustomerAuthResponse();
		authResponse.setId(customer.getId());
		authResponse.setFirstName(customer.getFirstName());
		authResponse.setLastName(customer.getLastName());
		authResponse.setEmail(customer.getEmail());
		authResponse.setPhone(customer.getPhone());

		response.put("success", true);
		response.put("customer", authResponse);
		return ResponseEntity.ok(response);
	}

}

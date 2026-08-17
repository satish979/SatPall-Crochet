package com.satpall.crochet.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.satpall.crochet.entity.Customer;
import com.satpall.crochet.entity.OtpVerification;
import com.satpall.crochet.exception.PaymentException;
import com.satpall.crochet.repository.CustomerRepository;
import com.satpall.crochet.repository.OtpVerificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpService {

	private static final Logger log = LoggerFactory.getLogger(OtpService.class);

	private final OtpVerificationRepository otpVerificationRepository;
	private final CustomerRepository customerRepository;
	private final EmailService emailService;

	@Transactional
	public void sendOtp(String identifier, String type) {

		if (identifier == null || identifier.trim().isEmpty()) {
			throw new PaymentException("Email is required");
		}

		String normalized = identifier.trim().toLowerCase();

		if (!normalized.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
			throw new PaymentException("Please enter a valid email address");
		}

		String otp = String.format("%06d", new Random().nextInt(1_000_000));
		LocalDateTime expiry = LocalDateTime.now().plusMinutes(5);

		log.info("Generating OTP for email: {} with expiry: {}", normalized, expiry);

		OtpVerification verification = otpVerificationRepository.findByIdentifier(normalized)
				.orElseGet(OtpVerification::new);

		verification.setIdentifier(normalized);
		verification.setOtp(otp);
		verification.setExpiryDate(expiry);
		verification.setVerified(false);
		verification.setAttempts(0);
		verification.setMaxAttempts(3);

		otpVerificationRepository.save(verification);

		String subject = "Your Loomelle Crochet verification code";

		String body = "<p>Dear Customer,</p>" + "<p>Your verification code is <strong>" + otp + "</strong>.</p>"
				+ "<p>This code will expire in 5 minutes.</p>"
				+ "<p>If you did not request this, please ignore this email.</p>" + "<p>Team Loomelle Crochet</p>";

		log.info("Attempting to send OTP email to: {} via SMTP", normalized);
		emailService.sendHtmlEmail(normalized, subject, body);
		log.info("OTP email send triggered for: {}", normalized);
	}

	public boolean verifyOtp(String identifier, String otp) {
		if (identifier == null || identifier.trim().isEmpty() || otp == null || otp.trim().isEmpty()) {
			return false;
		}

		String normalized = identifier.trim().toLowerCase();

		Optional<OtpVerification> opt = otpVerificationRepository.findByIdentifier(normalized);
		if (opt.isEmpty()) {
			return false;
		}

		OtpVerification verification = opt.get();

		if (verification.getVerified()) {
			return true;
		}

		if (verification.getExpiryDate().isBefore(LocalDateTime.now())) {
			otpVerificationRepository.delete(verification);
			return false;
		}

		if (verification.getAttempts() >= verification.getMaxAttempts()) {
			otpVerificationRepository.delete(verification);
			return false;
		}

		verification.setAttempts(verification.getAttempts() + 1);

		if (verification.getOtp().equals(otp)) {
			verification.setVerified(true);
			otpVerificationRepository.save(verification);
			return true;
		}

		otpVerificationRepository.save(verification);
		return false;
	}

	public Customer loginOrCreateCustomer(String identifier) {
		String normalized = identifier.trim().toLowerCase();
		Customer customer = customerRepository.findByEmail(normalized).orElse(null);

		if (customer == null) {
			customer = new Customer();
			customer.setEmail(normalized);
			customer.setEnabled(true);
			customerRepository.save(customer);
		}

		customer.setLastLogin(LocalDateTime.now());
		customerRepository.save(customer);
		return customer;
	}

}

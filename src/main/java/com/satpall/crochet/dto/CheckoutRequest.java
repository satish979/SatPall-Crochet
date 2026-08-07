package com.satpall.crochet.dto;

import java.math.BigDecimal;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class CheckoutRequest {

	@NotBlank(message = "Customer name is required")
	@Size(max = 100, message = "Name must be less than 100 characters")
	private String customerName;

	@NotBlank(message = "Email is required")
	@Email(message = "Email must be valid")
	private String email;

	@NotBlank(message = "Phone number is required")
	@Pattern(regexp = "^[0-9]{10}$", message = "Phone must be a valid 10-digit number")
	private String phone;

	@NotBlank(message = "Address Line 1 is required")
	@Size(max = 255, message = "Address Line 1 must be less than 255 characters")
	private String addressLine1;

	@Size(max = 255, message = "Address Line 2 must be less than 255 characters")
	private String addressLine2;

	@NotBlank(message = "City is required")
	@Size(max = 50, message = "City must be less than 50 characters")
	private String city;

	@NotBlank(message = "State is required")
	@Size(max = 50, message = "State must be less than 50 characters")
	private String state;

	@NotBlank(message = "Country is required")
	@Size(max = 100, message = "Country must be less than 100 characters")
	private String country;

	@NotBlank(message = "Pincode is required")
	@Pattern(regexp = "^[0-9]{6}$", message = "Pincode must be a valid 6-digit number")
	private String pinCode;

	@NotBlank(message = "Payment method is required")
	private String paymentMethod;

	@Size(max = 500, message = "Notes must be less than 500 characters")
	private String notes;

}
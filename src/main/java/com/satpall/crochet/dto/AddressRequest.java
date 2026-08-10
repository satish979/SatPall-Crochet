package com.satpall.crochet.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class AddressRequest {

	@NotBlank(message = "Full name is required")
	@Size(max = 100, message = "Full name must be less than 100 characters")
	private String fullName;

	@NotBlank(message = "Mobile number is required")
	@Size(max = 15, message = "Mobile must be less than 15 characters")
	private String mobile;

	@NotBlank(message = "Address line 1 is required")
	@Size(max = 255, message = "Address line 1 must be less than 255 characters")
	private String addressLine1;

	@Size(max = 255, message = "Address line 2 must be less than 255 characters")
	private String addressLine2;

	@NotBlank(message = "City is required")
	@Size(max = 50, message = "City must be less than 50 characters")
	private String city;

	@NotBlank(message = "State is required")
	@Size(max = 50, message = "State must be less than 50 characters")
	private String state;

	@NotBlank(message = "Pincode is required")
	@Size(max = 10, message = "Pincode must be less than 10 characters")
	private String pinCode;

	@Size(max = 255, message = "Landmark must be less than 255 characters")
	private String landmark;

	private String type = "HOME";

	private Boolean isDefault = false;

}

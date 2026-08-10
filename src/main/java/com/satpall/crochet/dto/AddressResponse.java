package com.satpall.crochet.dto;

import lombok.Data;

@Data
public class AddressResponse {

	private Long id;
	private String fullName;
	private String mobile;
	private String addressLine1;
	private String addressLine2;
	private String city;
	private String state;
	private String pinCode;
	private String landmark;
	private String type;
	private Boolean isDefault;

}

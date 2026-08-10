package com.satpall.crochet.dto;

import lombok.Data;

@Data
public class CustomerAuthResponse {

	private Long id;
	private String firstName;
	private String lastName;
	private String email;
	private String phone;
	private String message;

}

package com.satpall.crochet.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.web.bind.MethodArgumentNotValidException;

@ControllerAdvice
public class CustomerWebExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public String handleValidationException(MethodArgumentNotValidException ex, RedirectAttributes redirectAttributes, HttpServletRequest request) {
		String referer = request.getHeader("Referer");
		String target = "/customer/addresses";
		if (referer != null && referer.contains("/customer/")) {
			target = referer;
		}
		String message = ex.getBindingResult().getFieldErrors().stream()
				.findFirst()
				.map(error -> error.getField() + " " + error.getDefaultMessage())
				.orElse("Validation failed");
		redirectAttributes.addFlashAttribute("error", message);
		return "redirect:" + target;
	}

}

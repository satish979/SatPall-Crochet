package com.satpall.crochet.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public String handleAllExceptions(Exception ex, Model model) {
		model.addAttribute("message", ex.getMessage());
		return "500";
	}
}

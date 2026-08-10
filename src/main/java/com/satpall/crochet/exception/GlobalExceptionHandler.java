package com.satpall.crochet.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(assignableTypes = {
	com.satpall.crochet.controller.HomeController.class,
	com.satpall.crochet.controller.OrderController.class,
	com.satpall.crochet.controller.AdminController.class,
	com.satpall.crochet.controller.CategoryAdminController.class
})
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public String handleAllExceptions(Exception ex, Model model) {
		model.addAttribute("message", ex.getMessage());
		return "500";
	}
}

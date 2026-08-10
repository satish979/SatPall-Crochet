package com.satpall.crochet.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BindException;

@RestControllerAdvice(assignableTypes = {
	com.satpall.crochet.controller.OrderApiController.class,
	com.satpall.crochet.controller.PaymentController.class,
	com.satpall.crochet.controller.CartController.class
})
public class RestExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
		String message = ex.getBindingResult().getFieldErrors().stream()
				.findFirst()
				.map(error -> error.getField() + " " + error.getDefaultMessage())
				.orElse("Validation failed");
		return ResponseEntity.badRequest().body(Map.of("success", false, "message", message));
	}

	@ExceptionHandler(BindException.class)
	public ResponseEntity<Map<String, Object>> handleBindException(BindException ex) {
		String message = ex.getBindingResult().getFieldErrors().stream()
				.findFirst()
				.map(error -> error.getField() + " " + error.getDefaultMessage())
				.orElse("Validation failed");
		return ResponseEntity.badRequest().body(Map.of("success", false, "message", message));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(Map.of("success", false, "message", ex.getMessage()));
	}
}
package com.satpall.crochet.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.satpall.crochet.entity.Order;

import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

	private final JavaMailSender mailSender;

	@Value("${app.mail.from}")
	private String fromEmail;

	@Override
	public void sendHtmlEmail(String to, String subject, String htmlBody) {
		try {
			MimeMessage message = mailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setFrom(fromEmail);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(htmlBody, true);

			mailSender.send(message);

			System.out.println("Email sent successfully to: " + to);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	@Async
	public void sendOrderConfirmation(Order order) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(fromEmail);
			message.setTo(order.getEmail());
			message.setSubject("Order Confirmed - " + order.getOrderNumber());
			message.setText(buildOrderConfirmationText(order));
			mailSender.send(message);
		} catch (Exception e) {
			// Log but don't fail the order
			System.err.println("Failed to send order confirmation email: " + e.getMessage());
		}
	}

	@Override
	@Async
	public void sendPaymentSuccessEmail(Order order) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(fromEmail);
			message.setTo(order.getEmail());
			message.setSubject("Payment Successful - " + order.getOrderNumber());
			message.setText(buildPaymentSuccessText(order));
			mailSender.send(message);
		} catch (Exception e) {
			System.err.println("Failed to send payment success email: " + e.getMessage());
		}
	}

	private String buildOrderConfirmationText(Order order) {
		StringBuilder sb = new StringBuilder();
		sb.append("Dear ").append(order.getCustomerName()).append(",\n\n");
		sb.append("Thank you for your order!\n\n");
		sb.append("Order Number: ").append(order.getOrderNumber()).append("\n");
		sb.append("Order Date: ").append(order.getCreatedDate()).append("\n");
		sb.append("Payment Method: ").append(order.getPaymentMethod()).append("\n");
		sb.append("Payment Status: ").append(order.getPaymentStatus()).append("\n");
		sb.append("Order Status: ").append(order.getStatus()).append("\n\n");
		sb.append("Shipping Address:\n");
		sb.append(order.getShippingAddress()).append("\n\n");
		sb.append("Order Total: ₹").append(order.getTotal()).append("\n\n");
		sb.append("Thank you for shopping with Loomelle Crochet!\n");
		return sb.toString();
	}

	private String buildPaymentSuccessText(Order order) {
		StringBuilder sb = new StringBuilder();
		sb.append("Dear ").append(order.getCustomerName()).append(",\n\n");
		sb.append("Your payment has been received successfully!\n\n");
		sb.append("Order Number: ").append(order.getOrderNumber()).append("\n");
		sb.append("Payment ID: ").append(order.getRazorpayPaymentId()).append("\n");
		sb.append("Amount Paid: ₹").append(order.getTotal()).append("\n\n");
		sb.append("Your order will be processed shortly.\n\n");
		sb.append("Thank you for shopping with Loomelle Crochet!\n");
		return sb.toString();
	}

}
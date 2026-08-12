package com.satpall.crochet.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.satpall.crochet.entity.Order;
import com.satpall.crochet.entity.OrderItem;
import com.satpall.crochet.enums.OrderStatus;

import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

	private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

	private final JavaMailSender mailSender;

	@Value("${app.mail.from}")
	private String fromEmail;

	@Value("${admin.mail.id}")
	private String adminEmail;

	@Value("${server.servlet.context-path:/Loomellecrochet}")
	private String contextPath;

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

			log.info("Email sent successfully to: {}", to);

		} catch (Exception e) {
			log.error("Failed to send email to: {}", to, e);
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

	@Override
	@Async
	public void sendNewOrderToAdmin(Order order, java.util.List<OrderItem> items) {
		try {
			String subject = "New Order Received - " + order.getOrderNumber();
			String html = buildNewOrderAdminHtml(order, items);
			sendHtmlEmail(adminEmail, subject, html);
		} catch (Exception e) {
			log.error("Failed to send new order email to admin", e);
		}
	}

	private String buildNewOrderAdminHtml(Order order, java.util.List<OrderItem> items) {
		StringBuilder itemsHtml = new StringBuilder();
		if (items != null) {
			for (OrderItem item : items) {
				itemsHtml.append("<tr>")
						.append("<td>").append(item.getProductName() != null ? item.getProductName() : "").append("</td>")
						.append("<td>").append(item.getQuantity()).append("</td>")
						.append("<td>₹").append(item.getPrice()).append("</td>")
						.append("<td>₹").append(item.getTotal()).append("</td>")
						.append("</tr>");
			}
		}

		return "<html><body style=\"font-family: Arial, sans-serif; color: #333;\">"
				+ "<h2>New Order Received</h2>"
				+ "<p><strong>Order Number:</strong> " + order.getOrderNumber() + "</p>"
				+ "<p><strong>Order Date:</strong> " + order.getCreatedDate() + "</p>"
				+ "<p><strong>Payment Method:</strong> " + order.getPaymentMethod() + "</p>"
				+ "<p><strong>Payment Status:</strong> " + order.getPaymentStatus() + "</p>"
				+ "<p><strong>Order Status:</strong> " + order.getStatus() + "</p>"
				+ "<h3>Customer Details</h3>"
				+ "<p><strong>Name:</strong> " + order.getCustomerName() + "</p>"
				+ "<p><strong>Email:</strong> " + order.getEmail() + "</p>"
				+ "<p><strong>Phone:</strong> " + order.getPhone() + "</p>"
				+ "<h3>Shipping Address</h3>"
				+ "<p>" + order.getShippingAddress() + "</p>"
				+ "<h3>Order Items</h3>"
				+ "<table border=\"1\" cellpadding=\"8\" cellspacing=\"0\" style=\"border-collapse: collapse;\">"
				+ "<thead><tr><th>Product</th><th>Qty</th><th>Price</th><th>Total</th></tr></thead>"
				+ "<tbody>" + itemsHtml + "</tbody>"
				+ "</table>"
				+ "<h3>Order Total: ₹" + order.getTotal() + "</h3>"
				+ "<p>Please process this order promptly.</p>"
				+ "<p>Thank you,<br/>Loomelle Crochet</p>"
				+ "</body></html>";
	}

	@Override
	@Async
	public void sendOrderStatusUpdateToCustomer(Order order, OrderStatus newStatus) {
		try {
			String subject = "Order Status Updated - " + order.getOrderNumber();
			String html = buildStatusUpdateHtml(order, newStatus);
			sendHtmlEmail(order.getEmail(), subject, html);
		} catch (Exception e) {
			log.error("Failed to send order status update email to customer", e);
		}
	}

	private String buildStatusUpdateHtml(Order order, OrderStatus newStatus) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html><body style=\"font-family: Arial, sans-serif; color: #333;\">");
		sb.append("<h2>Order Status Updated</h2>");
		sb.append("<p>Dear ").append(order.getCustomerName()).append(",</p>");
		sb.append("<p>The status of your order <strong>").append(order.getOrderNumber()).append("</strong> has been updated.</p>");
		sb.append("<p><strong>New Status:</strong> ").append(newStatus).append("</p>");
		sb.append("<p><strong>Payment Method:</strong> ").append(order.getPaymentMethod()).append("</p>");
		sb.append("<p><strong>Shipping Address:</strong><br/>").append(order.getShippingAddress()).append("</p>");
		sb.append("<p><strong>Order Total:</strong> ₹").append(order.getTotal()).append("</p>");

		if (newStatus == OrderStatus.DELIVERED) {
			sb.append("<h3>Share Your Feedback</h3>");
			sb.append("<p>We hope you love your order! Please share your feedback with us.</p>");
			sb.append("<p><a href=\"").append(contextPath).append("/customer/feedback/").append(order.getOrderNumber()).append("\" style=\"background:#201a17;color:#fff;padding:10px 20px;text-decoration:none;border-radius:6px;\">Share Feedback</a></p>");
		}

		sb.append("<p>Thank you for shopping with Loomelle Crochet!</p>");
		sb.append("</body></html>");
		return sb.toString();
	}

}
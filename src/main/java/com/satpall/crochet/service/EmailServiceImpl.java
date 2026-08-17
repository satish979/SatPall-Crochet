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

	@Value("${app.base-url}")
	private String baseUrl;

	@Value("${server.servlet.context-path:/Loomellecrochet}")
	private String contextPath;

	@Override
	@Async
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
			throw new RuntimeException(e);
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
			sb.append("<p><a href=\"").append(baseUrl).append(contextPath).append("/customer/feedback/").append(order.getOrderNumber()).append("\" style=\"background:#201a17;color:#fff;padding:10px 20px;text-decoration:none;border-radius:6px;\">Share Feedback</a></p>");
		}

		sb.append("<p>Thank you for shopping with Loomelle Crochet!</p>");
		sb.append("</body></html>");
		return sb.toString();
	}

	@Override
	@Async
	public void sendContactUsEmail(String name, String email, String inquiryType, String message) {
		try {
			// 1. Send detailed inquiry email to Admin / Support team
			String adminSubject = "[Loomelle Crochet Support] New Contact Inquiry: " + (inquiryType != null && !inquiryType.trim().isEmpty() ? inquiryType : "General Inquiry");
			String adminHtml = buildContactAdminHtml(name, email, inquiryType, message);

			MimeMessage adminMsg = mailSender.createMimeMessage();
			MimeMessageHelper adminHelper = new MimeMessageHelper(adminMsg, true, "UTF-8");
			adminHelper.setFrom(fromEmail, "Loomelle Crochet Contact");
			adminHelper.setTo(adminEmail);
			if (email != null && !email.trim().isEmpty() && email.contains("@")) {
				try {
					adminHelper.setReplyTo(email, name != null ? name : "Customer");
				} catch (Exception e) {
					adminHelper.setReplyTo(email);
				}
			}
			adminHelper.setSubject(adminSubject);
			adminHelper.setText(adminHtml, true);
			mailSender.send(adminMsg);
			log.info("Contact inquiry email successfully sent to admin ({}) from customer: {}", adminEmail, email);

			// 2. Send acknowledgment email to customer if email is valid
			if (email != null && email.contains("@")) {
				try {
					String customerSubject = "We have received your message! - Loomelle Crochet";
					String customerHtml = buildContactCustomerAckHtml(name, inquiryType, message);

					MimeMessage custMsg = mailSender.createMimeMessage();
					MimeMessageHelper custHelper = new MimeMessageHelper(custMsg, true, "UTF-8");
					custHelper.setFrom(fromEmail, "Loomelle Crochet");
					custHelper.setTo(email);
					custHelper.setSubject(customerSubject);
					custHelper.setText(customerHtml, true);
					mailSender.send(custMsg);
					log.info("Contact inquiry acknowledgment sent to customer: {}", email);
				} catch (Exception ce) {
					log.warn("Could not send acknowledgment to customer: {}", email, ce);
				}
			}
		} catch (Exception e) {
			log.error("Failed to process and send contact us email from: {}", email, e);
		}
	}

	private String buildContactAdminHtml(String name, String email, String inquiryType, String message) {
		return "<!DOCTYPE html><html><body style=\"font-family: 'Helvetica Neue', Arial, sans-serif; color: #2C2320; background-color: #FAF6F2; padding: 24px;\">"
				+ "<div style=\"max-width: 600px; margin: 0 auto; background: #FFFFFF; border-radius: 12px; padding: 28px; border: 1px solid #EDE4DC;\">"
				+ "<div style=\"border-bottom: 2px solid #C27D86; padding-bottom: 12px; margin-bottom: 20px;\">"
				+ "<h2 style=\"color: #8E4C55; margin: 0; font-size: 22px;\">✨ Loomelle Crochet Support</h2>"
				+ "<p style=\"color: #786C66; margin: 4px 0 0; font-size: 14px;\">New Contact Form Submission</p>"
				+ "</div>"
				+ "<table style=\"width: 100%; border-collapse: collapse; margin-bottom: 20px;\">"
				+ "<tr><td style=\"padding: 8px 0; color: #786C66; width: 140px; font-weight: bold;\">Customer Name:</td><td style=\"padding: 8px 0; color: #2C2320;\">" + (name != null ? name : "Anonymous") + "</td></tr>"
				+ "<tr><td style=\"padding: 8px 0; color: #786C66; font-weight: bold;\">Email Address:</td><td style=\"padding: 8px 0;\"><a href=\"mailto:" + (email != null ? email : "") + "\" style=\"color: #C27D86; text-decoration: none;\">" + (email != null ? email : "N/A") + "</a></td></tr>"
				+ "<tr><td style=\"padding: 8px 0; color: #786C66; font-weight: bold;\">Inquiry Topic:</td><td style=\"padding: 8px 0; color: #2C2320;\"><span style=\"background: #FBF0F1; color: #8E4C55; padding: 4px 10px; border-radius: 20px; font-size: 13px; font-weight: bold;\">" + (inquiryType != null ? inquiryType : "General") + "</span></td></tr>"
				+ "</table>"
				+ "<div style=\"background: #FAF6F2; border-left: 4px solid #C27D86; padding: 16px; border-radius: 6px; margin-bottom: 24px;\">"
				+ "<h4 style=\"margin: 0 0 8px; color: #2C2320; font-size: 14px;\">Message Content:</h4>"
				+ "<p style=\"margin: 0; color: #4A3E39; font-size: 14px; line-height: 1.6; white-space: pre-wrap;\">" + (message != null ? message : "") + "</p>"
				+ "</div>"
				+ "<p style=\"font-size: 13px; color: #A59891; margin: 0; border-top: 1px solid #EDE4DC; padding-top: 12px;\">You can directly reply to this email to get in touch with " + (name != null ? name : "the customer") + ".</p>"
				+ "</div>"
				+ "</body></html>";
	}

	private String buildContactCustomerAckHtml(String name, String inquiryType, String message) {
		return "<!DOCTYPE html><html><body style=\"font-family: 'Helvetica Neue', Arial, sans-serif; color: #2C2320; background-color: #FAF6F2; padding: 24px;\">"
				+ "<div style=\"max-width: 600px; margin: 0 auto; background: #FFFFFF; border-radius: 12px; padding: 28px; border: 1px solid #EDE4DC;\">"
				+ "<div style=\"text-align: center; border-bottom: 1px solid #EDE4DC; padding-bottom: 16px; margin-bottom: 20px;\">"
				+ "<h2 style=\"color: #8E4C55; margin: 0; font-size: 22px;\">Loomelle Crochet</h2>"
				+ "<p style=\"color: #786C66; margin: 4px 0 0; font-size: 13px;\">Artisanal Handcrafted Creations</p>"
				+ "</div>"
				+ "<p style=\"font-size: 15px; color: #2C2320;\">Dear <strong>" + (name != null ? name : "Valued Customer") + "</strong>,</p>"
				+ "<p style=\"font-size: 14px; color: #4A3E39; line-height: 1.6;\">Thank you for reaching out to <strong>Loomelle Crochet</strong>! We have received your inquiry regarding <em>" + (inquiryType != null ? inquiryType : "your message") + "</em>.</p>"
				+ "<p style=\"font-size: 14px; color: #4A3E39; line-height: 1.6;\">Our artisan support team is reviewing your details and will get back to you within <strong>24 business hours</strong>.</p>"
				+ "<div style=\"background: #FAF6F2; border-radius: 8px; padding: 14px; margin: 20px 0;\">"
				+ "<strong style=\"font-size: 13px; color: #786C66; display: block; margin-bottom: 6px;\">Your Message Summary:</strong>"
				+ "<p style=\"margin: 0; font-size: 13px; color: #4A3E39; line-height: 1.5; white-space: pre-wrap;\">" + (message != null ? message : "") + "</p>"
				+ "</div>"
				+ "<p style=\"font-size: 14px; color: #4A3E39;\">Warm regards,<br/><strong>The Loomelle Crochet Studio Team</strong></p>"
				+ "</div>"
				+ "</body></html>";
	}

}
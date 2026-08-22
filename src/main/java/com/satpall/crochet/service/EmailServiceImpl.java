package com.satpall.crochet.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.satpall.crochet.entity.Order;
import com.satpall.crochet.entity.OrderItem;
import com.satpall.crochet.enums.OrderStatus;

import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

import static com.satpall.crochet.service.LoomelleEmailTemplates.*;

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
	public void sendOtpEmail(String to, String otp) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setFrom(fromEmail, "Loomelle Crochet");
			helper.setTo(to);
			helper.setSubject("Your Loomelle Crochet OTP | Secure Login");

			String htmlBody = buildOtpHtml(otp);
			helper.setText(htmlBody, true);

			mailSender.send(message);
			log.info("OTP verification email successfully sent to: {}", to);
		} catch (Exception e) {
			log.error("Failed to send OTP verification email to: {}", to, e);
			throw new RuntimeException("Could not send verification email (" + e.getMessage() + "). Please try again.", e);
		}
	}

	private String buildOtpHtml(String otp) {
		return "<!DOCTYPE html>"
				+ "<html><head><meta charset=\"UTF-8\"><title>Verification Code</title></head>"
				+ "<body style=\"margin:0;padding:0;background-color:#FAF6F2;font-family:'Segoe UI',Roboto,Helvetica,Arial,sans-serif;color:#2C2320;\">"
				+ "<table role=\"presentation\" width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"background-color:#FAF6F2;padding:30px 15px;\">"
				+ "<tr><td align=\"center\">"
				+ "<table role=\"presentation\" width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"max-width:540px;background:#ffffff;border-radius:16px;box-shadow:0 4px 20px rgba(142,76,85,0.08);border:1px solid #EDE4DC;overflow:hidden;\">"
				+ "<tr><td style=\"background:linear-gradient(135deg, #8E4C55 0%, #C27D86 100%);padding:28px 24px;text-align:center;\">"
				+ "<h1 style=\"margin:0;color:#FFFFFF;font-size:24px;letter-spacing:1px;font-weight:700;\">Loomelle Crochet</h1>"
				+ "<p style=\"margin:6px 0 0;color:rgba(255,255,255,0.85);font-size:13px;letter-spacing:0.5px;\">Artisanal Handcrafted Studio</p>"
				+ "</td></tr>"
				+ "<tr><td style=\"padding:32px 28px;\">"
				+ "<h2 style=\"margin:0 0 12px;color:#2C2320;font-size:20px;font-weight:600;\">Your One-Time Passcode (OTP)</h2>"
				+ "<p style=\"margin:0 0 24px;color:#786C66;font-size:14px;line-height:1.6;\">"
				+ "Use the 6-digit verification code below to complete your authentication at Loomelle Crochet:"
				+ "</p>"
				+ "<div style=\"background:#FAF6F2;border:2px dashed #C27D86;border-radius:12px;padding:20px;text-align:center;margin:0 0 24px;\">"
				+ "<span style=\"font-size:34px;letter-spacing:8px;font-weight:700;color:#8E4C55;font-family:monospace;\">" + otp + "</span>"
				+ "<div style=\"margin-top:8px;font-size:12px;color:#A59891;\">Valid for <strong>5 minutes</strong>. Do not share this code.</div>"
				+ "</div>"
				+ "<p style=\"margin:0 0 12px;color:#786C66;font-size:13px;line-height:1.5;\">"
				+ "If you did not request this verification code, please disregard this email."
				+ "</p>"
				+ "</td></tr>"
				+ "<tr><td style=\"background:#FAF6F2;border-top:1px solid #EDE4DC;padding:16px 24px;text-align:center;\">"
				+ "<p style=\"margin:0;font-size:12px;color:#A59891;\">Need help? Email us at <a href=\"mailto:loomellecrochet.support@gmail.com\" style=\"color:#8E4C55;text-decoration:none;\">loomellecrochet.support@gmail.com</a></p>"
				+ "<p style=\"margin:4px 0 0;font-size:11px;color:#C27D86;\">&copy; Loomelle Crochet. All rights reserved.</p>"
				+ "</td></tr>"
				+ "</table>"
				+ "</td></tr>"
				+ "</table>"
				+ "</body></html>";
	}

	@Override
	@Async
	public void sendHtmlEmail(String to, String subject, String htmlBody) {
		try {
			MimeMessage message = mailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setFrom(fromEmail, "Loomelle Crochet");
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
			String subject = "Your Loomelle Crochet Order is Confirmed | #" + order.getOrderNumber();
			String html = buildOrderConfirmationHtml(order);
			sendHtmlEmail(order.getEmail(), subject, html);
		} catch (Exception e) {
			// Log but don't fail the order
			log.error("Failed to send order confirmation email to {}: {}", order.getEmail(), e.getMessage(), e);
		}
	}

	@Override
	@Async
	public void sendPaymentSuccessEmail(Order order) {
		try {
			String subject = "Your Loomelle Crochet Payment Was Successful | #" + order.getOrderNumber();
			String html = buildPaymentSuccessHtml(order);
			sendHtmlEmail(order.getEmail(), subject, html);
		} catch (Exception e) {
			log.error("Failed to send payment success email to {}: {}", order.getEmail(), e.getMessage(), e);
		}
	}

	private String buildOrderConfirmationHtml(Order order) {
		StringBuilder rows = new StringBuilder();
		rows.append(kvRow("Order Number", "#" + order.getOrderNumber()));
		rows.append(kvRow("Order Date", fmt(order.getCreatedDate())));
		rows.append(kvRow("Payment Method", order.getPaymentMethod()));
		rows.append(kvRowHtml("Payment Status", badge(String.valueOf(order.getPaymentStatus()), C_SUCCESS, C_SOFT)));
		rows.append(kvRowHtml("Order Status", badge(String.valueOf(order.getStatus()), C_WINE, C_SOFT)));
		if (order.getCouponCode() != null && !order.getCouponCode().trim().isEmpty()) {
			rows.append(kvRow("Coupon Applied", order.getCouponCode()));
		}

		StringBuilder sb = new StringBuilder();
		sb.append(greeting(order.getCustomerName()));
		sb.append(p("Thank you for your order! We are delighted to handcraft your pieces. Here are your order details:"));
		sb.append(section("Order Summary", kvTable(rows.toString())));
		sb.append(section("Shipping Address", noteBox(esc(order.getShippingAddress()), C_ROSE)));
		sb.append(section("Amount Payable", "<div style=\"text-align:right;\">"
				+ "<span style=\"font-size:13px;color:" + C_MUTED + ";\">Order Total</span><br>"
				+ "<span style=\"font-size:26px;font-weight:700;color:" + C_WINE + ";\">" + money(order.getTotal()) + "</span></div>"));
		sb.append(button(baseUrl + contextPath + "/my-orders", "View My Orders"));
		return wrap("Order Confirmation", sb.toString());
	}

	private String buildPaymentSuccessHtml(Order order) {
		StringBuilder rows = new StringBuilder();
		rows.append(kvRow("Order Number", "#" + order.getOrderNumber()));
		rows.append(kvRow("Payment ID", order.getRazorpayPaymentId() != null ? order.getRazorpayPaymentId() : "-"));
		rows.append(kvRow("Transaction Date", fmt(order.getTransactionDate())));

		StringBuilder sb = new StringBuilder();
		sb.append(greeting(order.getCustomerName()));
		sb.append(p("Great news! Your payment has been received successfully. Your handcrafted pieces will be prepared with love and shipped soon."));
		sb.append(section("Payment Details", kvTable(rows.toString())));
		sb.append(section("Amount Paid", "<div style=\"text-align:right;\">"
				+ "<span style=\"font-size:13px;color:" + C_MUTED + ";\">Paid via " + esc(String.valueOf(order.getPaymentMethod())) + "</span><br>"
				+ "<span style=\"font-size:26px;font-weight:700;color:" + C_SUCCESS + ";\">" + money(order.getTotal()) + "</span></div>"));
		sb.append(button(baseUrl + contextPath + "/my-orders", "Track My Order"));
		return wrap("Payment Successful", sb.toString());
	}

	@Override
	@Async
	public void sendNewOrderToAdmin(Order order, java.util.List<OrderItem> items) {
		try {
			String subject = "New Order Received | #" + order.getOrderNumber();
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
						.append("<td style=\"padding:10px 8px;border-bottom:1px solid " + C_BORDER + ";color:" + C_INK + ";font-size:13px;\">")
						.append(esc(item.getProductName() != null ? item.getProductName() : "-")).append("</td>")
						.append("<td style=\"padding:10px 8px;border-bottom:1px solid " + C_BORDER + ";color:" + C_MUTED + ";font-size:13px;text-align:center;\">")
						.append(item.getQuantity()).append("</td>")
						.append("<td style=\"padding:10px 8px;border-bottom:1px solid " + C_BORDER + ";color:" + C_MUTED + ";font-size:13px;text-align:right;\">")
						.append(money(item.getPrice())).append("</td>")
						.append("<td style=\"padding:10px 8px;border-bottom:1px solid " + C_BORDER + ";color:" + C_INK + ";font-size:13px;font-weight:600;text-align:right;\">")
						.append(money(item.getTotal())).append("</td>")
						.append("</tr>");
			}
		}

		StringBuilder rows = new StringBuilder();
		rows.append(kvRow("Order Number", "#" + order.getOrderNumber()));
		rows.append(kvRow("Order Date", fmt(order.getCreatedDate())));
		rows.append(kvRowHtml("Payment Method", esc(String.valueOf(order.getPaymentMethod())) + " "
				+ badge(String.valueOf(order.getPaymentStatus()), C_SUCCESS, C_SOFT)));
		rows.append(kvRowHtml("Order Status", badge(String.valueOf(order.getStatus()), C_WINE, C_SOFT)));

		StringBuilder customerRows = new StringBuilder();
		customerRows.append(kvRow("Name", order.getCustomerName()));
		customerRows.append(kvRow("Email", order.getEmail()));
		customerRows.append(kvRow("Phone", order.getPhone()));

		StringBuilder sb = new StringBuilder();
		sb.append(p("A new order has been placed at Loomelle Crochet. Details below:"));
		sb.append(section("Order Summary", kvTable(rows.toString())));
		sb.append(section("Customer Details", kvTable(customerRows.toString())));
		sb.append(section("Shipping Address", noteBox(esc(order.getShippingAddress()), C_ROSE)));
		sb.append(section("Order Items",
				"<table role=\"presentation\" width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"border-collapse:collapse;\">"
				+ "<thead><tr>"
				+ "<th align=\"left\" style=\"padding:10px 8px;background:" + C_WINE + ";color:#FFFFFF;font-size:12px;letter-spacing:0.5px;border-radius:6px 0 0 0;\">Product</th>"
				+ "<th align=\"center\" style=\"padding:10px 8px;background:" + C_WINE + ";color:#FFFFFF;font-size:12px;letter-spacing:0.5px;\">Qty</th>"
				+ "<th align=\"right\" style=\"padding:10px 8px;background:" + C_WINE + ";color:#FFFFFF;font-size:12px;letter-spacing:0.5px;\">Price</th>"
				+ "<th align=\"right\" style=\"padding:10px 8px;background:" + C_WINE + ";color:#FFFFFF;font-size:12px;letter-spacing:0.5px;border-radius:0 6px 0 0;\">Total</th>"
				+ "</tr></thead><tbody>" + itemsHtml + "</tbody></table>"));
		sb.append(section("Amount Payable", "<div style=\"text-align:right;\">"
				+ "<span style=\"font-size:13px;color:" + C_MUTED + ";\">Order Total</span><br>"
				+ "<span style=\"font-size:26px;font-weight:700;color:" + C_WINE + ";\">" + money(order.getTotal()) + "</span></div>"));
		sb.append(button(baseUrl + contextPath + "/admin/orders", "Open Admin Orders"));
		return wrap("New Order Alert", sb.toString());
	}

	@Override
	@Async
	public void sendOrderStatusUpdateToCustomer(Order order, OrderStatus newStatus) {
		try {
			String subject = "Your Loomelle Crochet Order Has Been Updated | #" + order.getOrderNumber();
			String html = buildStatusUpdateHtml(order, newStatus);
			sendHtmlEmail(order.getEmail(), subject, html);
		} catch (Exception e) {
			log.error("Failed to send order status update email to customer", e);
		}
	}

	private String buildStatusUpdateHtml(Order order, OrderStatus newStatus) {
		String statusColor = (newStatus == OrderStatus.CANCELLED || newStatus == OrderStatus.RETURNED) ? C_DANGER : C_SUCCESS;
		StringBuilder rows = new StringBuilder();
		rows.append(kvRow("Order Number", "#" + order.getOrderNumber()));
		rows.append(kvRow("Payment Method", order.getPaymentMethod()));
		rows.append(kvRowHtml("New Status", badge(String.valueOf(newStatus), statusColor, C_SOFT)));
		if (newStatus == OrderStatus.SHIPPED && order.getShippedDate() != null) {
			rows.append(kvRow("Shipped On", fmt(order.getShippedDate())));
		}
		if (newStatus == OrderStatus.DELIVERED && order.getDeliveredDate() != null) {
			rows.append(kvRow("Delivered On", fmt(order.getDeliveredDate())));
		}

		StringBuilder sb = new StringBuilder();
		sb.append(greeting(order.getCustomerName()));
		sb.append(p("The status of your order <strong>#" + esc(order.getOrderNumber()) + "</strong> has been updated."));
		sb.append(section("Order Details", kvTable(rows.toString())));
		sb.append(section("Shipping Address", noteBox(esc(order.getShippingAddress()), C_ROSE)));
		sb.append(section("Amount Payable", "<div style=\"text-align:right;\">"
	+ "<span style=\"font-size:13px;color:" + C_MUTED + ";\">Order Total</span><br>"
	+ "<span style=\"font-size:26px;font-weight:700;color:" + C_WINE + ";\">" + money(order.getTotal()) + "</span></div>"));

	if (newStatus == OrderStatus.DELIVERED) {
		sb.append(p("We hope you love your handcrafted pieces! Please share your feedback with us."));
		sb.append(button(baseUrl + contextPath + "/customer/feedback/" + order.getOrderNumber(), "Share Your Feedback"));
	} else {
		sb.append(button(baseUrl + contextPath + "/my-orders", "Track My Order"));
	}
	return wrap("Order Update", sb.toString());
	}

	@Override
	@Async
	public void sendContactUsEmail(String name, String email, String inquiryType, String message) {
		try {
			// 1. Send detailed inquiry email to Admin / Support team
			String adminSubject = "New Contact Inquiry | Loomelle Crochet Support" + (inquiryType != null && !inquiryType.trim().isEmpty() ? " | " + inquiryType : "");
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
					String customerSubject = "We Have Received Your Message | Loomelle Crochet";
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
		StringBuilder rows = new StringBuilder();
		rows.append(kvRow("Customer Name", name != null && !name.trim().isEmpty() ? name : "Anonymous"));
		rows.append(kvRow("Email Address", email != null ? email : "N/A"));
		rows.append(kvRowHtml("Inquiry Topic", badge(inquiryType != null && !inquiryType.trim().isEmpty() ? inquiryType : "General", C_WINE, C_SOFT)));

		StringBuilder sb = new StringBuilder();
		sb.append(p("A new contact form submission has been received:"));
		sb.append(section("Inquiry Details", kvTable(rows.toString())));
		sb.append(section("Message Content", noteBox(message != null ? esc(message) : "-", C_ROSE)));
		sb.append(p("You can directly reply to this email to get in touch with " + esc(name != null ? name : "the customer") + "."));
		return wrap("Contact Inquiry", sb.toString());
	}

	private String buildContactCustomerAckHtml(String name, String inquiryType, String message) {
		StringBuilder sb = new StringBuilder();
		sb.append(greeting(name));
		sb.append(p("Thank you for reaching out to <strong>Loomelle Crochet</strong>! We have received your inquiry regarding <em>"
				+ esc(inquiryType != null ? inquiryType : "your message") + "</em>."));
		sb.append(p("Our artisan support team is reviewing your details and will get back to you within <strong>24 business hours</strong>."));
		sb.append(section("Your Message Summary", noteBox(message != null ? esc(message) : "-", C_ROSE)));
		sb.append(p("Warm regards,<br/><strong>The Loomelle Crochet Studio Team</strong>"));
		return wrap("Message Received", sb.toString());
	}

}

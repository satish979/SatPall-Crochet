package com.satpall.crochet.service;

import com.satpall.crochet.entity.Order;
import com.satpall.crochet.entity.OrderItem;
import com.satpall.crochet.enums.OrderStatus;

public interface EmailService {

	void sendOrderConfirmation(Order order);

	void sendPaymentSuccessEmail(Order order);

	void sendHtmlEmail(String to, String subject, String htmlBody);

	void sendOtpEmail(String to, String otp);

	void sendNewOrderToAdmin(Order order, java.util.List<OrderItem> items);

	void sendOrderStatusUpdateToCustomer(Order order, OrderStatus newStatus);

	void sendContactUsEmail(String name, String email, String inquiryType, String message);

}

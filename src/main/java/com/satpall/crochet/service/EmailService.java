package com.satpall.crochet.service;

import com.satpall.crochet.entity.Order;

public interface EmailService {

	void sendOrderConfirmation(Order order);

	void sendPaymentSuccessEmail(Order order);

	void sendHtmlEmail(String to, String subject, String htmlBody);

}

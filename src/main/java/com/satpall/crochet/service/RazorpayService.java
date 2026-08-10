package com.satpall.crochet.service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.satpall.crochet.exception.PaymentException;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RazorpayService {

	@Value("${razorpay.key.id}")
	private String razorpayKeyId;

	@Value("${razorpay.key.secret}")
	private String razorpayKeySecret;

	public com.razorpay.Order createRazorpayOrder(java.math.BigDecimal amount, String receipt) throws RazorpayException {
		RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

		JSONObject orderRequest = new JSONObject();
		orderRequest.put("amount", amount.multiply(new java.math.BigDecimal(100)).intValue());
		orderRequest.put("currency", "INR");
		orderRequest.put("receipt", receipt);
		orderRequest.put("payment_capture", 1);

		return razorpay.orders.create(orderRequest);
	}

	public boolean verifyPaymentSignature(String orderId, String paymentId, String signature) {
		try {
			String payload = orderId + "|" + paymentId;
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(new SecretKeySpec(razorpayKeySecret.getBytes(), "HmacSHA256"));
			byte[] rawHmac = mac.doFinal(payload.getBytes());
			String generatedSignature = bytesToHex(rawHmac);
			return generatedSignature.equals(signature);
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw new PaymentException("Payment signature verification failed: " + e.getMessage());
		}
	}

	private String bytesToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}

	public String getRazorpayKeyId() {
		return razorpayKeyId;
	}

}
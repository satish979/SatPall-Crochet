package com.satpall.crochet.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import com.satpall.crochet.enums.OrderStatus;
import com.satpall.crochet.enums.PaymentMethod;
import com.satpall.crochet.enums.PaymentStatus;

import lombok.Data;

@Entity
@Table(name = "orders")
@Data
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String orderNumber;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<OrderItem> orderItems = new ArrayList<>();

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal subtotal;

	@Column(precision = 10, scale = 2)
	private BigDecimal tax;

	@Column(precision = 10, scale = 2)
	private BigDecimal shipping;

	@Column(precision = 10, scale = 2)
	private BigDecimal discount;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal total;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OrderStatus status = OrderStatus.PENDING;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_method", nullable = false)
	private PaymentMethod paymentMethod = PaymentMethod.COD;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_status")
	private PaymentStatus paymentStatus = PaymentStatus.PENDING;

	@Column(name = "shipping_address", columnDefinition = "TEXT", nullable = false)
	private String shippingAddress;

	@Column(name = "billing_address", columnDefinition = "TEXT")
	private String billingAddress;

	@Size(max = 50, message = "City must be less than 50 characters")
	@Column(nullable = false)
	private String city;

	@Size(max = 50, message = "State must be less than 50 characters")
	@Column(nullable = false)
	private String state;

	@Size(max = 10, message = "PIN code must be less than 10 characters")
	@Column(name = "pin_code", length = 10, nullable = false)
	private String pinCode;

	@Size(max = 15, message = "Phone must be less than 15 characters")
	@Column(nullable = false)
	private String phone;

	@Size(max = 500, message = "Notes must be less than 500 characters")
	@Column(length = 500)
	private String notes;

	@Column(name = "created_date", nullable = false)
	private LocalDateTime createdDate;

	@Column(name = "updated_date")
	private LocalDateTime updatedDate;

	@Column(name = "shipped_date")
	private LocalDateTime shippedDate;

	@Column(name = "delivered_date")
	private LocalDateTime deliveredDate;

	@Column(name = "cancelled_date")
	private LocalDateTime cancelledDate;

	@Column(name = "cancellation_reason", length = 500)
	private String cancellationReason;

	@Column(name = "coupon_code", length = 50)
	private String couponCode;

	@PrePersist
	protected void onPersist() {
		createdDate = LocalDateTime.now();
		updatedDate = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedDate = LocalDateTime.now();
	}

}
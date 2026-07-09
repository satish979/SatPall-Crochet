package com.satpall.crochet.dto;


import java.math.BigDecimal;

import lombok.Data;

@Data
public class CartItemDTO {
    
    private Long productId;
    private String productName;
    private String productImageUrl;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal total;
    
    public CartItemDTO() {
    }
    
 
}

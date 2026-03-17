package com.aistock.backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SignalRequest {
    private String symbol;
    private BigDecimal price;
    private BigDecimal quantity;
    private String action; // "BUY" or "SELL"
}

package com.svsbas.modules.billing.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CostCalculationService {

    public static final BigDecimal STANDARD_TAX_RATE = BigDecimal.valueOf(18.00); // 18% GST

    public BigDecimal calculateTax(BigDecimal subtotal, BigDecimal taxRate) {
        if (subtotal == null || taxRate == null) return BigDecimal.ZERO;
        return subtotal.multiply(taxRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateTotal(BigDecimal subtotal, BigDecimal taxAmount) {
        if (subtotal == null) subtotal = BigDecimal.ZERO;
        if (taxAmount == null) taxAmount = BigDecimal.ZERO;
        return subtotal.add(taxAmount).setScale(2, RoundingMode.HALF_UP);
    }
}

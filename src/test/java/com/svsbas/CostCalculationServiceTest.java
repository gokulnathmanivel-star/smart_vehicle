package com.svsbas;

import com.svsbas.modules.billing.service.CostCalculationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CostCalculationServiceTest {

    private CostCalculationService costCalculationService;

    @BeforeEach
    void setUp() {
        costCalculationService = new CostCalculationService();
    }

    @Test
    @DisplayName("Should correctly calculate 18% GST on subtotal")
    void testCalculateTax() {
        BigDecimal subtotal = BigDecimal.valueOf(1000.00);
        BigDecimal tax = costCalculationService.calculateTax(subtotal, CostCalculationService.STANDARD_TAX_RATE);
        assertEquals(BigDecimal.valueOf(180.00).setScale(2), tax);
    }

    @Test
    @DisplayName("Should correctly calculate grand total with tax")
    void testCalculateTotal() {
        BigDecimal subtotal = BigDecimal.valueOf(2500.00);
        BigDecimal tax = costCalculationService.calculateTax(subtotal, CostCalculationService.STANDARD_TAX_RATE);
        BigDecimal total = costCalculationService.calculateTotal(subtotal, tax);

        // Subtotal = 2500, Tax = 450, Total = 2950
        assertEquals(BigDecimal.valueOf(450.00).setScale(2), tax);
        assertEquals(BigDecimal.valueOf(2950.00).setScale(2), total);
    }

    @Test
    @DisplayName("Should handle null inputs gracefully")
    void testNullInputs() {
        BigDecimal tax = costCalculationService.calculateTax(null, CostCalculationService.STANDARD_TAX_RATE);
        BigDecimal total = costCalculationService.calculateTotal(null, null);

        assertEquals(BigDecimal.ZERO, tax);
        assertEquals(BigDecimal.valueOf(0.00).setScale(2), total);
    }
}

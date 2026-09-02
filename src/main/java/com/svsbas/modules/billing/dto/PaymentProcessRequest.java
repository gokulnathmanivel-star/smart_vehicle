package com.svsbas.modules.billing.dto;

import com.svsbas.modules.billing.entity.PaymentMode;
import jakarta.validation.constraints.NotNull;

public class PaymentProcessRequest {

    @NotNull(message = "Payment mode is required")
    private PaymentMode paymentMode;

    private String transactionRef;

    public PaymentProcessRequest() {}

    public PaymentMode getPaymentMode() { return paymentMode; }
    public void setPaymentMode(PaymentMode paymentMode) { this.paymentMode = paymentMode; }
    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }
}

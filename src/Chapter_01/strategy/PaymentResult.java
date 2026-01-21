package Chapter_01.strategy;

import java.math.BigDecimal;

public class PaymentResult {

    private final String transactionId;
    private final PaymentType paymentType;
    private final BigDecimal amount;
    private final PaymentStatus status;

    public PaymentResult(String transactionId, PaymentType paymentType, BigDecimal amount) {
        this.transactionId = transactionId;
        this.paymentType = paymentType;
        this.amount = amount;
        this.status = PaymentStatus.SUCCESS;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s원 결제 완료 (거래ID: %s)",
                paymentType, amount, transactionId);
    }
}

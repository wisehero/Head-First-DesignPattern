package Chapter_01.strategy;

import java.math.BigDecimal;
import java.util.Objects;

public class PaymentResult {

    private final String transactionId;
    private final PaymentType paymentType;
    private final BigDecimal amount;
    private final PaymentStatus status;

    public PaymentResult(String transactionId, PaymentType paymentType, BigDecimal amount) {
        this(transactionId, paymentType, amount, PaymentStatus.SUCCESS);
    }

    public PaymentResult(String transactionId, PaymentType paymentType, BigDecimal amount, PaymentStatus status) {
        this.transactionId = transactionId;
        this.paymentType = Objects.requireNonNull(paymentType, "paymentType must not be null");
        this.amount = Objects.requireNonNull(amount, "amount must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
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
        String statusText;
        switch (status) {
            case SUCCESS:
                statusText = "결제 완료";
                break;
            case FAILED:
                statusText = "결제 실패";
                break;
            case PENDING:
                statusText = "결제 대기";
                break;
            case CANCELLED:
                statusText = "결제 취소";
                break;
            default:
                statusText = status.name();
        }
        return String.format("[%s] %s원 %s (거래ID: %s)",
                paymentType, amount, statusText, transactionId);
    }
}

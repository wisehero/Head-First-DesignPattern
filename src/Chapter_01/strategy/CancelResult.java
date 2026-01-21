package Chapter_01.strategy;

import java.math.BigDecimal;

public class CancelResult {

    private final String transactionId;
    private final String cancelTransactionId;
    private final BigDecimal refundAmount;
    private final boolean success;

    private CancelResult(String transactionId,  String cancelTransactionId,
                         BigDecimal refundAmount, boolean success){
        this.transactionId = transactionId;
        this.cancelTransactionId = cancelTransactionId;
        this.refundAmount = refundAmount;
        this.success = success;
    }

    public static CancelResult success(String transactionId, String cancelTxId, BigDecimal amount) {
        return new CancelResult(transactionId, cancelTxId, amount, true);
    }

    public static CancelResult fail(String transactionId) {
        return new CancelResult(transactionId, null, BigDecimal.ZERO, false);
    }

    // Getter들
    public String getTransactionId() { return transactionId; }
    public String getCancelTransactionId() { return cancelTransactionId; }
    public BigDecimal getRefundAmount() { return refundAmount; }
    public boolean isSuccess() { return success; }
}

package Chapter_01.strategy.bank;

/**
 * 은행 API 응답
 */
public class BankResponse {
    private final String transactionId;
    private final boolean success;

    public BankResponse(String transactionId, boolean success) {
        this.transactionId = transactionId;
        this.success = success;
    }

    public String getTransactionId() { return transactionId; }
    public boolean isSuccess() { return success; }
}
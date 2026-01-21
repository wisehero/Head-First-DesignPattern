package Chapter_01.strategy.card;

/**
 * 카드사 API 응답
 */
public class CardResponse {
    private final String transactionId;
    private final String responseCode;

    public CardResponse(String transactionId, String responseCode) {
        this.transactionId = transactionId;
        this.responseCode = responseCode;
    }

    public String getTransactionId() { return transactionId; }
    public String getResponseCode() { return responseCode; }
}

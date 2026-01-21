package Chapter_01.strategy.card;

import Chapter_01.strategy.CancelResult;

import java.math.BigDecimal;

/**
 * 카드사 API 클라이언트 (가상)
 */
public class CardApiClient {

    public CardResponse requestPayment(String cardNumber, String cvc, BigDecimal amount) {
        // 실제로는 HTTP 호출
        System.out.println("  [CardAPI] 카드 승인 요청: " + maskCardNumber(cardNumber));
        return new CardResponse("CARD_TXN_" + System.currentTimeMillis(), "00");
    }

    public CancelResult cancel(String transactionId) {
        System.out.println("  [CardAPI] 카드 승인 취소: " + transactionId);
        return CancelResult.success(transactionId, "CANCEL_" + transactionId, BigDecimal.ZERO);
    }

    private String maskCardNumber(String cardNumber) {
        return cardNumber.substring(0, 4) + "-****-****-" + cardNumber.substring(12);
    }
}
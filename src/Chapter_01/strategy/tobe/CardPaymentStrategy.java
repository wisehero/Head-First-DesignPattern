package Chapter_01.strategy.tobe;

import Chapter_01.strategy.*;
import Chapter_01.strategy.card.CardApiClient;
import Chapter_01.strategy.card.CardResponse;

/**
 * 카드 결제 전략
 * 카드 결제에 관한 모든 것은 이 클래스가 책임진다
 */
public class CardPaymentStrategy implements PaymentStrategy{

    private final CardApiClient cardApiClient;

    public CardPaymentStrategy(CardApiClient cardApiClient) {
        this.cardApiClient = cardApiClient;
    }

    @Override
    public void validate(PaymentRequest request) {
        if (request.getCardNumber() == null || request.getCardNumber().length() != 16) {
            throw new PaymentException("유효하지 않은 카드번호입니다");
        }
        if (request.getCvc() == null || request.getCvc().length() != 3) {
            throw new PaymentException("유효하지 않은 CVC입니다");
        }
    }

    @Override
    public PaymentResult pay(PaymentRequest request) {
        CardResponse response = cardApiClient.requestPayment(
                request.getCardNumber(),
                request.getCvc(),
                request.getAmount()
        );
        return new PaymentResult(
                response.getTransactionId(),
                PaymentType.CARD,
                request.getAmount());
    }

    @Override
    public CancelResult cancel(String transactionId) {
        return cardApiClient.cancel(transactionId);
    }

    @Override
    public PaymentType getPaymentType() {
        return PaymentType.CARD;
    }
}

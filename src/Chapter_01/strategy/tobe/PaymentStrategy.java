package Chapter_01.strategy.tobe;

import Chapter_01.strategy.CancelResult;
import Chapter_01.strategy.PaymentRequest;
import Chapter_01.strategy.PaymentResult;
import Chapter_01.strategy.PaymentType;

/**
 * 결제 전략 인터페이스
 * 모든 결제 수단이 이 계약을 따른다
 */
public interface PaymentStrategy {

    void validate(PaymentRequest request);

    PaymentResult pay(PaymentRequest request);

    CancelResult cancel(String transactionId);

    PaymentType getPaymentType();

}

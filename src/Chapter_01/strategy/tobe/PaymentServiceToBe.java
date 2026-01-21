package Chapter_01.strategy.tobe;

import Chapter_01.strategy.CancelResult;
import Chapter_01.strategy.PaymentRequest;
import Chapter_01.strategy.PaymentResult;
import Chapter_01.strategy.PaymentType;

public class PaymentServiceToBe {

    private final PaymentStrategyRegistry registry;

    public PaymentServiceToBe(PaymentStrategyRegistry registry) {
        this.registry = registry;
    }

    public PaymentResult processPayment(PaymentType paymentType, PaymentRequest request) {
        // 1. 결제 타입에 맞는 전략을 가져온다
        PaymentStrategy strategy = registry.getStrategy(paymentType);

        // 2. 유효성 검증 위임
        strategy.validate(request);

        // 3. 결제 처리 위임
        return strategy.pay(request);
    }

    public CancelResult cancelPayment(PaymentType paymentType, String transactionId) {
        PaymentStrategy strategy = registry.getStrategy(paymentType);
        return strategy.cancel(transactionId);
    }
}

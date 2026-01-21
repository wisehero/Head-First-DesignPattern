package Chapter_01.strategy.tobe;

import Chapter_01.strategy.PaymentException;
import Chapter_01.strategy.PaymentType;

import java.util.HashMap;
import java.util.Map;

public class PaymentStrategyRegistry {

    private final Map<PaymentType, PaymentStrategy> strategies = new HashMap<>();

    // 전략 등록
    public void register(PaymentStrategy strategy) {
        strategies.put(strategy.getPaymentType(), strategy);
    }

    // 전략 조회
    public PaymentStrategy getStrategy(PaymentType paymentType) {
        PaymentStrategy strategy = strategies.get(paymentType);
        if (strategy == null) {
            throw new PaymentException("지원하지 않는 결제 수단입니다: " + paymentType);
        }
        return strategy;
    }

}

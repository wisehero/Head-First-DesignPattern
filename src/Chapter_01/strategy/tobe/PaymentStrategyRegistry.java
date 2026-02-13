package Chapter_01.strategy.tobe;

import Chapter_01.strategy.PaymentException;
import Chapter_01.strategy.PaymentType;

import java.util.HashMap;
import java.util.Map;

public class PaymentStrategyRegistry {

    private final Map<PaymentType, PaymentStrategy> strategies = new HashMap<>();

    // 전략 등록
    public void register(PaymentStrategy strategy) {
        if (strategy == null) {
            throw new PaymentException("전략은 null일 수 없습니다");
        }

        PaymentType paymentType = strategy.getPaymentType();
        if (strategies.containsKey(paymentType)) {
            throw new PaymentException("이미 등록된 결제 전략입니다: " + paymentType);
        }

        strategies.put(paymentType, strategy);
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

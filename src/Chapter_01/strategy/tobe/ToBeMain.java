package Chapter_01.strategy.tobe;

import Chapter_01.strategy.CancelResult;
import Chapter_01.strategy.PaymentRequest;
import Chapter_01.strategy.PaymentResult;
import Chapter_01.strategy.PaymentType;
import Chapter_01.strategy.bank.BankApiClient;
import Chapter_01.strategy.card.CardApiClient;
import Chapter_01.strategy.kakao.KakaoPayApiClient;

import java.math.BigDecimal;

public class ToBeMain {
    public static void main(String[] args) {
        PaymentStrategyRegistry registry = new PaymentStrategyRegistry();
        registry.register(new CardPaymentStrategy(new CardApiClient()));
        registry.register(new BankTransferStrategy(new BankApiClient()));
        registry.register(new KakaoPayStrategy(new KakaoPayApiClient()));

        PaymentServiceToBe paymentService = new PaymentServiceToBe(registry);

        // 3. 카드 결제
        System.out.println("=== 카드 결제 ===");
        PaymentRequest cardRequest = new PaymentRequest(
                1001L,
                new BigDecimal("50000"),
                "1234567890123456",
                "123"
        );
        PaymentResult cardResult = paymentService.processPayment(PaymentType.CARD, cardRequest);
        System.out.println(cardResult);

        // 4. 카카오페이 결제
        System.out.println("\n=== 카카오페이 결제 ===");
        PaymentRequest kakaoRequest = new PaymentRequest(
                1002L,
                new BigDecimal("30000"),
                "kakao_user_123"
        );
        PaymentResult kakaoResult = paymentService.processPayment(PaymentType.KAKAO_PAY, kakaoRequest);
        System.out.println(kakaoResult);

        // 5. 결제 취소
        System.out.println("\n=== 결제 취소 ===");
        CancelResult cancelResult = paymentService.cancelPayment(
                PaymentType.CARD,
                cardResult.getTransactionId()
        );
        System.out.println("취소 성공: " + cancelResult.isSuccess());
    }
}

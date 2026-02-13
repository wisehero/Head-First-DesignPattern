package Chapter_01.strategy.asis;

import Chapter_01.strategy.CancelResult;
import Chapter_01.strategy.PaymentRequest;
import Chapter_01.strategy.PaymentResult;
import Chapter_01.strategy.PaymentType;

import java.math.BigDecimal;

public class AsIsMain {
    public static void main(String[] args) {
        PaymentService paymentService = new PaymentService();

        // 1. 카드 결제
        System.out.println("=== 카드 결제 ===");
        PaymentRequest cardRequest = PaymentRequest.card(
                1001L,
                new BigDecimal("50000"),
                "1234567890123456",
                "123"
        );
        PaymentResult cardResult = paymentService.processPayment(PaymentType.CARD, cardRequest);
        System.out.println(cardResult);

        // 2. 카카오페이 결제
        System.out.println("\n=== 카카오페이 결제 ===");
        PaymentRequest kakaoRequest = PaymentRequest.kakaoPay(
                1002L,
                new BigDecimal("30000"),
                "kakao_user_123"
        );
        PaymentResult kakaoResult = paymentService.processPayment(PaymentType.KAKAO_PAY, kakaoRequest);
        System.out.println(kakaoResult);

        // 3. 결제 취소
        System.out.println("\n=== 결제 취소 ===");
        CancelResult cancelResult = paymentService.cancelPayment(
                PaymentType.CARD,
                cardResult.getTransactionId()
        );
        System.out.println("취소 성공: " + cancelResult.isSuccess());
    }
}

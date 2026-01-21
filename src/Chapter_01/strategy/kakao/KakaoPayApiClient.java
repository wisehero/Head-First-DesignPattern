package Chapter_01.strategy.kakao;

import Chapter_01.strategy.CancelResult;

import java.math.BigDecimal;

/**
 * 카카오페이 API 클라이언트 (가상)
 */
public class KakaoPayApiClient {

    public KakaoPayResponse pay(String kakaoUserId, BigDecimal amount) {
        System.out.println("  [KakaoPayAPI] 결제 요청: 사용자 " + kakaoUserId);
        return new KakaoPayResponse("KAKAO_TID_" + System.currentTimeMillis(), "SUCCESS");
    }

    public CancelResult cancel(String transactionId) {
        System.out.println("  [KakaoPayAPI] 결제 취소: " + transactionId);
        return CancelResult.success(transactionId, "CANCEL_" + transactionId, BigDecimal.ZERO);
    }
}

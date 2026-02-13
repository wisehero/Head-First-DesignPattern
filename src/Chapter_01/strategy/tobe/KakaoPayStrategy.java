package Chapter_01.strategy.tobe;

import Chapter_01.strategy.*;
import Chapter_01.strategy.kakao.KakaoPayApiClient;
import Chapter_01.strategy.kakao.KakaoPayResponse;

public class KakaoPayStrategy implements PaymentStrategy{

    private final KakaoPayApiClient kakaoPayApiClient;

    public KakaoPayStrategy(KakaoPayApiClient kakaoPayApiClient) {
        this.kakaoPayApiClient = kakaoPayApiClient;
    }

    @Override
    public void validate(PaymentRequest request) {
        if (request.getKakaoUserId() == null) {
            throw new PaymentException("카카오 사용자 ID가 필요합니다");
        }
    }

    @Override
    public PaymentResult pay(PaymentRequest request) {
        KakaoPayResponse response = kakaoPayApiClient.pay(
                request.getKakaoUserId(),
                request.getAmount()
        );
        PaymentStatus status = "SUCCESS".equals(response.getStatus())
                ? PaymentStatus.SUCCESS
                : PaymentStatus.FAILED;
        return new PaymentResult(response.getTid(), getPaymentType(), request.getAmount(), status);
    }

    @Override
    public CancelResult cancel(String transactionId) {
        return kakaoPayApiClient.cancel(transactionId);
    }

    @Override
    public PaymentType getPaymentType() {
        return PaymentType.KAKAO_PAY;
    }
}

package Chapter_01.strategy.asis;

import Chapter_01.strategy.*;
import Chapter_01.strategy.bank.BankApiClient;
import Chapter_01.strategy.bank.BankResponse;
import Chapter_01.strategy.card.CardApiClient;
import Chapter_01.strategy.card.CardResponse;
import Chapter_01.strategy.kakao.KakaoPayApiClient;
import Chapter_01.strategy.kakao.KakaoPayResponse;

/**
 * AS-IS : 모든 결제 로직이 하나의 클래스에 집중됨
 * 전형적인 레거시 코드 스타일
 */
public class PaymentService {

    private final CardApiClient cardApiClient;
    private final BankApiClient bankApiClient;
    private final KakaoPayApiClient kakaoPayApiClient;

    public PaymentService() {
        // 모든 API 클라이언트를 다 알고 있어야 함
        this.cardApiClient = new CardApiClient();
        this.bankApiClient = new BankApiClient();
        this.kakaoPayApiClient = new KakaoPayApiClient();
    }

    public PaymentResult processPayment(PaymentType paymentType, PaymentRequest request) {
        if (paymentType == PaymentType.CARD) {
            if (request.getCardNumber() == null || request.getCardNumber().length() != 16) {
                throw new PaymentException("유효하지 않은 카드번호입니다");
            }
            if (request.getCvc() == null || request.getCvc().length() != 3) {
                throw new PaymentException("유효하지 않은 CVC입니다");
            }

            // 카드 결제 처리
            CardResponse response = cardApiClient.requestPayment(
                    request.getCardNumber(),
                    request.getCvc(),
                    request.getAmount()
            );
            return new PaymentResult(
                    response.getTransactionId(),
                    PaymentType.CARD,
                    request.getAmount());
        } else if (paymentType == PaymentType.BANK_TRANSFER) {
            // 계좌이체 유효성 검증
            if (request.getBankCode() == null) {
                throw new PaymentException("유효하지 않은 은행 코드입니다");
            }
            if (request.getAccountNumber() == null) {
                throw new PaymentException("유효하지 않은 계좌번호입니다");
            }

            // 계좌이체 처리
            BankResponse response = bankApiClient.transfer(
                    request.getBankCode(),
                    request.getAccountNumber(),
                    request.getAmount()
            );
            return new PaymentResult(response.getTransactionId(), PaymentType.BANK_TRANSFER, request.getAmount());
        } else if (paymentType == PaymentType.KAKAO_PAY) {
            // 카카오페이 유효성 검증
            if (request.getKakaoUserId() == null) {
                throw new PaymentException("카카오 사용자 ID가 필요합니다.");
            }

            // 카카오페이 처리
            KakaoPayResponse response = kakaoPayApiClient.pay(
                    request.getKakaoUserId(),
                    request.getAmount()
            );
            return new PaymentResult(response.getTid(), PaymentType.KAKAO_PAY, request.getAmount());
        }

        // 토스페이 추가되면 여기에 또 다른 else if 블록이 추가될 것임
        // 코드가 점점 더 길어지고 복잡해짐

        throw new PaymentException("지원하지 않는 결제 수단입니다" + paymentType);
    }

    /**
     * 결제 취소 - 또 조건문 분기
     */
    public CancelResult cancelPayment(PaymentType paymentType, String transactionId) {

        if (paymentType == PaymentType.CARD) {
            return cardApiClient.cancel(transactionId);

        } else if (paymentType == PaymentType.BANK_TRANSFER) {
            return bankApiClient.cancelTransfer(transactionId);

        } else if (paymentType == PaymentType.KAKAO_PAY) {
            return kakaoPayApiClient.cancel(transactionId);
        }

        throw new PaymentException("지원하지 않는 결제 수단입니다: " + paymentType);
    }
}

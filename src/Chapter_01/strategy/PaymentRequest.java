package Chapter_01.strategy;

import java.math.BigDecimal;

public class PaymentRequest {

    private final Long orderId;
    private final BigDecimal amount;

    // 카드 결제용
    private final String cardNumber;
    private final String cvc;

    // 계좌 이체용
    private final String bankCode;
    private final String accountNumber;

    // 간편 결제용
    private final String kakaoUserId;

    public PaymentRequest(Long orderId, BigDecimal amount, String cardNumber, String cvc) {
        this.orderId = orderId;
        this.amount = amount;
        this.cardNumber = cardNumber;
        this.cvc = cvc;
        this.bankCode = null;
        this.accountNumber = null;
        this.kakaoUserId = null;
    }

    public PaymentRequest(Long orderId, BigDecimal amount, String bankCode, String accountNumber, boolean isBank) {
        this.orderId = orderId;
        this.amount = amount;
        this.cardNumber = null;
        this.cvc = null;
        this.bankCode = bankCode;
        this.accountNumber = accountNumber;
        this.kakaoUserId = null;
    }

    public PaymentRequest(Long orderId, BigDecimal amount, String kakaoUserId) {
        this.orderId = orderId;
        this.amount = amount;
        this.cardNumber = null;
        this.cvc = null;
        this.bankCode = null;
        this.accountNumber = null;
        this.kakaoUserId = kakaoUserId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getCvc() {
        return cvc;
    }

    public String getBankCode() {
        return bankCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getKakaoUserId() {
        return kakaoUserId;
    }
}

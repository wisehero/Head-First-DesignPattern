package Chapter_01.strategy;

import java.math.BigDecimal;
import java.util.Objects;

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

    private PaymentRequest(
            Long orderId,
            BigDecimal amount,
            String cardNumber,
            String cvc,
            String bankCode,
            String accountNumber,
            String kakaoUserId
    ) {
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
        this.amount = Objects.requireNonNull(amount, "amount must not be null");
        this.cardNumber = cardNumber;
        this.cvc = cvc;
        this.bankCode = bankCode;
        this.accountNumber = accountNumber;
        this.kakaoUserId = kakaoUserId;
    }

    public static PaymentRequest card(Long orderId, BigDecimal amount, String cardNumber, String cvc) {
        return new PaymentRequest(orderId, amount, cardNumber, cvc, null, null, null);
    }

    public static PaymentRequest bankTransfer(Long orderId, BigDecimal amount, String bankCode, String accountNumber) {
        return new PaymentRequest(orderId, amount, null, null, bankCode, accountNumber, null);
    }

    public static PaymentRequest kakaoPay(Long orderId, BigDecimal amount, String kakaoUserId) {
        return new PaymentRequest(orderId, amount, null, null, null, null, kakaoUserId);
    }

    public PaymentRequest(Long orderId, BigDecimal amount, String cardNumber, String cvc) {
        this(orderId, amount, cardNumber, cvc, null, null, null);
    }

    public PaymentRequest(Long orderId, BigDecimal amount, String bankCode, String accountNumber, boolean isBank) {
        if (!isBank) {
            throw new IllegalArgumentException("은행 이체 요청 생성자는 isBank=true로 호출해야 합니다.");
        }
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
        this.amount = Objects.requireNonNull(amount, "amount must not be null");
        this.cardNumber = null;
        this.cvc = null;
        this.bankCode = bankCode;
        this.accountNumber = accountNumber;
        this.kakaoUserId = null;
    }

    public PaymentRequest(Long orderId, BigDecimal amount, String kakaoUserId) {
        this(orderId, amount, null, null, null, null, kakaoUserId);
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

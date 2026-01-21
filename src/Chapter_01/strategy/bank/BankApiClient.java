package Chapter_01.strategy.bank;

import Chapter_01.strategy.CancelResult;

import java.math.BigDecimal;

/**
 * 은행 API 클라이언트 (가상)
 */
public class BankApiClient {

    public BankResponse transfer(String bankCode, String accountNumber, BigDecimal amount) {
        System.out.println("  [BankAPI] 계좌이체 요청: " + bankCode + " / " + accountNumber);
        return new BankResponse("BANK_TXN_" + System.currentTimeMillis(), true);
    }

    public CancelResult cancelTransfer(String transactionId) {
        System.out.println("  [BankAPI] 이체 취소: " + transactionId);
        return CancelResult.success(transactionId, "CANCEL_" + transactionId, BigDecimal.ZERO);
    }
}

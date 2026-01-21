package Chapter_01.strategy.tobe;

import Chapter_01.strategy.*;
import Chapter_01.strategy.bank.BankApiClient;
import Chapter_01.strategy.bank.BankResponse;

public class BankTransferStrategy implements PaymentStrategy{

    private final BankApiClient bankApiClient;

    public BankTransferStrategy(BankApiClient bankApiClient) {
        this.bankApiClient = bankApiClient;
    }
    @Override
    public void validate(PaymentRequest request) {
        if (request.getBankCode() == null) {
            throw new PaymentException("유효하지 않은 은행 코드입니다");
        }
        if (request.getAccountNumber() == null) {
            throw new PaymentException("유효하지 않은 계좌번호입니다");
        }
    }

    @Override
    public PaymentResult pay(PaymentRequest request) {
        BankResponse response = bankApiClient.transfer(
                request.getBankCode(),
                request.getAccountNumber(),
                request.getAmount()
        );
        return new PaymentResult(response.getTransactionId(), getPaymentType(), request.getAmount());
    }


    @Override
    public CancelResult cancel(String transactionId) {
        return bankApiClient.cancelTransfer(transactionId);
    }

    @Override
    public PaymentType getPaymentType() {
        return PaymentType.BANK_TRANSFER;
    }
}

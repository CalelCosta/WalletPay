package recargapay.wallet.application.service;

import org.springframework.http.ResponseEntity;
import recargapay.wallet.application.dto.response.DepositResponse;
import recargapay.wallet.application.dto.response.TransferResponse;
import recargapay.wallet.application.dto.response.WithdrawResponse;

public interface WalletTransactionService {

    ResponseEntity<DepositResponse> deposit();
    ResponseEntity<WithdrawResponse> withdraw();
    ResponseEntity<TransferResponse> transfer();
}

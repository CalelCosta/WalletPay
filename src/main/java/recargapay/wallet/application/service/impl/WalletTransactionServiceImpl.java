package recargapay.wallet.application.service.impl;

import org.springframework.http.ResponseEntity;
import recargapay.wallet.application.dto.response.DepositResponse;
import recargapay.wallet.application.dto.response.TransferResponse;
import recargapay.wallet.application.dto.response.WithdrawResponse;
import recargapay.wallet.application.service.WalletTransactionService;

public class WalletTransactionServiceImpl implements WalletTransactionService {
    @Override
    public ResponseEntity<DepositResponse> deposit() {
        return null;
    }

    @Override
    public ResponseEntity<WithdrawResponse> withdraw() {
        return null;
    }

    @Override
    public ResponseEntity<TransferResponse> transfer() {
        return null;
    }
}

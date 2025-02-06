package recargapay.wallet.application.service.impl;

import org.springframework.http.ResponseEntity;
import recargapay.wallet.application.dto.response.BalanceResponse;
import recargapay.wallet.application.dto.response.WalletResponse;
import recargapay.wallet.application.service.WalletService;

public class WalletServiceImpl implements WalletService {
    @Override
    public ResponseEntity<WalletResponse> createWallet() {
        return null;
    }

    @Override
    public ResponseEntity<BalanceResponse> getBalance() {
        return null;
    }
}

package recargapay.wallet.application.service.impl;

import org.springframework.http.ResponseEntity;
import recargapay.wallet.application.dto.response.BalanceResponseDTO;
import recargapay.wallet.application.dto.response.WalletResponseDTO;
import recargapay.wallet.application.service.WalletService;

public class WalletServiceImpl implements WalletService {
    @Override
    public ResponseEntity<WalletResponseDTO> createWallet() {
        return null;
    }

    @Override
    public ResponseEntity<BalanceResponseDTO> getBalance() {
        return null;
    }
}

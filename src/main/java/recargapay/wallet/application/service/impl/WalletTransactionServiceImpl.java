package recargapay.wallet.application.service.impl;

import org.springframework.http.ResponseEntity;
import recargapay.wallet.application.dto.response.DepositResponseDTO;
import recargapay.wallet.application.dto.response.TransferResponseDTO;
import recargapay.wallet.application.dto.response.WithdrawResponseDTO;
import recargapay.wallet.application.service.WalletTransactionService;

public class WalletTransactionServiceImpl implements WalletTransactionService {
    @Override
    public ResponseEntity<DepositResponseDTO> deposit() {
        return null;
    }

    @Override
    public ResponseEntity<WithdrawResponseDTO> withdraw() {
        return null;
    }

    @Override
    public ResponseEntity<TransferResponseDTO> transfer() {
        return null;
    }
}

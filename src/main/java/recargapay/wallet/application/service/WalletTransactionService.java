package recargapay.wallet.application.service;

import org.springframework.http.ResponseEntity;
import recargapay.wallet.application.dto.response.DepositResponseDTO;
import recargapay.wallet.application.dto.response.TransferResponseDTO;
import recargapay.wallet.application.dto.response.WithdrawResponseDTO;

public interface WalletTransactionService {

    ResponseEntity<DepositResponseDTO> deposit();
    ResponseEntity<WithdrawResponseDTO> withdraw();
    ResponseEntity<TransferResponseDTO> transfer();
}

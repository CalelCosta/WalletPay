package recargapay.wallet.application.service;

import org.springframework.http.ResponseEntity;
import recargapay.wallet.application.dto.response.*;

public interface WalletService {

    ResponseEntity<WalletResponse> createWallet();
    ResponseEntity<BalanceResponse> getBalance();

}

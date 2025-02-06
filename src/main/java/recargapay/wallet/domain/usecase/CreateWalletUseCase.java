package recargapay.wallet.domain.usecase;

import org.springframework.http.ResponseEntity;
import recargapay.wallet.application.dto.request.CreateWalletRequestDTO;
import recargapay.wallet.application.dto.response.WalletResponseDTO;
import recargapay.wallet.infra.model.User;

public interface CreateWalletUseCase {

    ResponseEntity<WalletResponseDTO> createWallet(CreateWalletRequestDTO walletRequestDTO);
}

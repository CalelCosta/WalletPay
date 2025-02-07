package recargapay.wallet.domain.usecase;

import recargapay.wallet.application.dto.request.CreateWalletRequestDTO;
import recargapay.wallet.application.dto.response.WalletResponseDTO;

public interface CreateWalletUseCase {

    WalletResponseDTO createWallet(CreateWalletRequestDTO walletRequestDTO);
}

package recargapay.wallet.application.service;

import recargapay.wallet.application.dto.request.CreateWalletRequestDTO;
import recargapay.wallet.application.dto.response.BalanceResponseDTO;
import recargapay.wallet.application.dto.response.WalletResponseDTO;
import recargapay.wallet.infra.model.User;

public interface WalletService {

    WalletResponseDTO createWallet(CreateWalletRequestDTO createWalletRequestDTO, User user);
    WalletResponseDTO createWalletAndNewUser(CreateWalletRequestDTO createWalletRequestDTO);
    BalanceResponseDTO getBalance();

}

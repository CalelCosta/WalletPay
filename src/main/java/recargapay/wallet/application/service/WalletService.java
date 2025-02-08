package recargapay.wallet.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import recargapay.wallet.application.dto.request.CreateWalletRequestDTO;
import recargapay.wallet.application.dto.response.BalanceResponseDTO;
import recargapay.wallet.application.dto.response.WalletResponseDTO;
import recargapay.wallet.infra.model.User;
import recargapay.wallet.infra.model.Wallet;

public interface WalletService {

    WalletResponseDTO createWallet(CreateWalletRequestDTO createWalletRequestDTO, User user);
    WalletResponseDTO createWalletAndNewUser(CreateWalletRequestDTO createWalletRequestDTO);
    Page<BalanceResponseDTO> getBalanceWithFilter(Pageable pageable, Wallet wallet, String date);
    Page<BalanceResponseDTO> getAllBalanceWithCurrentDate(Pageable pageable, Wallet wallet, String localDate);

}

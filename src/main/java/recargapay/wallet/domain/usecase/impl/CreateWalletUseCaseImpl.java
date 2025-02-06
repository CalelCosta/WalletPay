package recargapay.wallet.domain.usecase.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import recargapay.wallet.application.dto.request.CreateWalletRequestDTO;
import recargapay.wallet.application.dto.response.WalletResponseDTO;
import recargapay.wallet.domain.usecase.CreateWalletUseCase;
import recargapay.wallet.infra.repository.WalletRepository;

@Service
public class CreateWalletUseCaseImpl implements CreateWalletUseCase {

    WalletRepository walletRepository;

    @Override
    public ResponseEntity<WalletResponseDTO> createWallet(CreateWalletRequestDTO walletRequestDTO) {

        var walletById = walletRepository.findWalletById(1L);
        return null;
    }
}

package recargapay.wallet.domain.usecase.impl;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import recargapay.wallet.application.dto.request.CreateWalletRequestDTO;
import recargapay.wallet.application.dto.response.WalletResponseDTO;
import recargapay.wallet.application.service.WalletService;
import recargapay.wallet.domain.exception.BusinessException;
import recargapay.wallet.domain.usecase.CreateWalletUseCase;
import recargapay.wallet.infra.model.User;
import recargapay.wallet.infra.repository.UserRepository;

import static recargapay.wallet.domain.exception.ExceptionEnum.ALREADY_EXIST;

@Service
public class CreateWalletUseCaseImpl implements CreateWalletUseCase {

    private final WalletService walletService;
    private final UserRepository userRepository;

    public CreateWalletUseCaseImpl(WalletService walletService, UserRepository userRepository) {
        this.walletService = walletService;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public WalletResponseDTO createWallet(CreateWalletRequestDTO walletRequestDTO) {
        User userByCpf = userRepository.findByCpf(walletRequestDTO.getCpf());
        if (userByCpf == null) {
            return walletService.createWalletAndNewUser(walletRequestDTO);
        } else if (userByCpf.getWallet() == null) {
            return walletService.createWallet(walletRequestDTO, userByCpf);
        } else {
            throw new BusinessException(ALREADY_EXIST.getMessage(), ALREADY_EXIST.getStatusCode());
        }
    }
}

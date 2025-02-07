package recargapay.wallet.application.service.impl;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import recargapay.wallet.application.dto.request.CreateWalletRequestDTO;
import recargapay.wallet.application.dto.response.BalanceResponseDTO;
import recargapay.wallet.application.dto.response.WalletResponseDTO;
import recargapay.wallet.application.service.WalletService;
import recargapay.wallet.domain.exception.PersistenceException;
import recargapay.wallet.domain.mapper.WalletMapper;
import recargapay.wallet.infra.model.User;
import recargapay.wallet.infra.model.Wallet;
import recargapay.wallet.infra.repository.UserRepository;
import recargapay.wallet.infra.repository.WalletRepository;

import java.math.BigDecimal;

import static recargapay.wallet.domain.exception.ExceptionEnum.ERROR_PERSISTENCE;

@Service("walletService")
public class WalletServiceImpl implements WalletService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;

    public WalletServiceImpl(UserRepository userRepository, WalletRepository walletRepository, WalletMapper walletMapper) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.walletMapper = walletMapper;
    }

    @Override
    public WalletResponseDTO createWallet(CreateWalletRequestDTO createWalletRequestDTO, User user) throws PersistenceException {

        return null;
    }

    @Override
    public WalletResponseDTO createWalletAndNewUser(CreateWalletRequestDTO createWalletRequestDTO) {
        User newUser = createUserPersistAndReturn(createWalletRequestDTO);
        Wallet newWallet = createWalletAndPersist(createWalletRequestDTO, newUser);
        return walletMapper.toDto(newWallet.getUser(), newWallet);
    }

    @Override
    public BalanceResponseDTO getBalance() {
        return null;
    }

    private User createUserPersistAndReturn(CreateWalletRequestDTO userRequestDTO) {
        User newUser = User.builder()
                .username(userRequestDTO.getUsername())
                .cpf(userRequestDTO.getCpf())
                .email(userRequestDTO.getEmail())
                .password(passwordEncoder().encode(userRequestDTO.getPassword()))
                .build();
        try {
            userRepository.saveAndFlush(newUser);
        } catch (RuntimeException e) {
            throw new PersistenceException(ERROR_PERSISTENCE.getMessage(), ERROR_PERSISTENCE.getStatusCode());
        }
        return userRepository.findByCpf(userRequestDTO.getCpf());
    }

    private Wallet createWalletAndPersist(CreateWalletRequestDTO createWalletRequestDTO, User user) {
        Wallet newWallet = Wallet.builder()
                .user(user)
                .balance(BigDecimal.ZERO)
                .currency(createWalletRequestDTO.getCurrency())
                .build();
        try {
            walletRepository.saveAndFlush(newWallet);
        } catch (RuntimeException e) {
            throw new PersistenceException(ERROR_PERSISTENCE.getMessage(), ERROR_PERSISTENCE.getStatusCode());
        }
        return walletRepository.findWalletById(newWallet.getId());
    }

    private PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

package recargapay.wallet.domain.usecase.impl;

import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import recargapay.wallet.application.dto.response.BalanceResponseDTO;
import recargapay.wallet.application.service.WalletService;
import recargapay.wallet.domain.exception.BusinessException;
import recargapay.wallet.domain.usecase.GetBalanceUseCase;
import recargapay.wallet.infra.model.User;
import recargapay.wallet.infra.model.Wallet;
import recargapay.wallet.infra.repository.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

import static recargapay.wallet.domain.exception.ExceptionEnum.NOT_FOUND;

@Service
public class GetBalanceUseCaseImpl implements GetBalanceUseCase {

    private final WalletService walletService;
    private final UserRepository userRepository;
    private static final String DATE_PATTERN = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$";

    public GetBalanceUseCaseImpl(WalletService walletService, UserRepository userRepository) {
        this.walletService = walletService;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    @Cacheable(value = "getBalance", keyGenerator = "keyGenerator", unless = "#result == null")
    public Page<BalanceResponseDTO> getBalance(Pageable pageable, Long userId, String date) {
        Optional<Wallet> result = userRepository.findById(Math.toIntExact(userId)).map(User::getWallet);
        if (result.isPresent() && date.equals("2099-12-31")) {
            date = LocalDate.now().toString();
            return walletService.getAllBalanceWithCurrentDate(pageable, result.get(), date);
        } else if (date.matches(DATE_PATTERN)) {
            return walletService.getBalanceWithFilter(pageable, result.get(), date);
        } else {
            throw new BusinessException(NOT_FOUND.getMessage(), NOT_FOUND.getStatusCode());
        }
    }
}

package recargapay.wallet.domain.usecase.impl;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import recargapay.wallet.application.dto.request.DepositRequestDTO;
import recargapay.wallet.domain.exception.BusinessException;
import recargapay.wallet.domain.usecase.DepositUseCase;
import recargapay.wallet.infra.model.User;
import recargapay.wallet.infra.model.Wallet;
import recargapay.wallet.infra.repository.UserRepository;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.Optional;

import static recargapay.wallet.domain.exception.ExceptionEnum.AMOUNT_NOT_VALID;
import static recargapay.wallet.domain.exception.ExceptionEnum.NOT_FOUND;

@Service
@Slf4j
public class DepositUseCaseImpl implements DepositUseCase {

    private final UserRepository userRepository;
    private final KafkaTemplate<String, DepositRequestDTO> kafkaTemplate;

    public DepositUseCaseImpl(UserRepository userRepository, KafkaTemplate<String, DepositRequestDTO> kafkaTemplate) {
        this.userRepository = userRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @Transactional
    public void execute(DepositRequestDTO depositRequestDTO) {
        verifyDepositAmount(depositRequestDTO);
        Optional<Wallet> result = Optional.of(userRepository.findByCpf(depositRequestDTO.getCpf())).map(User::getWallet);
        if (result.isEmpty()) {
            throw new BusinessException(NOT_FOUND.getMessage(), NOT_FOUND.getStatusCode());
        } else {
            log.info("Send Deposit Event: {}", depositRequestDTO);
            kafkaTemplate.send("deposit-events", depositRequestDTO);
        }
    }

    private void verifyDepositAmount(DepositRequestDTO depositRequestDTO) {
        if (depositRequestDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0 ) {
            throw new BusinessException(AMOUNT_NOT_VALID.getMessage(), AMOUNT_NOT_VALID.getStatusCode());
        }
    }
}

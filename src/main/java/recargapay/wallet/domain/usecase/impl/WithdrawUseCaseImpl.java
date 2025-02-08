package recargapay.wallet.domain.usecase.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import recargapay.wallet.application.dto.request.WithdrawRequestDTO;
import recargapay.wallet.domain.exception.BusinessException;
import recargapay.wallet.domain.usecase.WithdrawUseCase;
import recargapay.wallet.infra.model.User;
import recargapay.wallet.infra.model.Wallet;
import recargapay.wallet.infra.repository.UserRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static recargapay.wallet.domain.exception.ExceptionEnum.AMOUNT_NOT_VALID;
import static recargapay.wallet.domain.exception.ExceptionEnum.NOT_FOUND;

@Service
@Slf4j
public class WithdrawUseCaseImpl implements WithdrawUseCase {

    private final UserRepository userRepository;
    private final KafkaTemplate<String, WithdrawRequestDTO> kafkaTemplate;

    public WithdrawUseCaseImpl(UserRepository userRepository, KafkaTemplate<String, WithdrawRequestDTO> kafkaTemplate) {
        this.userRepository = userRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void execute(WithdrawRequestDTO withdrawRequestDTO) {
        if (withdrawRequestDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0 ) {
            throw new BusinessException(AMOUNT_NOT_VALID.getMessage(), AMOUNT_NOT_VALID.getStatusCode());
        }
        Optional<Wallet> result = Optional.ofNullable(userRepository.findByCpf(withdrawRequestDTO.getCpf())).map(User::getWallet);
        if (result.isEmpty()) {
            throw new BusinessException(NOT_FOUND.getMessage(), NOT_FOUND.getStatusCode());
        } else {
            log.info("Send Withdraw Event: {}", withdrawRequestDTO);
            kafkaTemplate.send("withdraw-events", withdrawRequestDTO);
        }
    }
}

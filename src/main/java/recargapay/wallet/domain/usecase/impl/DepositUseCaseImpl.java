package recargapay.wallet.domain.usecase.impl;

import com.google.gson.Gson;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import recargapay.wallet.application.dto.request.DepositRequestDTO;
import recargapay.wallet.domain.exception.BusinessException;
import recargapay.wallet.domain.usecase.DepositUseCase;
import recargapay.wallet.infra.model.User;
import recargapay.wallet.infra.model.Wallet;
import recargapay.wallet.infra.model.outbox.OutboxEvent;
import recargapay.wallet.infra.repository.OutboxRepository;
import recargapay.wallet.infra.repository.UserRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static recargapay.wallet.domain.exception.ExceptionEnum.AMOUNT_NOT_VALID;
import static recargapay.wallet.domain.exception.ExceptionEnum.NOT_FOUND;

@Service
@Slf4j
public class DepositUseCaseImpl implements DepositUseCase {

    private final UserRepository userRepository;
    private final OutboxRepository outboxRepository;


    public DepositUseCaseImpl(UserRepository userRepository, OutboxRepository outboxRepository) {
        this.userRepository = userRepository;
        this.outboxRepository = outboxRepository;
    }

    @Override
    @Transactional
    public void execute(DepositRequestDTO depositRequestDTO) {
        verifyDepositAmount(depositRequestDTO);
        Optional<Wallet> result = Optional.ofNullable(userRepository.findByCpf(depositRequestDTO.getCpf())).map(User::getWallet);
        if (result.isEmpty()) {
            throw new BusinessException(NOT_FOUND.getMessage(), NOT_FOUND.getStatusCode());
        } else {
            depositRequestDTO.setMessageId(UUID.randomUUID().toString());
            Gson gson = new Gson();
            OutboxEvent event = OutboxEvent.builder()
                    .id(UUID.randomUUID())
                    .aggregateType("Wallet")
                    .aggregateId(result.get().getId().toString())
                    .eventType("DepositRequested")
                    .payload(gson.toJson(depositRequestDTO))
                    .processed(false)
                    .build();
            outboxRepository.saveAndFlush(event);
            log.info("Deposit event saved to Outbox: {}", depositRequestDTO);
        }
    }

    private void verifyDepositAmount(DepositRequestDTO depositRequestDTO) {
        if (depositRequestDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(AMOUNT_NOT_VALID.getMessage(), AMOUNT_NOT_VALID.getStatusCode());
        }
    }
}

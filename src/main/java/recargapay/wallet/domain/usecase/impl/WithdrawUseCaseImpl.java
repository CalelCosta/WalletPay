package recargapay.wallet.domain.usecase.impl;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import recargapay.wallet.application.dto.request.WithdrawRequestDTO;
import recargapay.wallet.domain.exception.BusinessException;
import recargapay.wallet.domain.usecase.WithdrawUseCase;
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
public class WithdrawUseCaseImpl implements WithdrawUseCase {

    private final UserRepository userRepository;
    private final OutboxRepository outboxRepository;
    private final Gson gson;

    public WithdrawUseCaseImpl(UserRepository userRepository, OutboxRepository outboxRepository, Gson gson) {
        this.userRepository = userRepository;
        this.outboxRepository = outboxRepository;
        this.gson = gson;
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
            log.info("Prepare Withdraw Event...");
            withdrawRequestDTO.setMessageId(UUID.randomUUID().toString());
            OutboxEvent event = OutboxEvent.builder()
                    .id(UUID.randomUUID())
                    .aggregateType("Wallet")
                    .aggregateId(result.get().getId().toString())
                    .eventType("WithdrawRequested")
                    .payload(gson.toJson(withdrawRequestDTO))
                    .processed(false)
                    .build();
            outboxRepository.saveAndFlush(event);
            log.info("Withdraw event saved to Outbox: {}", withdrawRequestDTO);
        }
    }
}

package recargapay.wallet.domain.usecase.impl;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import recargapay.wallet.application.dto.request.TransferRequestDTO;
import recargapay.wallet.domain.exception.BusinessException;
import recargapay.wallet.domain.usecase.TransferUseCase;
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
public class TransferUseCaseImpl implements TransferUseCase {

    private final UserRepository userRepository;
    private final OutboxRepository outboxRepository;
    private final Gson gson;

    public TransferUseCaseImpl(UserRepository userRepository, KafkaTemplate<String, TransferRequestDTO> kafkaTemplate, OutboxRepository outboxRepository, Gson gson) {
        this.userRepository = userRepository;
        this.outboxRepository = outboxRepository;
        this.gson = gson;
    }

    @Override
    public void execute(TransferRequestDTO transferRequestDTO) {
        if (transferRequestDTO.getAmount().compareTo(BigDecimal.ZERO) > 0 ) {
            Optional<Wallet> sourceWallet = Optional.ofNullable(userRepository.findByCpf(transferRequestDTO.getFromCpf())).map(User::getWallet);
            Optional<Wallet> targetWallet = Optional.ofNullable(userRepository.findByCpf(transferRequestDTO.getToCpf())).map(User::getWallet);
            if (sourceWallet.isPresent() && targetWallet.isPresent()) {
                transferRequestDTO.setMessageId(UUID.randomUUID().toString());
                OutboxEvent event = OutboxEvent.builder()
                        .id(UUID.randomUUID())
                        .aggregateType("Wallet")
                        .aggregateId(sourceWallet.get().getId().toString())
                        .eventType("TransferRequested")
                        .payload(gson.toJson(transferRequestDTO))
                        .processed(false)
                        .build();
                outboxRepository.saveAndFlush(event);
                log.info("Transfer event saved to Outbox: {}", transferRequestDTO);
            } else {
                throw new BusinessException(NOT_FOUND.getMessage(), NOT_FOUND.getStatusCode());
            }
        } else {
            throw new BusinessException(AMOUNT_NOT_VALID.getMessage(), AMOUNT_NOT_VALID.getStatusCode());
        }
    }
}

package recargapay.wallet.domain.usecase.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import recargapay.wallet.application.dto.request.TransferRequestDTO;
import recargapay.wallet.domain.exception.BusinessException;
import recargapay.wallet.domain.usecase.TransferUseCase;
import recargapay.wallet.infra.model.User;
import recargapay.wallet.infra.model.Wallet;
import recargapay.wallet.infra.repository.UserRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static recargapay.wallet.domain.exception.ExceptionEnum.AMOUNT_NOT_VALID;
import static recargapay.wallet.domain.exception.ExceptionEnum.NOT_FOUND;

@Service
@Slf4j
public class TransferUseCaseImpl implements TransferUseCase {

    private final UserRepository userRepository;
    private final KafkaTemplate<String, TransferRequestDTO> kafkaTemplate;

    public TransferUseCaseImpl(UserRepository userRepository, KafkaTemplate<String, TransferRequestDTO> kafkaTemplate) {
        this.userRepository = userRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void execute(TransferRequestDTO transferRequestDTO) {
        if (transferRequestDTO.getAmount().compareTo(BigDecimal.ZERO) > 0 ) {
            Optional<Wallet> sourceWallet = Optional.ofNullable(userRepository.findByCpf(transferRequestDTO.getFromCpf())).map(User::getWallet);
            Optional<Wallet> targetWallet = Optional.ofNullable(userRepository.findByCpf(transferRequestDTO.getToCpf())).map(User::getWallet);
            if (sourceWallet.isPresent() && targetWallet.isPresent()) {
                kafkaTemplate.send("transfer-events", transferRequestDTO);
            } else {
                throw new BusinessException(NOT_FOUND.getMessage(), NOT_FOUND.getStatusCode());
            }
        } else {
            throw new BusinessException(AMOUNT_NOT_VALID.getMessage(), AMOUNT_NOT_VALID.getStatusCode());
        }
    }
}

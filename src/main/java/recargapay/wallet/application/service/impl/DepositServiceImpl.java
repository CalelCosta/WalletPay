package recargapay.wallet.application.service.impl;

import com.google.gson.Gson;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import recargapay.wallet.application.dto.request.DepositRequestDTO;
import recargapay.wallet.application.service.DepositService;
import recargapay.wallet.domain.exception.PersistenceException;
import recargapay.wallet.infra.model.ProcessedMessage;
import recargapay.wallet.infra.model.Transaction;
import recargapay.wallet.infra.model.User;
import recargapay.wallet.infra.model.Wallet;
import recargapay.wallet.infra.model.modelEnum.TransactionStatus;
import recargapay.wallet.infra.model.modelEnum.TransactionType;
import recargapay.wallet.infra.repository.ProcessedMessageRepository;
import recargapay.wallet.infra.repository.TransactionRepository;
import recargapay.wallet.infra.repository.UserRepository;
import recargapay.wallet.infra.repository.WalletRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static recargapay.wallet.domain.exception.ExceptionEnum.ERROR_PERSISTENCE;

@Service("depositService")
@Slf4j
public class DepositServiceImpl implements DepositService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final ProcessedMessageRepository processedMessageRepository;

    public DepositServiceImpl(TransactionRepository transactionRepository, WalletRepository walletRepository, UserRepository userRepository, ProcessedMessageRepository processedMessageRepository) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
        this.processedMessageRepository = processedMessageRepository;
    }

    @Override
    @KafkaListener(topics = "deposit-events", groupId = "wallet-group")
    @Transactional
    public void handleDeposit(String payload) {
        Gson gson = new Gson();
        DepositRequestDTO eventDepositDTO = gson.fromJson(payload, DepositRequestDTO.class);
        log.info("Received deposit event: {}", eventDepositDTO);
        if (processedMessageRepository.existsById(eventDepositDTO.getMessageId())) {
            log.warn("Duplicate message ignored: {}", eventDepositDTO.getMessageId());
            return;
        }
        Optional<Wallet> result = Optional.ofNullable(userRepository.findByCpf(eventDepositDTO.getCpf())).map(User::getWallet);
        buildTransactionAndSave(eventDepositDTO, result.get());
        try {
            result.ifPresent(wallet -> wallet.setBalance(wallet.getBalance().add(eventDepositDTO.getAmount())));
            walletRepository.saveAndFlush(result.get());
            processedMessageRepository.save(new ProcessedMessage(eventDepositDTO.getMessageId(), LocalDateTime.now()));
        } catch (RuntimeException e) {
            throw new PersistenceException(ERROR_PERSISTENCE.getMessage(), ERROR_PERSISTENCE.getStatusCode());
        }
    }

    private void buildTransactionAndSave(DepositRequestDTO eventDepositDTO, Wallet wallet) {
        Transaction newTransaction = Transaction.builder()
                .type(TransactionType.DEPOSIT)
                .wallet(wallet)
                .amount(eventDepositDTO.getAmount())
                .status(TransactionStatus.COMPLETED)
                .build();
        try {
            transactionRepository.saveAndFlush(newTransaction);
        } catch (RuntimeException e) {
            throw new PersistenceException(ERROR_PERSISTENCE.getMessage(), ERROR_PERSISTENCE.getStatusCode());
        }
    }
}

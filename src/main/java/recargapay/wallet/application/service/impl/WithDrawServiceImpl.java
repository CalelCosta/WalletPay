package recargapay.wallet.application.service.impl;

import com.google.gson.Gson;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import recargapay.wallet.application.dto.request.WithdrawRequestDTO;
import recargapay.wallet.application.service.WithdrawService;
import recargapay.wallet.domain.exception.BusinessException;
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
import static recargapay.wallet.domain.exception.ExceptionEnum.INSUFFICIENT_FUNDS;

@Service("withDrawService")
@Slf4j
public class WithDrawServiceImpl implements WithdrawService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final ProcessedMessageRepository processedMessageRepository;
    private final Gson gson;

    public WithDrawServiceImpl(TransactionRepository transactionRepository, WalletRepository walletRepository, UserRepository userRepository, ProcessedMessageRepository processedMessageRepository, Gson gson) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
        this.processedMessageRepository = processedMessageRepository;
        this.gson = gson;
    }

    @Override
    @KafkaListener(topics = "withdraw-events", groupId = "wallet-group")
    @Transactional
    public void handleWithdraw(String payload) {
        log.info("Received withdraw event...");
        WithdrawRequestDTO eventWithdrawDTO = gson.fromJson(payload, WithdrawRequestDTO.class);
        if (processedMessageRepository.existsById(eventWithdrawDTO.getMessageId())) {
            log.warn("Duplicate message ignored: {}", eventWithdrawDTO.getMessageId());
            return;
        }
        Optional<Wallet> result = Optional.ofNullable(userRepository.findByCpf(eventWithdrawDTO.getCpf())).map(User::getWallet);
        if (result.isPresent() && result.get().getBalance().compareTo(eventWithdrawDTO.getAmount()) >= 0) {
            Transaction newTransaction = Transaction.builder()
                    .type(TransactionType.WITHDRAW)
                    .wallet(result.get())
                    .amount(eventWithdrawDTO.getAmount())
                    .status(TransactionStatus.COMPLETED)
                    .build();
            getAndSaveTransaction(newTransaction);
        } else {
            throw new BusinessException(INSUFFICIENT_FUNDS.getMessage(),INSUFFICIENT_FUNDS.getStatusCode());
        }
        try {
            result.ifPresent(wallet -> wallet.setBalance(result.get().getBalance().subtract(eventWithdrawDTO.getAmount())));
            walletRepository.saveAndFlush(result.get());
            processedMessageRepository.save(new ProcessedMessage(eventWithdrawDTO.getMessageId(), LocalDateTime.now()));
        } catch (RuntimeException e) {
            throw new PersistenceException(ERROR_PERSISTENCE.getMessage(), ERROR_PERSISTENCE.getStatusCode());
        }
    }

    private void getAndSaveTransaction(Transaction transaction) {
        try {
            transactionRepository.saveAndFlush(transaction);
        } catch (RuntimeException e) {
            throw new PersistenceException(ERROR_PERSISTENCE.getMessage(), ERROR_PERSISTENCE.getStatusCode());
        }
    }
}

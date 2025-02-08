package recargapay.wallet.application.service.impl;

import com.google.gson.Gson;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import recargapay.wallet.application.dto.request.TransferRequestDTO;
import recargapay.wallet.application.service.TransferService;
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

@Service("transferService")
@Slf4j
public class TransferServiceImpl implements TransferService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final ProcessedMessageRepository processedMessageRepository;
    private final Gson gson;

    public TransferServiceImpl(TransactionRepository transactionRepository, WalletRepository walletRepository, UserRepository userRepository, ProcessedMessageRepository processedMessageRepository, Gson gson) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
        this.processedMessageRepository = processedMessageRepository;
        this.gson = gson;
    }

    @Override
    @KafkaListener(topics = "transfer-events", groupId = "wallet-group")
    @Transactional
    public void handleTransfer(String payload) {
        TransferRequestDTO transferRequestDTO = gson.fromJson(payload, TransferRequestDTO.class);
        log.info("Received transfer event: {}", transferRequestDTO);
        if (processedMessageRepository.existsById(transferRequestDTO.getMessageId())) {
            log.warn("Duplicate message ignored: {}", transferRequestDTO.getMessageId());
            return;
        }
        Optional<Wallet> sourceWallet = Optional.ofNullable(userRepository.findByCpf(transferRequestDTO.getFromCpf())).map(User::getWallet);
        Optional<Wallet> targetWallet = Optional.ofNullable(userRepository.findByCpf(transferRequestDTO.getToCpf())).map(User::getWallet);
        verifyAmountsInWalletAndMakeSourceTransaction(sourceWallet.get(), transferRequestDTO);
        addAmountInTargetWallet(transferRequestDTO, targetWallet.get());
        processedMessageRepository.save(new ProcessedMessage(transferRequestDTO.getMessageId(), LocalDateTime.now()));
    }

    private void verifyAmountsInWalletAndMakeSourceTransaction(Wallet soucerWallet, TransferRequestDTO transferRequestDTO) {
        try {
            if (soucerWallet.getBalance().compareTo(transferRequestDTO.getAmount()) >= 0){
                Transaction newTransaction = Transaction.builder()
                        .type(TransactionType.TRANSFER)
                        .wallet(soucerWallet)
                        .amount(transferRequestDTO.getAmount())
                        .status(TransactionStatus.COMPLETED)
                        .build();
                transactionRepository.saveAndFlush(newTransaction);
                soucerWallet.setBalance(soucerWallet.getBalance().subtract(transferRequestDTO.getAmount()));
                walletRepository.saveAndFlush(soucerWallet);
            } else {
                throw new BusinessException(INSUFFICIENT_FUNDS.getMessage(),INSUFFICIENT_FUNDS.getStatusCode());
            }
        } catch (RuntimeException e) {
            throw new PersistenceException(ERROR_PERSISTENCE.getMessage(), ERROR_PERSISTENCE.getStatusCode());
        }
    }

    private void addAmountInTargetWallet(TransferRequestDTO transferRequestDTO, Wallet targetWallet) {
        targetWallet.setBalance(targetWallet.getBalance().add(transferRequestDTO.getAmount()));
        Transaction targetTransaction = Transaction.builder()
                .type(TransactionType.RECEIVED)
                .wallet(targetWallet)
                .amount(transferRequestDTO.getAmount())
                .status(TransactionStatus.COMPLETED)
                .build();
        try {
            transactionRepository.saveAndFlush(targetTransaction);
            walletRepository.saveAndFlush(targetWallet);
        } catch (RuntimeException e) {
            throw new PersistenceException(ERROR_PERSISTENCE.getMessage(), ERROR_PERSISTENCE.getStatusCode());
        }
    }
}

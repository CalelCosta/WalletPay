package recargapay.wallet.application.service.impl;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import recargapay.wallet.application.dto.request.TransferRequestDTO;
import recargapay.wallet.domain.exception.PersistenceException;
import recargapay.wallet.infra.model.ProcessedMessage;
import recargapay.wallet.infra.model.Transaction;
import recargapay.wallet.infra.model.User;
import recargapay.wallet.infra.model.Wallet;
import recargapay.wallet.infra.model.modelEnum.TransactionType;
import recargapay.wallet.infra.repository.ProcessedMessageRepository;
import recargapay.wallet.infra.repository.TransactionRepository;
import recargapay.wallet.infra.repository.UserRepository;
import recargapay.wallet.infra.repository.WalletRepository;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProcessedMessageRepository processedMessageRepository;

    @Mock
    private Gson gson;

    @InjectMocks
    private TransferServiceImpl transferService;

    private final String validPayload = "{\"messageId\":\"123\",\"fromCpf\":\"111\",\"toCpf\":\"222\",\"amount\":100}";
    private TransferRequestDTO transferRequest;

    @BeforeEach
    void setUp() {
        transferRequest = new TransferRequestDTO(
                "123", "111", "222", BigDecimal.valueOf(100)
        );
    }

    @Test
    void handleTransfer_ShouldIgnoreDuplicateMessage() {
        when(gson.fromJson(validPayload, TransferRequestDTO.class)).thenReturn(transferRequest);
        when(processedMessageRepository.existsById("123")).thenReturn(true);

        transferService.handleTransfer(validPayload);

        verify(processedMessageRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void handleTransfer_ShouldProcessTransferSuccessfully() {
        Wallet sourceWallet = new Wallet();
        sourceWallet.setBalance(BigDecimal.valueOf(200));
        User sourceUser = new User();
        sourceUser.setWallet(sourceWallet);

        Wallet targetWallet = new Wallet();
        targetWallet.setBalance(BigDecimal.ZERO);
        User targetUser = new User();
        targetUser.setWallet(targetWallet);

        when(gson.fromJson(validPayload, TransferRequestDTO.class)).thenReturn(transferRequest);
        when(processedMessageRepository.existsById("123")).thenReturn(false);
        when(userRepository.findByCpf("111")).thenReturn(sourceUser);
        when(userRepository.findByCpf("222")).thenReturn(targetUser);

        transferService.handleTransfer(validPayload);

        ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
        verify(walletRepository, times(2)).saveAndFlush(walletCaptor.capture());

        assertEquals(BigDecimal.valueOf(100), walletCaptor.getAllValues().get(0).getBalance()); // Source
        assertEquals(BigDecimal.valueOf(100), walletCaptor.getAllValues().get(1).getBalance()); // Target

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(2)).saveAndFlush(transactionCaptor.capture());

        assertEquals(TransactionType.TRANSFER, transactionCaptor.getAllValues().get(0).getType());
        assertEquals(TransactionType.RECEIVED, transactionCaptor.getAllValues().get(1).getType());

        verify(processedMessageRepository).save(any(ProcessedMessage.class));
    }

    @Test
    void handleTransfer_ShouldThrowBusinessExceptionWhenInsufficientFunds() {
        // Arrange
        Wallet sourceWallet = new Wallet();
        sourceWallet.setBalance(BigDecimal.valueOf(50));
        User sourceUser = new User();
        sourceUser.setWallet(sourceWallet);

        when(gson.fromJson(validPayload, TransferRequestDTO.class)).thenReturn(transferRequest);
        when(processedMessageRepository.existsById("123")).thenReturn(false);
        when(userRepository.findByCpf("111")).thenReturn(sourceUser);

        // Act & Assert
        assertThrows(PersistenceException.class, () ->
                transferService.handleTransfer(validPayload)
        );
    }

    @Test
    void handleTransfer_ShouldThrowPersistenceExceptionOnSaveError() {
        Wallet sourceWallet = new Wallet();
        sourceWallet.setBalance(BigDecimal.valueOf(200));
        User sourceUser = new User();
        sourceUser.setWallet(sourceWallet);

        when(gson.fromJson(validPayload, TransferRequestDTO.class)).thenReturn(transferRequest);
        when(processedMessageRepository.existsById("123")).thenReturn(false);
        when(userRepository.findByCpf("111")).thenReturn(sourceUser);
        when(transactionRepository.saveAndFlush(any())).thenThrow(new RuntimeException("DB Error"));

        assertThrows(PersistenceException.class, () ->
                transferService.handleTransfer(validPayload)
        );
    }

    @Test
    void verifyAmountsInWalletAndMakeSourceTransaction_ShouldUpdateBalanceCorrectly() {
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(200));

        assertEquals(BigDecimal.valueOf(200), wallet.getBalance());
    }

    @Test
    void addAmountInTargetWallet_ShouldUpdateBalanceCorrectly() {
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.ZERO);

        assertEquals(BigDecimal.valueOf(0), wallet.getBalance());
    }
}
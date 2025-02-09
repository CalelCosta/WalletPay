package recargapay.wallet.application.service.impl;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import recargapay.wallet.application.dto.request.WithdrawRequestDTO;
import recargapay.wallet.domain.exception.BusinessException;
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
class WithDrawServiceImplTest {

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
    private WithDrawServiceImpl withdrawService;

    private final String validPayload = "{\"messageId\":\"123\",\"cpf\":\"111\",\"amount\":100}";
    private final WithdrawRequestDTO withdrawRequest = new WithdrawRequestDTO("123", "111", BigDecimal.valueOf(100));

    @Test
    void handleWithdraw_ShouldIgnoreDuplicateMessage() {
        when(gson.fromJson(validPayload, WithdrawRequestDTO.class)).thenReturn(withdrawRequest);
        when(processedMessageRepository.existsById("123")).thenReturn(true);

        withdrawService.handleWithdraw(validPayload);

        verify(transactionRepository, never()).saveAndFlush(any());
        verify(walletRepository, never()).saveAndFlush(any());
    }

    @Test
    void handleWithdraw_ShouldThrowBusinessExceptionWhenUserNotFound() {
        when(gson.fromJson(validPayload, WithdrawRequestDTO.class)).thenReturn(withdrawRequest);
        when(processedMessageRepository.existsById("123")).thenReturn(false);
        when(userRepository.findByCpf("111")).thenReturn(null); // Usuário não encontrado

        assertThrows(BusinessException.class, () ->
                withdrawService.handleWithdraw(validPayload)
        );
    }

    @Test
    void handleWithdraw_ShouldThrowBusinessExceptionWhenInsufficientFunds() {
        User user = new User();
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(50));
        user.setWallet(wallet);

        when(gson.fromJson(validPayload, WithdrawRequestDTO.class)).thenReturn(withdrawRequest);
        when(processedMessageRepository.existsById("123")).thenReturn(false);
        when(userRepository.findByCpf("111")).thenReturn(user);

        assertThrows(BusinessException.class, () ->
                withdrawService.handleWithdraw(validPayload)
        );
    }

    @Test
    void handleWithdraw_ShouldProcessWithdrawSuccessfully() {
        User user = new User();
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(200));
        user.setWallet(wallet);

        when(gson.fromJson(validPayload, WithdrawRequestDTO.class)).thenReturn(withdrawRequest);
        when(processedMessageRepository.existsById("123")).thenReturn(false);
        when(userRepository.findByCpf("111")).thenReturn(user);

        withdrawService.handleWithdraw(validPayload);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).saveAndFlush(transactionCaptor.capture());
        assertEquals(TransactionType.WITHDRAW, transactionCaptor.getValue().getType());
        assertEquals(BigDecimal.valueOf(100), transactionCaptor.getValue().getAmount());

        ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
        verify(walletRepository).saveAndFlush(walletCaptor.capture());
        assertEquals(BigDecimal.valueOf(100), walletCaptor.getValue().getBalance());

        verify(processedMessageRepository).save(any(ProcessedMessage.class));
    }

    @Test
    void handleWithdraw_ShouldThrowPersistenceExceptionWhenTransactionSaveFails() {
        User user = new User();
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(200));
        user.setWallet(wallet);

        when(gson.fromJson(validPayload, WithdrawRequestDTO.class)).thenReturn(withdrawRequest);
        when(processedMessageRepository.existsById("123")).thenReturn(false);
        when(userRepository.findByCpf("111")).thenReturn(user);
        when(transactionRepository.saveAndFlush(any())).thenThrow(new RuntimeException());

        assertThrows(PersistenceException.class, () ->
                withdrawService.handleWithdraw(validPayload)
        );
        verify(walletRepository, never()).saveAndFlush(any());
    }

    @Test
    void handleWithdraw_ShouldThrowPersistenceExceptionWhenWalletSaveFails() {
        User user = new User();
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(200));
        user.setWallet(wallet);

        when(gson.fromJson(validPayload, WithdrawRequestDTO.class)).thenReturn(withdrawRequest);
        when(processedMessageRepository.existsById("123")).thenReturn(false);
        when(userRepository.findByCpf("111")).thenReturn(user);
        when(walletRepository.saveAndFlush(any())).thenThrow(new RuntimeException());

        assertThrows(PersistenceException.class, () ->
                withdrawService.handleWithdraw(validPayload)
        );
        verify(transactionRepository).saveAndFlush(any());
        verify(processedMessageRepository, never()).save(any());
    }
}
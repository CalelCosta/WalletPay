package recargapay.wallet.application.service.impl;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import recargapay.wallet.application.dto.request.DepositRequestDTO;
import recargapay.wallet.domain.exception.PersistenceException;
import recargapay.wallet.infra.model.ProcessedMessage;
import recargapay.wallet.infra.model.Transaction;
import recargapay.wallet.infra.model.User;
import recargapay.wallet.infra.model.Wallet;
import recargapay.wallet.infra.repository.ProcessedMessageRepository;
import recargapay.wallet.infra.repository.TransactionRepository;
import recargapay.wallet.infra.repository.UserRepository;
import recargapay.wallet.infra.repository.WalletRepository;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DepositServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProcessedMessageRepository processedMessageRepository;

    @InjectMocks
    private DepositServiceImpl depositService;

    private Gson gson = new Gson();

    private DepositRequestDTO depositRequestDTO;

    @BeforeEach
    public void setUp() {
        depositRequestDTO = new DepositRequestDTO();
        depositRequestDTO.setCpf("12345678900");
        depositRequestDTO.setAmount(BigDecimal.valueOf(100));
        depositRequestDTO.setMessageId("msg-1");
    }

    @Test
    public void testHandleDeposit_NewMessage_Success() {
        String payload = gson.toJson(depositRequestDTO);
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(500));

        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setWallet(wallet);

        when(processedMessageRepository.existsById(depositRequestDTO.getMessageId())).thenReturn(false);
        when(userRepository.findByCpf(depositRequestDTO.getCpf())).thenReturn(user);

        depositService.handleDeposit(payload);

        verify(transactionRepository, times(1)).saveAndFlush(any(Transaction.class));
        verify(walletRepository, times(1)).saveAndFlush(wallet);
        verify(processedMessageRepository, times(1)).save(any(ProcessedMessage.class));

        assertEquals(BigDecimal.valueOf(600), wallet.getBalance());
    }

    @Test
    public void testHandleDeposit_DuplicateMessage_Ignored() {
        String payload = gson.toJson(depositRequestDTO);

        when(processedMessageRepository.existsById(depositRequestDTO.getMessageId())).thenReturn(true);

        depositService.handleDeposit(payload);

        verify(transactionRepository, never()).saveAndFlush(any(Transaction.class));
        verify(walletRepository, never()).saveAndFlush(any(Wallet.class));
        verify(processedMessageRepository, never()).save(any(ProcessedMessage.class));
    }

    @Test
    public void testHandleDeposit_PersistenceException_Thrown() {
        String payload = gson.toJson(depositRequestDTO);
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(500));

        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setWallet(wallet);

        when(processedMessageRepository.existsById(depositRequestDTO.getMessageId())).thenReturn(false);
        when(userRepository.findByCpf(depositRequestDTO.getCpf())).thenReturn(user);
        doThrow(new RuntimeException()).when(transactionRepository).saveAndFlush(any(Transaction.class));

        PersistenceException exception = assertThrows(PersistenceException.class, () -> {
            depositService.handleDeposit(payload);
        });

        assertEquals("Error on create new User and Wallet", exception.getMessage());
    }
}

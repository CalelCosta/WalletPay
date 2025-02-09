package recargapay.wallet.domain.usecase.impl;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import recargapay.wallet.application.dto.request.TransferRequestDTO;
import recargapay.wallet.domain.exception.BusinessException;
import recargapay.wallet.infra.model.User;
import recargapay.wallet.infra.model.Wallet;
import recargapay.wallet.infra.model.outbox.OutboxEvent;
import recargapay.wallet.infra.repository.OutboxRepository;
import recargapay.wallet.infra.repository.UserRepository;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferUseCaseImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private Gson gson;

    @InjectMocks
    private TransferUseCaseImpl transferUseCase;

    private TransferRequestDTO transferRequestDTO;
    private User sourceUser;
    private User targetUser;
    private Wallet sourceWallet;
    private Wallet targetWallet;

    @BeforeEach
    void setUp() {
        transferRequestDTO = new TransferRequestDTO();
        transferRequestDTO.setFromCpf("12345678900");
        transferRequestDTO.setToCpf("09876543211");
        transferRequestDTO.setAmount(BigDecimal.valueOf(100));

        sourceWallet = new Wallet();
        sourceWallet.setId(123456L);
        sourceWallet.setBalance(BigDecimal.valueOf(500));

        targetWallet = new Wallet();
        targetWallet.setId(1234567L);
        targetWallet.setBalance(BigDecimal.valueOf(300));

        sourceUser = new User();
        sourceUser.setCpf("12345678900");
        sourceUser.setWallet(sourceWallet);

        targetUser = new User();
        targetUser.setCpf("09876543211");
        targetUser.setWallet(targetWallet);
    }

    @Test
    void execute_AmountNotValid_ThrowsBusinessException() {
        transferRequestDTO.setAmount(BigDecimal.ZERO);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            transferUseCase.execute(transferRequestDTO);
        });

        assertEquals("Deposit amount must be positive", exception.getMessage());
    }

    @Test
    void execute_UserNotFound_ThrowsBusinessException() {
        when(userRepository.findByCpf("12345678900")).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            transferUseCase.execute(transferRequestDTO);
        });

        assertEquals("No one balance found with informed date", exception.getMessage());
    }
}

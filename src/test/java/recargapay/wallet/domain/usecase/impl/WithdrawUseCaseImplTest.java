package recargapay.wallet.domain.usecase.impl;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import recargapay.wallet.application.dto.request.WithdrawRequestDTO;
import recargapay.wallet.domain.exception.BusinessException;
import recargapay.wallet.infra.model.User;
import recargapay.wallet.infra.model.Wallet;
import recargapay.wallet.infra.model.outbox.OutboxEvent;
import recargapay.wallet.infra.repository.OutboxRepository;
import recargapay.wallet.infra.repository.UserRepository;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WithdrawUseCaseImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private Gson gson;

    @InjectMocks
    private WithdrawUseCaseImpl withdrawUseCase;

    private WithdrawRequestDTO withdrawRequestDTO;
    private User user;

    @BeforeEach
    void setUp() {
        withdrawRequestDTO = new WithdrawRequestDTO();
        withdrawRequestDTO.setCpf("12345678900");
        withdrawRequestDTO.setAmount(BigDecimal.valueOf(100));

        Wallet wallet = new Wallet();
        wallet.setId(1232456L);
        wallet.setBalance(BigDecimal.valueOf(500));

        user = new User();
        user.setCpf("12345678900");
        user.setWallet(wallet);
    }

    @Test
    void execute_ShouldSaveWithdrawEvent_WhenValidRequest() {
        when(userRepository.findByCpf(withdrawRequestDTO.getCpf())).thenReturn(user);
        when(gson.toJson(withdrawRequestDTO)).thenReturn("mockedPayload");

        withdrawUseCase.execute(withdrawRequestDTO);

        assertNotNull(withdrawRequestDTO.getMessageId());
        verify(outboxRepository, times(1)).saveAndFlush(any(OutboxEvent.class));
    }

    @Test
    void execute_ShouldThrowBusinessException_WhenAmountIsInvalid() {
        withdrawRequestDTO.setAmount(BigDecimal.ZERO);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            withdrawUseCase.execute(withdrawRequestDTO);
        });

        assertEquals("Deposit amount must be positive", exception.getMessage());
        verify(outboxRepository, never()).saveAndFlush(any(OutboxEvent.class));
    }

    @Test
    void execute_ShouldThrowBusinessException_WhenUserNotFound() {
        when(userRepository.findByCpf(withdrawRequestDTO.getCpf())).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            withdrawUseCase.execute(withdrawRequestDTO);
        });

        assertEquals("No one balance found with informed date", exception.getMessage());
        verify(outboxRepository, never()).saveAndFlush(any(OutboxEvent.class));
    }
}

package recargapay.wallet.domain.usecase.impl;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import recargapay.wallet.application.dto.request.DepositRequestDTO;
import recargapay.wallet.domain.exception.BusinessException;
import recargapay.wallet.infra.repository.OutboxRepository;
import recargapay.wallet.infra.repository.UserRepository;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class DepositUseCaseImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private Gson gson;

    @InjectMocks
    private DepositUseCaseImpl depositUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void execute_InvalidAmount_ThrowsBusinessException() {
        DepositRequestDTO requestDTO = new DepositRequestDTO();
        requestDTO.setCpf("12345678900");
        requestDTO.setAmount(BigDecimal.valueOf(-100));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            depositUseCase.execute(requestDTO);
        });
        assertEquals("Deposit amount must be positive", exception.getMessage());
    }

    @Test
    void execute_UserNotFound_ThrowsBusinessException() {
        DepositRequestDTO requestDTO = new DepositRequestDTO();
        requestDTO.setCpf("12345678900");
        requestDTO.setAmount(BigDecimal.valueOf(100));
        when(userRepository.findByCpf(requestDTO.getCpf())).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            depositUseCase.execute(requestDTO);
        });
        assertEquals("No one balance found with informed date", exception.getMessage());
    }
}

package recargapay.wallet.domain.usecase.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import recargapay.wallet.application.dto.request.CreateWalletRequestDTO;
import recargapay.wallet.application.dto.response.WalletResponseDTO;
import recargapay.wallet.application.service.WalletService;
import recargapay.wallet.domain.exception.BusinessException;
import recargapay.wallet.infra.model.User;
import recargapay.wallet.infra.model.Wallet;
import recargapay.wallet.infra.repository.UserRepository;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateWalletUseCaseImplTest {

    @Mock
    private WalletService walletService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CreateWalletUseCaseImpl createWalletUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createWallet_NewUser_CreatesWalletAndUser() {
        CreateWalletRequestDTO requestDTO = new CreateWalletRequestDTO();
        requestDTO.setCpf("12345678900");
        when(userRepository.findByCpf(requestDTO.getCpf())).thenReturn(null);
        WalletResponseDTO expectedResponse = new WalletResponseDTO("teste", "user", "email@email.com", BigDecimal.ZERO, "BRL");
        when(walletService.createWalletAndNewUser(requestDTO)).thenReturn(expectedResponse);

        WalletResponseDTO actualResponse = createWalletUseCase.createWallet(requestDTO);

        assertEquals(expectedResponse, actualResponse);
        verify(walletService, times(1)).createWalletAndNewUser(requestDTO);
    }

    @Test
    void createWallet_ExistingUserWithoutWallet_CreatesWallet() {
        CreateWalletRequestDTO requestDTO = new CreateWalletRequestDTO();
        requestDTO.setCpf("12345678900");
        User existingUser = new User();
        when(userRepository.findByCpf(requestDTO.getCpf())).thenReturn(existingUser);
        WalletResponseDTO expectedResponse = new WalletResponseDTO("teste", "user", "email@email.com", BigDecimal.ZERO, "BRL");
        when(walletService.createWallet(requestDTO, existingUser)).thenReturn(expectedResponse);

        WalletResponseDTO actualResponse = createWalletUseCase.createWallet(requestDTO);

        assertEquals(expectedResponse, actualResponse);
        verify(walletService, times(1)).createWallet(requestDTO, existingUser);
    }

    @Test
    void createWallet_ExistingUserWithWallet_ThrowsBusinessException() {
        CreateWalletRequestDTO requestDTO = new CreateWalletRequestDTO();
        requestDTO.setCpf("12345678900");
        User existingUser = new User();
        existingUser.setWallet(new Wallet());
        when(userRepository.findByCpf(requestDTO.getCpf())).thenReturn(existingUser);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            createWalletUseCase.createWallet(requestDTO);
        });
        assertEquals("There is already a Wallet for the CPF informed", exception.getMessage());
    }
}

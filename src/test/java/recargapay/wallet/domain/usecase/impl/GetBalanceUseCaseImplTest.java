package recargapay.wallet.domain.usecase.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import recargapay.wallet.application.dto.response.BalanceResponseDTO;
import recargapay.wallet.application.service.WalletService;
import recargapay.wallet.domain.exception.BusinessException;
import recargapay.wallet.infra.model.User;
import recargapay.wallet.infra.model.Wallet;
import recargapay.wallet.infra.repository.UserRepository;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetBalanceUseCaseImplTest {

    @Mock
    private WalletService walletService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GetBalanceUseCaseImpl getBalanceUseCase;

    @Test
    void getBalance_ShouldReturnCurrentDateBalanceWhenDateIs2099_12_31() {
        // Arrange
        long userId = 1L;
        String date = "2099-12-31";
        Pageable pageable = Pageable.unpaged();
        Wallet wallet = new Wallet();
        User user = new User();
        user.setWallet(wallet);
        when(userRepository.findById(Math.toIntExact(userId))).thenReturn(Optional.of(user));
        when(walletService.getAllBalanceWithCurrentDate(pageable, wallet, LocalDate.now().toString()))
                .thenReturn(Page.empty());

        // Act
        Page<BalanceResponseDTO> result = getBalanceUseCase.getBalance(pageable, userId, date);

        // Assert
        assertNotNull(result);
        verify(walletService).getAllBalanceWithCurrentDate(pageable, wallet, LocalDate.now().toString());
    }

    @Test
    void getBalance_ShouldReturnFilteredBalanceWhenDateIsValid() {
        // Arrange
        long userId = 1L;
        String date = "2023-10-01";
        Pageable pageable = Pageable.unpaged();
        Wallet wallet = new Wallet();
        User user = new User();
        user.setWallet(wallet);
        when(userRepository.findById(Math.toIntExact(userId))).thenReturn(Optional.of(user));
        when(walletService.getBalanceWithFilter(pageable, wallet, date)).thenReturn(Page.empty());

        // Act
        Page<BalanceResponseDTO> result = getBalanceUseCase.getBalance(pageable, userId, date);

        // Assert
        assertNotNull(result);
        verify(walletService).getBalanceWithFilter(pageable, wallet, date);
    }

    @Test
    void getBalance_ShouldThrowBusinessExceptionWhenDateIsInvalid() {
        // Arrange
        long userId = 1L;
        String invalidDate = "2023/10/01"; // Formato inválido
        Pageable pageable = Pageable.unpaged();
        Wallet wallet = new Wallet();
        User user = new User();
        user.setWallet(wallet);
        when(userRepository.findById(Math.toIntExact(userId))).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(BusinessException.class, () ->
                getBalanceUseCase.getBalance(pageable, userId, invalidDate)
        );
    }

    @Test
    void getBalance_ShouldThrowBusinessExceptionWhenUserNotFound() {
        // Arrange
        long userId = 1L;
        String date = "2023-10-01";
        Pageable pageable = Pageable.unpaged();
        when(userRepository.findById(Math.toIntExact(userId))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () ->
                getBalanceUseCase.getBalance(pageable, userId, date)
        );
    }

    @Test
    void getBalance_ShouldThrowBusinessExceptionWhenWalletNotFound() {
        // Arrange
        long userId = 1L;
        String date = "2023-10-01";
        Pageable pageable = Pageable.unpaged();
        User user = new User();
        user.setWallet(null); // Carteira não encontrada
        when(userRepository.findById(Math.toIntExact(userId))).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(NoSuchElementException.class, () ->
                getBalanceUseCase.getBalance(pageable, userId, date)
        );
    }
}
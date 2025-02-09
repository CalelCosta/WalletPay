package recargapay.wallet.application.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import recargapay.wallet.application.dto.request.CreateWalletRequestDTO;
import recargapay.wallet.application.dto.response.BalanceResponseDTO;
import recargapay.wallet.domain.exception.PersistenceException;
import recargapay.wallet.domain.mapper.WalletMapper;
import recargapay.wallet.infra.model.User;
import recargapay.wallet.infra.model.Wallet;
import recargapay.wallet.infra.repository.UserRepository;
import recargapay.wallet.infra.repository.WalletRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletMapper walletMapper;

    @InjectMocks
    private WalletServiceImpl walletService;

    private final CreateWalletRequestDTO sampleRequest = new CreateWalletRequestDTO(
            "john_doe", "12345678900", "john@example.com", "password123", "BRL"
    );

    @Test
    void createWallet_ShouldThrowPersistenceExceptionOnFailure() {
        User user = new User();
        when(walletRepository.saveAndFlush(any())).thenThrow(new RuntimeException());

        assertThrows(PersistenceException.class, () ->
                walletService.createWallet(sampleRequest, user)
        );
    }

    @Test
    void createWalletAndNewUser_ShouldThrowWhenUserSaveFails() {
        when(userRepository.saveAndFlush(any())).thenThrow(new RuntimeException());

        assertThrows(PersistenceException.class, () ->
                walletService.createWalletAndNewUser(sampleRequest)
        );
    }

    @Test
    void createWalletAndNewUser_ShouldThrowWhenWalletSaveFails() {
        when(userRepository.saveAndFlush(any())).thenReturn(new User());
        when(walletRepository.saveAndFlush(any())).thenThrow(new RuntimeException());

        assertThrows(PersistenceException.class, () ->
                walletService.createWalletAndNewUser(sampleRequest)
        );
    }

    @Test
    void getBalanceWithFilter_ShouldFilterWalletsBeforeDate() {
        Wallet wallet = new Wallet();
        wallet.setCreatedAt(LocalDate.parse("2023-01-15"));
        Pageable pageable = Pageable.unpaged();
        when(walletMapper.toListDto(anyList())).thenReturn(List.of(new BalanceResponseDTO()));

        Page<BalanceResponseDTO> result = walletService.getBalanceWithFilter(
                pageable, wallet, "2023-02-01"
        );

        assertEquals(1, result.getContent().size());
    }

    @Test
    void getBalanceWithFilter_ShouldExcludeWalletsAfterDate() {
        Wallet wallet = new Wallet();
        wallet.setCreatedAt(LocalDate.parse("2023-03-01"));
        Pageable pageable = Pageable.unpaged();

        Page<BalanceResponseDTO> result = walletService.getBalanceWithFilter(
                pageable, wallet, "2023-02-01"
        );

        assertTrue(result.getContent().isEmpty());
    }

    @Test
    void getAllBalanceWithCurrentDate_ShouldFilterWalletsOnExactDate() {
        Wallet wallet = new Wallet();
        LocalDate today = LocalDate.now();
        wallet.setCreatedAt(today);
        Pageable pageable = Pageable.unpaged();
        when(walletMapper.toListDto(anyList())).thenReturn(List.of(new BalanceResponseDTO()));

        Page<BalanceResponseDTO> result = walletService.getAllBalanceWithCurrentDate(
                pageable, wallet, today.toString()
        );

        assertEquals(1, result.getContent().size());
    }

    @Test
    void getAllBalanceWithCurrentDate_ShouldExcludeWalletsNotMatchingDate() {
        Wallet wallet = new Wallet();
        wallet.setCreatedAt(LocalDate.parse("2023-01-01"));
        Pageable pageable = Pageable.unpaged();

        Page<BalanceResponseDTO> result = walletService.getAllBalanceWithCurrentDate(
                pageable, wallet, "2023-02-01"
        );

        assertTrue(result.getContent().isEmpty());
    }
}
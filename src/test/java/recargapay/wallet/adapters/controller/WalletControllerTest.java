package recargapay.wallet.adapters.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.mockito.junit.jupiter.MockitoExtension;

import recargapay.wallet.application.dto.request.CreateWalletRequestDTO;
import recargapay.wallet.application.dto.request.DepositRequestDTO;
import recargapay.wallet.application.dto.request.TransferRequestDTO;
import recargapay.wallet.application.dto.request.WithdrawRequestDTO;
import recargapay.wallet.application.dto.response.BalanceResponseDTO;
import recargapay.wallet.application.dto.response.DepositResponseDTO;
import recargapay.wallet.application.dto.response.TransferResponseDTO;
import recargapay.wallet.application.dto.response.WalletResponseDTO;
import recargapay.wallet.application.dto.response.WithdrawResponseDTO;
import recargapay.wallet.domain.usecase.CreateWalletUseCase;
import recargapay.wallet.domain.usecase.DepositUseCase;
import recargapay.wallet.domain.usecase.GetBalanceUseCase;
import recargapay.wallet.domain.usecase.TransferUseCase;
import recargapay.wallet.domain.usecase.WithdrawUseCase;

@ExtendWith(MockitoExtension.class)
public class WalletControllerTest {

    @InjectMocks
    private WalletController walletController;

    @Mock
    private CreateWalletUseCase createWalletUseCase;

    @Mock
    private GetBalanceUseCase getBalanceUseCase;

    @Mock
    private DepositUseCase depositUseCase;

    @Mock
    private WithdrawUseCase withdrawUseCase;

    @Mock
    private TransferUseCase transferUseCase;

    @Test
    public void testCreateWallet() {
        CreateWalletRequestDTO requestDTO = new CreateWalletRequestDTO();
        requestDTO.setUsername("testUser");
        requestDTO.setPassword("password");
        requestDTO.setEmail("test@example.com");
        requestDTO.setCurrency("BRL");
        requestDTO.setCpf("123.456.789-10");

        WalletResponseDTO walletResponseDTO = new WalletResponseDTO("teste", "user", "email@email.com", BigDecimal.ZERO, "BRL");
        when(createWalletUseCase.createWallet(requestDTO)).thenReturn(walletResponseDTO);
        ResponseEntity<WalletResponseDTO> response = walletController.createWallet(requestDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(walletResponseDTO, response.getBody());
        verify(createWalletUseCase, times(1)).createWallet(requestDTO);
    }

    @Test
    public void testGetBalance() {
        Pageable pageable = Pageable.unpaged();
        Long userId = 1L;
        String date = "2099-12-31";
        BalanceResponseDTO balanceDTO = new BalanceResponseDTO();
        balanceDTO.setBalance(BigDecimal.valueOf(100.00));
        Page<BalanceResponseDTO> page = new PageImpl<>(Arrays.asList(balanceDTO));

        when(getBalanceUseCase.getBalance(pageable, userId, date)).thenReturn(page);
        ResponseEntity<Page<BalanceResponseDTO>> response = walletController.getBalance(pageable, userId, date);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
        verify(getBalanceUseCase, times(1)).getBalance(pageable, userId, date);
    }

    @Test
    public void testCreateDeposit() {
        DepositRequestDTO depositRequestDTO = new DepositRequestDTO();
        depositRequestDTO.setAmount(BigDecimal.valueOf(50.00));
        doNothing().when(depositUseCase).execute(depositRequestDTO);
        ResponseEntity<DepositResponseDTO> response = walletController.createDeposit(depositRequestDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("deposit successfully completed", response.getBody().message());
        assertEquals(depositRequestDTO.getAmount(), response.getBody().amount());
        verify(depositUseCase, times(1)).execute(depositRequestDTO);
    }

    @Test
    public void testWithdrawMoney() {
        WithdrawRequestDTO withdrawRequestDTO = new WithdrawRequestDTO();
        withdrawRequestDTO.setAmount(BigDecimal.valueOf(30.00));
        doNothing().when(withdrawUseCase).execute(withdrawRequestDTO);

        ResponseEntity<WithdrawResponseDTO> response = walletController.withdrawMoney(withdrawRequestDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("successful withdrawal", response.getBody().message());
        assertEquals(withdrawRequestDTO.getAmount(), response.getBody().amount());
        verify(withdrawUseCase, times(1)).execute(withdrawRequestDTO);
    }

    @Test
    public void testTransferMoney() {
        TransferRequestDTO transferRequestDTO = new TransferRequestDTO();
        transferRequestDTO.setFromCpf("123.456.789-10");
        transferRequestDTO.setToCpf("987.654.321-00");
        transferRequestDTO.setAmount(BigDecimal.valueOf(75.00));
        doNothing().when(transferUseCase).execute(transferRequestDTO);

        ResponseEntity<TransferResponseDTO> response = walletController.transferMoney(transferRequestDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("transafer performed successfully", response.getBody().message());
        assertEquals(transferRequestDTO.getFromCpf(), response.getBody().sender());
        assertEquals(transferRequestDTO.getToCpf(), response.getBody().receiver());
        assertEquals(transferRequestDTO.getAmount(), response.getBody().amount());
        verify(transferUseCase, times(1)).execute(transferRequestDTO);
    }
}

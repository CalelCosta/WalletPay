package recargapay.wallet.adapters.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recargapay.wallet.application.dto.request.CreateWalletRequestDTO;
import recargapay.wallet.application.dto.response.*;
import recargapay.wallet.domain.usecase.CreateWalletUseCase;

@OpenAPIDefinition(
        info = @Info(title = "Wallet Service API", version = "1.0", description = "API for digital wallet management")
)
@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final CreateWalletUseCase createWalletUseCase;

    public WalletController(CreateWalletUseCase createWalletUseCase) {
        this.createWalletUseCase = createWalletUseCase;
    }

    @PostMapping
    public ResponseEntity<WalletResponseDTO> createWallet(@RequestBody CreateWalletRequestDTO walletRequestDTO) {
        createWalletUseCase.createWallet(walletRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{userId}/balance")
    public ResponseEntity<BalanceResponseDTO> getBalance(Pageable pageable,
                                                         @PathVariable Long userId,
                                                         @RequestParam(name = "date", required = false) String date) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/deposit")
    public ResponseEntity<DepositResponseDTO> createDeposit(@PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{userId}/withdraw")
    public ResponseEntity<WithdrawResponseDTO> withdrawMoney(@PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{fromId}/transfer/{toId}")
    public ResponseEntity<TransferResponseDTO> transferMoney(@PathVariable Long fromId, @PathVariable Long toId) {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

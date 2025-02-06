package recargapay.wallet.adapters.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recargapay.wallet.application.dto.response.*;

@OpenAPIDefinition(
        info = @Info(title = "Wallet Service API", version = "1.0", description = "API for digital wallet management")
)
@RestController
@RequestMapping("/wallets")
public class WalletController {

    @PostMapping
    public ResponseEntity<WalletResponse> createWallet() {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{userId}/balance")
    public ResponseEntity<BalanceResponse> getBalance(Pageable pageable, @PathVariable Long userId, @RequestParam String date) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/deposit")
    public ResponseEntity<DepositResponse> createDeposit(@PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{userId}/withdraw")
    public ResponseEntity<WithdrawResponse> withdrawMoney(@PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{fromId}/transfer/{toId}")
    public ResponseEntity<TransferResponse> transferMoney(@PathVariable Long fromId, @PathVariable Long toId) {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

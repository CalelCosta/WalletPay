package recargapay.wallet.adapters.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recargapay.wallet.application.dto.request.CreateWalletRequestDTO;
import recargapay.wallet.application.dto.response.*;
import recargapay.wallet.domain.usecase.CreateWalletUseCase;
import recargapay.wallet.domain.usecase.GetBalanceUseCase;

@OpenAPIDefinition(
        info = @Info(title = "Wallet Service API", version = "1.0", description = "API for digital wallet management")
)
@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final CreateWalletUseCase createWalletUseCase;
    private final GetBalanceUseCase getBalanceUseCase;

    public WalletController(CreateWalletUseCase createWalletUseCase, GetBalanceUseCase getBalanceUseCase) {
        this.createWalletUseCase = createWalletUseCase;
        this.getBalanceUseCase = getBalanceUseCase;
    }

    @Operation(summary = "Create a Wallet", description = "Endpoint for creating a new digital wallet for the user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Wallet Created Successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WalletResponseDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid Input Data",
                    content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class)) })
    })
    @PostMapping
    public ResponseEntity<WalletResponseDTO> createWallet(@Valid @RequestBody CreateWalletRequestDTO walletRequestDTO) {
        return new ResponseEntity<>(createWalletUseCase.createWallet(walletRequestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Consult Wallet Balance", description = "Endpoint for consulting User wallet balance.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BalanceResponseDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Invalid Input Data",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)) })
    })
    @GetMapping("/{userId}/balance")
    public ResponseEntity<Page<BalanceResponseDTO>> getBalance(Pageable pageable,
                                               @PathVariable Long userId,
                                               @RequestParam(name = "date", required = false
                                                       , defaultValue = "2099-12-31") String date) {
        return ResponseEntity.ok(getBalanceUseCase.getBalance(pageable, userId, date));
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

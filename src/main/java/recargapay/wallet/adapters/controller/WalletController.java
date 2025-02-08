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
import recargapay.wallet.application.dto.request.DepositRequestDTO;
import recargapay.wallet.application.dto.request.TransferRequestDTO;
import recargapay.wallet.application.dto.request.WithdrawRequestDTO;
import recargapay.wallet.application.dto.response.*;
import recargapay.wallet.domain.usecase.*;

@OpenAPIDefinition(
        info = @Info(title = "Wallet Service API", version = "1.0", description = "API for digital wallet management")
)
@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final CreateWalletUseCase createWalletUseCase;
    private final GetBalanceUseCase getBalanceUseCase;
    private final DepositUseCase depositUseCase;
    private final WithdrawUseCase withdrawUseCase;
    private final TransferUseCase transferUseCase;

    public WalletController(CreateWalletUseCase createWalletUseCase, GetBalanceUseCase getBalanceUseCase, DepositUseCase depositUseCase, WithdrawUseCase withdrawUseCase, TransferUseCase transferUseCase) {
        this.createWalletUseCase = createWalletUseCase;
        this.getBalanceUseCase = getBalanceUseCase;
        this.depositUseCase = depositUseCase;
        this.withdrawUseCase = withdrawUseCase;
        this.transferUseCase = transferUseCase;
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

    @Operation(summary = "Makes a Deposit", description = "Endpoint for make a deposit.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "OK",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DepositResponseDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid Input Data",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)) })
    })
    @PostMapping("/deposit")
    public ResponseEntity<DepositResponseDTO> createDeposit(@Valid @RequestBody DepositRequestDTO depositRequestDTO) {
        depositUseCase.execute(depositRequestDTO);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @Operation(summary = "Makes a Withdraw", description = "Endpoint for make a withdraw.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "OK",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WithdrawResponseDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid Input Data",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)) })
    })
    @PostMapping("/withdraw")
    public ResponseEntity<WithdrawResponseDTO> withdrawMoney(@Valid @RequestBody WithdrawRequestDTO withdrawRequestDTO) {
        withdrawUseCase.execute(withdrawRequestDTO);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @Operation(summary = "Makes a Transfer", description = "Endpoint for make a money transfer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "OK",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransferResponseDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid Input Data",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)) })
    })
    @PostMapping("/transfer")
    public ResponseEntity<TransferResponseDTO> transferMoney(@Valid @RequestBody TransferRequestDTO transferRequestDTO) {
        transferUseCase.execute(transferRequestDTO);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}

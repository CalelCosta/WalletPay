package recargapay.wallet.application.dto.response;

import java.math.BigDecimal;

public record WalletResponseDTO(String message, String username, String email, BigDecimal balance, String currency) {
}

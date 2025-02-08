package recargapay.wallet.application.dto.response;

import java.math.BigDecimal;

public record TransferResponseDTO(String message, String sender, String receiver, BigDecimal amount) {
}

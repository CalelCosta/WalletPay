package recargapay.wallet.application.dto.response;

import java.math.BigDecimal;

public record DepositResponseDTO(String message, BigDecimal amount) {
}

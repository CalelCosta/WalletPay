package recargapay.wallet.application.dto.response;

import java.math.BigDecimal;

public record BalanceResponseDTO(BigDecimal balance, String currency) {
}

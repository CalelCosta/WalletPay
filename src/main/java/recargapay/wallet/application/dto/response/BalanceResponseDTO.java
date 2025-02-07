package recargapay.wallet.application.dto.response;

import java.math.BigDecimal;

public record BalanceResponseDTO(String date, BigDecimal balance, String currency) {
}

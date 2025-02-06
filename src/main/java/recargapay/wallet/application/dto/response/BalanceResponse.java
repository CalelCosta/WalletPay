package recargapay.wallet.application.dto.response;

import java.math.BigDecimal;

public record BalanceResponse(BigDecimal balance, String currency) {
}

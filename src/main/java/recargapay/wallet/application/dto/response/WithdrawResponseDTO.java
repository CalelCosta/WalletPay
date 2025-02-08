package recargapay.wallet.application.dto.response;

import java.math.BigDecimal;

public record WithdrawResponseDTO(String message, BigDecimal amount) {
}

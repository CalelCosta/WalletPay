package recargapay.wallet.application.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Getter
@Setter
public class BalanceResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String date;
    private BigDecimal balance;
    private String currency;

}

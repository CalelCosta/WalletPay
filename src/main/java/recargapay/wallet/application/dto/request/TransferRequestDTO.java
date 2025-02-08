package recargapay.wallet.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class TransferRequestDTO implements Serializable {

    private String messageId;

    @JsonProperty
    @NotNull
    @Max(value = 14)
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")
    private String fromCpf;

    @JsonProperty
    @NotNull
    @Max(value = 14)
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")
    private String toCpf;

    @JsonProperty
    @NotNull
    BigDecimal amount;

}

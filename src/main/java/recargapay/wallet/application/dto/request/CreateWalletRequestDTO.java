package recargapay.wallet.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateWalletRequestDTO {

    @JsonProperty
    @NotNull
    @NotBlank
    private String username;

    @JsonProperty
    @NotNull
    @NotBlank
    private String password;

    @JsonProperty
    @NotNull
    @NotBlank
    private String email;

    @JsonProperty
    @Max(value = 3)
    private String currency;

    @JsonProperty
    @NotNull
    @Max(value = 14)
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")
    private String cpf;
}

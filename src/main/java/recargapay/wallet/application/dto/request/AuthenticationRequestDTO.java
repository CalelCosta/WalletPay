package recargapay.wallet.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthenticationRequestDTO {

    @JsonProperty
    @NotNull
    private String username;

    @JsonProperty
    @NotNull
    private String password;
}

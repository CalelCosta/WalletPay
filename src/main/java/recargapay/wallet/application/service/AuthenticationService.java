package recargapay.wallet.application.service;

import org.springframework.http.ResponseEntity;
import recargapay.wallet.application.dto.request.AuthenticationRequestDTO;
import recargapay.wallet.application.dto.response.AuthenticationResponseDTO;

public interface AuthenticationService {

    ResponseEntity<AuthenticationResponseDTO> createAuthenticationToken(AuthenticationRequestDTO authenticationRequestDTO);
}

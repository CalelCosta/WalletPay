package recargapay.wallet.adapters.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import recargapay.wallet.application.dto.request.AuthenticationRequestDTO;
import recargapay.wallet.application.dto.response.AuthenticationResponseDTO;
import recargapay.wallet.application.service.AuthenticationService;
import recargapay.wallet.application.service.impl.AuthenticationServiceImpl;

@RestController
@Slf4j
public class AuthenticationController {

    AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationServiceImpl authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDTO> createAuthenticationToken(
            @RequestBody AuthenticationRequestDTO authenticationRequestDTO) {
        log.info("Creating authentication token");
        return authenticationService.createAuthenticationToken(authenticationRequestDTO);
    }
}

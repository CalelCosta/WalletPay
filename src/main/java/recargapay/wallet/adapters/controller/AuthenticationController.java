package recargapay.wallet.adapters.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import recargapay.wallet.application.dto.request.AuthenticationRequest;
import recargapay.wallet.application.dto.response.AuthenticationResponseDTO;
import recargapay.wallet.application.service.AuthenticationService;
import recargapay.wallet.application.service.impl.AuthenticationServiceImpl;

@OpenAPIDefinition(
        info = @Info(title = "Wallet Authentication", version = "1.0", description = "Controller to Authenticate User and generate an valid Token")
)
@RestController
@Slf4j
public class AuthenticationController {

    AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationServiceImpl authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDTO> createAuthenticationToken(
            @RequestBody AuthenticationRequest authenticationRequest) {
        log.info("Creating authentication token");
        return authenticationService.createAuthenticationToken(authenticationRequest);
    }
}

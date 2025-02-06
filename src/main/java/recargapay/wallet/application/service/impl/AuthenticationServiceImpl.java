package recargapay.wallet.application.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import recargapay.wallet.application.dto.request.AuthenticationRequestDTO;
import recargapay.wallet.application.dto.response.AuthenticationResponseDTO;
import recargapay.wallet.application.service.AuthenticationService;
import recargapay.wallet.infra.security.JwtCacheService;
import recargapay.wallet.infra.security.JwtUtil;

@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    private final JwtCacheService jwtCacheService;

    private final UserDetailsService userDetailsService;

    @Value("${jwt.expiration}")
    private Long expiration;

    public AuthenticationServiceImpl(AuthenticationManager authenticationManager, JwtUtil jwtUtil, JwtCacheService jwtCacheService, UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.jwtCacheService = jwtCacheService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public ResponseEntity<AuthenticationResponseDTO> createAuthenticationToken(AuthenticationRequestDTO authenticationRequestDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequestDTO.username(), authenticationRequestDTO.password())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequestDTO.username());
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());

        jwtCacheService.cacheJwt(userDetails.getUsername(), jwt);

        log.info("JWT generated");

        return ResponseEntity.ok(new AuthenticationResponseDTO(jwt, "Bearer", expiration.intValue()));
    }
}

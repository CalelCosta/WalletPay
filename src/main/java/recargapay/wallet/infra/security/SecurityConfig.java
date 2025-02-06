package recargapay.wallet.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    private final JwtCacheService jwtCacheService;

    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService, JwtUtil jwtUtil, JwtCacheService jwtCacheService) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.jwtCacheService = jwtCacheService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Desabilita CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/authenticate").permitAll() // Permite acesso público ao endpoint de autenticação
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Apenas usuários com ROLE_ADMIN podem acessar rotas /admin/**
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN") // Usuários com ROLE_USER ou ROLE_ADMIN podem acessar rotas /user/**
                        .anyRequest().authenticated() // Exige autenticação para todos os outros endpoints
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Configura sessões como stateless
                )
                .addFilterBefore(new JwtRequestFilter(jwtUtil, jwtCacheService, userDetailsService), UsernamePasswordAuthenticationFilter.class); // Adiciona o filtro JWT

        return http.build();
    }
}
package recargapay.wallet.application.service.impl;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service("userDetailService")
public class CustomUserDetailsService implements UserDetailsService {


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Aqui você buscaria o usuário no banco de dados
        if ("admin".equals(username)) {
            return new User("admin", passwordEncoder().encode("admin123"), Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        } else if ("user".equals(username)) {
            return new User("user", passwordEncoder().encode("user123"), Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        } else {
            throw new UsernameNotFoundException("Usuário não encontrado: " + username);
        }
    }

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

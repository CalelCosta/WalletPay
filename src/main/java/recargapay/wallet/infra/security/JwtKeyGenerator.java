package recargapay.wallet.infra.security;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Collections;

@Configuration
@Slf4j
public class JwtKeyGenerator implements EnvironmentPostProcessor {

    private static final String PROPERTY_NAME = "jwt.secret";
    private static final String ENV_VAR_NAME = "JWT_SECRET";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // Verifica se já existe a propriedade na linha de comando ou nas variáveis de ambiente
        String secret = System.getProperty(ENV_VAR_NAME);
        if (secret == null || secret.isEmpty()) {
            // Se não estiver definida, gera uma chave segura
            SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            secret = Base64.getEncoder().encodeToString(key.getEncoded());
            // Opcional: também definir em System properties, se necessário
            System.setProperty(ENV_VAR_NAME, secret);
        }
        // Adiciona a propriedade "jwt.secret" no início da cadeia de PropertySources
        environment.getPropertySources().addFirst(new MapPropertySource("jwtSecret",
                Collections.singletonMap(PROPERTY_NAME, secret)));
    }
}
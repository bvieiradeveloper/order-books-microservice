package br.com.microservices.orchestrated.orderservice.config.jwt;


import br.com.bvieira.core.service.JWTService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Value("${app.token.secret-key}")
    private String secretKey;

    @Bean
    public JWTService jwtService(){
        return new JWTService(secretKey);
    }
}
package br.com.com.microservices.orchestrated.authenticationservice.core.service;

import br.com.com.microservices.orchestrated.authenticationservice.config.exception.ValidationException;
import br.com.com.microservices.orchestrated.authenticationservice.core.dto.AuthRequest;
import br.com.com.microservices.orchestrated.authenticationservice.core.dto.TokenDTO;
import br.com.com.microservices.orchestrated.authenticationservice.core.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static org.springframework.util.ObjectUtils.isEmpty;


@Service
@AllArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public TokenDTO login(AuthRequest request){
        var user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new ValidationException("User not found"));
        var accessToken = jwtService.createToken(user);
        validatePassword(request.password(),user.getPassword());
        return new TokenDTO(accessToken);
    };

    //ratelimite para evitar ataque de força bruta
    private void validatePassword(String rawPassword, String encodedPassword){
        if (!passwordEncoder.matches(rawPassword, encodedPassword)){
            throw new ValidationException("The password is incorrect!");
        }
    }

    public TokenDTO validateToken(String accessToken){
        validateExistingToken(accessToken);
        jwtService.validateAccessToken(accessToken);

        return new TokenDTO(accessToken);
    }

    private void validateExistingToken(String accessToken){
        if (isEmpty(accessToken)){
            throw  new ValidationException("The access token must be informed!");
        }
    }

}

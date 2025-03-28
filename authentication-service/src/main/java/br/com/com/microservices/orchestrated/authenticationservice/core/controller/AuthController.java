package br.com.com.microservices.orchestrated.authenticationservice.core.controller;

import br.com.com.microservices.orchestrated.authenticationservice.core.dto.AuthRequest;
import br.com.com.microservices.orchestrated.authenticationservice.core.dto.TokenDTO;
import br.com.com.microservices.orchestrated.authenticationservice.core.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("api/auth")
public class AuthController {

    private final AuthService service;

    @PostMapping("login")
    public TokenDTO login(@RequestBody AuthRequest request){
        return service.login(request);
    }

    @PostMapping("token/validate")
    public TokenDTO login(@RequestHeader String accessToken){
        return service.validateToken(accessToken);
    }
}

package br.com.microservices.orchestrated.orderservice.core.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    @GetMapping("/me")
    public String getLoggedUser(@AuthenticationPrincipal UserDetails user) {
        return user.getUsername();
    }
}

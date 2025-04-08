package br.com.com.microservices.orchestrated.authenticationservice.core.repository;

import br.com.com.microservices.orchestrated.authenticationservice.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
}

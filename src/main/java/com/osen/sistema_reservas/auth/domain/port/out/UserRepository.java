package com.osen.sistema_reservas.auth.domain.port.out;

import com.osen.sistema_reservas.auth.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    User save(User user);
    Optional<User> findById(Long id);

}

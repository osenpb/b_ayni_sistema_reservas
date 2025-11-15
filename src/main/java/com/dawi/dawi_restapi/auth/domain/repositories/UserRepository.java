package com.dawi.dawi_restapi.auth.domain.repositories;

import com.dawi.dawi_restapi.auth.domain.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    User save(User user);
    Optional<User> findById(Long id);

}

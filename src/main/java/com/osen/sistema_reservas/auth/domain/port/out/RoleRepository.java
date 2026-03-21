package com.osen.sistema_reservas.auth.domain.port.out;

import com.osen.sistema_reservas.auth.domain.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}

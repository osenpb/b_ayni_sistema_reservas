package com.dawi.dawi_restapi.auth.domain.repositories;

import com.dawi.dawi_restapi.auth.domain.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}

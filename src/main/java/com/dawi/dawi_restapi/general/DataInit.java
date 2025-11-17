package com.dawi.dawi_restapi.general;

import com.dawi.dawi_restapi.auth.domain.models.Role;
import com.dawi.dawi_restapi.auth.domain.repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInit implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataInit(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if(roleRepository.findAll().isEmpty()) {
                Role roleAdmin = new Role(null, "ADMIN");
            Role roleClient = new Role(null, "CLIENT");

            roleRepository.save(roleAdmin);
            roleRepository.save(roleClient);
        }
    }
}

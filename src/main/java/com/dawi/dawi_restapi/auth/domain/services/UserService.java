package com.dawi.dawi_restapi.auth.domain.services;

import com.dawi.dawi_restapi.auth.domain.models.User;
import com.dawi.dawi_restapi.auth.infraestructure.dtos.UserResponse;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<UserResponse> findAll();
    User findById(Long id);
    UserResponse save(User user);
    void deleteById(Long id);
    Optional<User> findByEmail(String email);

}

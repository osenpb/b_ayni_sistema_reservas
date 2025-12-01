package com.dawi.dawi_restapi.auth.application.services;

import com.dawi.dawi_restapi.auth.application.mappers.AuthMapper;
import com.dawi.dawi_restapi.auth.domain.models.User;
import com.dawi.dawi_restapi.auth.domain.repositories.UserRepository;
import com.dawi.dawi_restapi.auth.domain.services.UserService;
import com.dawi.dawi_restapi.auth.infraestructure.dtos.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserResponse> findAll() {
        List<User> userList = userRepository.findAll();

        List<UserResponse> userResponseDTOList = userList.stream()
                                                        .map(AuthMapper::toDto).toList();

        return userResponseDTOList;
    }

    @Override
    public User findById(Long id) {
        return null;
    }

    @Override
    public UserResponse save(User user) {
        return null;
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}

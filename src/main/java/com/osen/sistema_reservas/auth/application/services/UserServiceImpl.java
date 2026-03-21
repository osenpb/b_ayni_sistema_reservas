package com.osen.sistema_reservas.auth.application.services;

import com.osen.sistema_reservas.auth.application.mappers.AuthMapper;
import com.osen.sistema_reservas.auth.domain.model.User;
import com.osen.sistema_reservas.auth.domain.port.out.UserRepository;
import com.osen.sistema_reservas.auth.domain.port.in.UserService;
import com.osen.sistema_reservas.auth.infraestructure.dtos.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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

package com.dawi.dawi_restapi.auth.application.services;

import com.dawi.dawi_restapi.auth.application.mappers.AuthMapper;
import com.dawi.dawi_restapi.auth.domain.models.Role;
import com.dawi.dawi_restapi.auth.domain.models.User;
import com.dawi.dawi_restapi.auth.domain.repositories.RoleRepository;
import com.dawi.dawi_restapi.auth.domain.repositories.UserRepository;
import com.dawi.dawi_restapi.auth.domain.services.AuthService;
import com.dawi.dawi_restapi.auth.domain.services.TokenService;
import com.dawi.dawi_restapi.auth.infraestructure.dtos.LoginRequest;
import com.dawi.dawi_restapi.auth.infraestructure.dtos.RegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService, UserDetailsService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final RoleRepository roleRepository;

    @Override
    public Map<String, String> login(LoginRequest loginRequestDTO) {
        try {
            final AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();
            final Authentication authRequest = AuthMapper.fromDto(loginRequestDTO);
            final Authentication authentication = authenticationManager.authenticate(authRequest);

            User user = (User) authentication.getPrincipal();

            String token = tokenService.generateToken(authentication);

            return Map.of(
                    "access-token", token // aqui por si quieres agregar un refresh token, pero sino puede enviar directo el token sin un map
            );

        } catch (BadCredentialsException e) {
            log.error("ERROR: Credenciales incorrectas para: {}", loginRequestDTO.email());
            throw new BadCredentialsException("Credenciales inválidas", e);
        } catch (UsernameNotFoundException e) {
            log.error("ERROR: Usuario no encontrado: {}", loginRequestDTO.email());
            throw new UsernameNotFoundException("Usuario no encontrado", e);
        } catch (DisabledException e) {
            log.error("ERROR: Usuario deshabilitado: {}", loginRequestDTO.email());
            throw new DisabledException("Usuario deshabilitado", e);
        } catch (Exception e) {
            log.error("ERROR INESPERADO durante login: {}", e.getMessage(), e);
            throw new RuntimeException("Error en autenticación generico", e);
        }
    }

    @Override
    public boolean validateToken(String token) {
        return tokenService.validateToken(token);
    }

    @Override
    public String getUserFromToken(String token) {
        return tokenService.getUserFromToken(token);
    }

    @Override
    public void createUser(RegisterRequest createUserDto) {
        Role roleClient = roleRepository.findById(2L).orElseThrow();


        final User createUser = AuthMapper.fromDto(createUserDto);
        createUser.setEmail(createUserDto.email());
        createUser.setPassword(passwordEncoder.encode(createUserDto.password()));
        createUser.setRole(roleClient);
        createUser.setTelefono(createUserDto.telefono());

        final User user = userRepository.save(createUser);
        log.info("[USER] : User successfully created with id {}", user.getId());
    }

    @Override
    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.info("[USER] : User not found with id {}", id);
                    return new UsernameNotFoundException("User not found"); // exception personalizada
                });
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    log.error("[USER] : User not found with email {}", username);
                    return new UsernameNotFoundException("User not found");
                });
    }
}

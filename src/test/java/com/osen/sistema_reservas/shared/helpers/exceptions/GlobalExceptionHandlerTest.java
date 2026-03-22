package com.osen.sistema_reservas.shared.helpers.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler - Tests de manejo de excepciones")
class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    @Nested
    @DisplayName("EntityNotFoundException")
    class EntityNotFoundTests {

        @Test
        @DisplayName("Debe retornar 404 con mensaje de entidad no encontrada")
        void shouldReturn404ForEntityNotFound() {
            EntityNotFoundException ex = new EntityNotFoundException("Reserva con ID:999 no encontrada");

            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleEntityNotFoundException(ex, request);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertEquals(404, response.getBody().getStatus());
            assertEquals("Reserva con ID:999 no encontrada", response.getBody().getMessage());
        }
    }

    @Nested
    @DisplayName("BusinessException")
    class BusinessExceptionTests {

        @Test
        @DisplayName("Debe retornar 400 con codigo de error de negocio")
        void shouldReturn400ForBusinessException() {
            BusinessException ex = new BusinessException("Solo se pueden confirmar reservas pendientes", "ESTADO_INVALIDO");

            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBusinessException(ex, request);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("ESTADO_INVALIDO", response.getBody().getError());
        }
    }

    @Nested
    @DisplayName("ValidationException")
    class ValidationExceptionTests {

        @Test
        @DisplayName("Debe retornar 400 con campo invalido")
        void shouldReturn400ForValidationException() {
            ValidationException ex = new ValidationException("fechaInicio", "La fecha de inicio es requerida");

            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationException(ex, request);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Validation Error", response.getBody().getError());
        }
    }

    @Nested
    @DisplayName("ConflictException")
    class ConflictExceptionTests {

        @Test
        @DisplayName("Debe retornar 409 para conflictos")
        void shouldReturn409ForConflict() {
            ConflictException ex = new ConflictException("Habitacion no disponible", "HABITACION_NO_DISPONIBLE");

            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleConflictException(ex, request);

            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
            assertEquals("HABITACION_NO_DISPONIBLE", response.getBody().getError());
        }
    }

    @Nested
    @DisplayName("JwtException y TokenExpiredException")
    class AuthExceptionTests {

        @Test
        @DisplayName("Debe retornar 401 para JWT invalido")
        void shouldReturn401ForJwtException() {
            JwtException ex = new JwtException("Token invalido");

            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleJwtException(ex, request);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Invalid Token", response.getBody().getError());
        }

        @Test
        @DisplayName("Debe retornar 401 para token expirado")
        void shouldReturn401ForTokenExpired() {
            TokenExpiredException ex = new TokenExpiredException("Token expirado");

            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleTokenExpiredException(ex, request);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Token Expired", response.getBody().getError());
        }

        @Test
        @DisplayName("Debe retornar 401 para bad credentials")
        void shouldReturn401ForBadCredentials() {
            BadCredentialsException ex = new BadCredentialsException("Credenciales incorrectas");

            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBadCredentialsException(ex, request);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Email o contraseña incorrectos", response.getBody().getMessage());
        }
    }

    @Nested
    @DisplayName("ForbiddenException")
    class ForbiddenExceptionTests {

        @Test
        @DisplayName("Debe retornar 403 para acceso denegado")
        void shouldReturn403ForForbidden() {
            ForbiddenException ex = new ForbiddenException("No tienes permiso para esta accion");

            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleForbiddenException(ex, request);

            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
            assertEquals("Forbidden", response.getBody().getError());
        }
    }

    @Nested
    @DisplayName("DataIntegrityViolationException")
    class DataIntegrityTests {

        @Test
        @DisplayName("Debe retornar 409 con mensaje de DNI duplicado")
        void shouldReturn409ForDniDuplicate() {
            DataIntegrityViolationException ex = new DataIntegrityViolationException("Duplicate entry '12345678' for key 'dni'");

            ResponseEntity<ErrorResponse> response = globalExceptionHandler.dataIntegrityViolationException(ex, request);

            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
            assertEquals("El DNI ya está registrado", response.getBody().getMessage());
        }

        @Test
        @DisplayName("Debe retornar 409 con mensaje de email duplicado")
        void shouldReturn409ForEmailDuplicate() {
            DataIntegrityViolationException ex = new DataIntegrityViolationException("Duplicate entry 'test@test.com' for key 'email'");

            ResponseEntity<ErrorResponse> response = globalExceptionHandler.dataIntegrityViolationException(ex, request);

            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
            assertEquals("El correo electrónico ya está registrado", response.getBody().getMessage());
        }
    }

    @Nested
    @DisplayName("Generic Exception")
    class GenericExceptionTests {

        @Test
        @DisplayName("Debe retornar 500 para excepciones no controladas")
        void shouldReturn500ForGenericException() {
            RuntimeException ex = new RuntimeException("Error interno");

            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleRuntimeException(ex, request);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        }
    }
}

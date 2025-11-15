package com.dawi.dawi_restapi.auth.infraestructure.filters;

import com.dawi.dawi_restapi.auth.domain.services.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthService authService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(AuthService authService, UserDetailsService userDetailsService) {
        this.authService = authService;
        this.userDetailsService = userDetailsService;
    }
    // no he usado el required args construct para q se note que esta clase tiene el constructor explicito

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Si es una ruta pública, no validamos
        if (shouldNotFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        final Optional<String> token = resolveTokenFromHeader(request);

        // Si no hay token o es inválido, que Spring Security maneje la autorización
        if (token.isEmpty() || !authService.validateToken(token.get())) {
            filterChain.doFilter(request, response);
            return;
        }

        String userName = authService.getUserFromToken(token.get());

        UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());

        authenticationToken.setDetails(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }

    //si en lugar de gestionar mediante cookies quieres hacerlo con authorization, solo cambias este metodo
    private Optional<String> resolveTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return Optional.of(bearerToken.substring(7)); // quitar "Bearer "
        }
        return Optional.empty();
    }

}

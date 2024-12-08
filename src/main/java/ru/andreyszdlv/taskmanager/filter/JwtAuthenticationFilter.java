package ru.andreyszdlv.taskmanager.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.andreyszdlv.taskmanager.enums.Role;
import ru.andreyszdlv.taskmanager.exception.InvalidTokenException;
import ru.andreyszdlv.taskmanager.service.JwtExtractorService;
import ru.andreyszdlv.taskmanager.validator.JwtValidator;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final MessageSource messageSource;

    private final JwtExtractorService jwtExtractorService;

    private final JwtValidator jwtValidator;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractToken(request);

        try {
            if (token != null) {
                jwtValidator.validateAccessToken(token);

                String username = jwtExtractorService.extractUserEmail(token);
                log.info("Extracted username: {}", username);

                Role role = jwtExtractorService.extractRole(token);
                log.info("Extracted role: {}", role);

                List<GrantedAuthority> authorities =
                        List.of(new SimpleGrantedAuthority(role.name()));

                Authentication auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);

                log.info("User authenticated: {}", username);
            }

            filterChain.doFilter(request, response);
        }
        catch (InvalidTokenException ex) {
            log.error("Invalid token, send response unauthorized for user");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("text/plain; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(
                    messageSource.getMessage("error.401.user.unauthorized", null, request.getLocale())
            );
        }
    }

    private String extractToken(HttpServletRequest request) {
        log.info("Extract token from request header");
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            log.info("token != null");
            return bearerToken.substring(7);
        }
        log.info("token == null");
        return null;
    }
}
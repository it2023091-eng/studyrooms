package gr.hua.dit.studyrooms.core.security;


import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

import gr.hua.dit.studyrooms.web.rest.error.ApiError;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(final JwtService jwtService, final ObjectMapper objectMapper) {
        if (jwtService == null) throw new NullPointerException();
        if (objectMapper == null) throw new NullPointerException();
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        // No header or not Bearer -> continue unauthenticated
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authorizationHeader.substring(7);

        try {
            final Claims claims = this.jwtService.parse(token);
            final String subject = claims.getSubject();
            final Collection<String> roles = (Collection<String>) claims.get("roles");

            final List<GrantedAuthority> authorities =
                    (roles == null)
                            ? List.<GrantedAuthority>of()
                            : roles.stream()
                            .map(r -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + r))
                            .toList();


            final User principal = new User(subject, "", authorities);

            final UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(principal, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (Exception ex) {
            LOGGER.warn("JwtAuthenticationFilter failed", ex);
            writeError(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void writeError(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        final ApiError apiError = new ApiError(
                Instant.now(),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                "",
                request.getRequestURI()
        );

        response.getWriter().write(this.objectMapper.writeValueAsString(apiError));
    }
}

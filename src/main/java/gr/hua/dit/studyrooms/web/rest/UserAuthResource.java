package gr.hua.dit.studyrooms.web.rest;

import gr.hua.dit.studyrooms.core.model.User;
import gr.hua.dit.studyrooms.core.repository.UserRepository;
import gr.hua.dit.studyrooms.core.security.JwtService;

import gr.hua.dit.studyrooms.web.rest.model.UserTokenRequest;
import gr.hua.dit.studyrooms.web.rest.model.UserTokenResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserAuthResource {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserAuthResource(final UserRepository userRepository,
                            final PasswordEncoder passwordEncoder,
                            final JwtService jwtService) {
        if (userRepository == null) throw new NullPointerException();
        if (passwordEncoder == null) throw new NullPointerException();
        if (jwtService == null) throw new NullPointerException();
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/tokens")
    public UserTokenResponse token(@RequestBody @Valid final UserTokenRequest request) {
        final String email = request.email().trim();

        final User user = this.userRepository.findByEmailIgnoreCase(email).orElse(null);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        if (!this.passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        final String role = user.getRole().name(); // e.g. USER / ADMIN
        final String subject = "user:" + user.getEmail();

        final String token = this.jwtService.issue(subject, List.of(role));

        return new UserTokenResponse(token, "Bearer", 60 * 60);
    }
}

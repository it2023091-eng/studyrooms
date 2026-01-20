package gr.hua.dit.studyrooms.config;



import gr.hua.dit.studyrooms.core.security.JwtAuthenticationFilter;
import gr.hua.dit.studyrooms.web.rest.error.RestApiAccessDeniedHandler;
import gr.hua.dit.studyrooms.web.rest.error.RestApiAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * API chain (/api/**): STATELESS + JWT
     */
    @Bean
    @Order(1)
    public SecurityFilterChain apiChain(final HttpSecurity http,
                                        final JwtAuthenticationFilter jwtAuthenticationFilter,
                                        final RestApiAuthenticationEntryPoint restApiAuthenticationEntryPoint,
                                        final RestApiAccessDeniedHandler restApiAccessDeniedHandler) throws Exception {

        http
                .securityMatcher("/api/**")
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Token endpoint
                        .requestMatchers("/api/v1/auth/tokens").permitAll()

                        // (Δεν είναι /api, αλλά δεν πειράζει να υπάρχει κι εδώ)
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()

                        // ✅ ROOMS management = STAFF only (Admin)
                        .requestMatchers(HttpMethod.POST, "/api/rooms/**").hasRole("STAFF")
                        .requestMatchers(HttpMethod.PUT, "/api/rooms/**").hasRole("STAFF")
                        .requestMatchers(HttpMethod.DELETE, "/api/rooms/**").hasRole("STAFF")

                        // Everything else in /api requires authentication
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exh -> exh
                        .authenticationEntryPoint(restApiAuthenticationEntryPoint)
                        .accessDeniedHandler(restApiAccessDeniedHandler)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable);

        return http.build();
    }

    /**
     * UI chain (/**): STATEFUL + form login
     */
    @Bean
    @Order(2)
    public SecurityFilterChain uiChain(final HttpSecurity http) throws Exception {

        http
                .securityMatcher("/**")
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .headers(h -> h.frameOptions(f -> f.disable()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/rooms", "/register", "/login", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()

                        // ✅ Swagger UI & OpenAPI JSON must be accessible
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()

                        // ✅ Staff-only area
                        .requestMatchers("/staff/**").hasRole("STAFF")

                        // ✅ Booking endpoints: logged-in
                        .requestMatchers("/my-bookings", "/rooms/*/book", "/bookings/*/cancel").authenticated()

                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/rooms", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/rooms")
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

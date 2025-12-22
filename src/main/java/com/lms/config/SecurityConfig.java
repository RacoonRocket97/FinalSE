package com.lms.config;

import com.lms.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        builder
                .userDetailsService(userService)
                .passwordEncoder(passwordEncoder);

        return builder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // Public
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()

                        // Users
                        .requestMatchers(HttpMethod.GET, "/api/users/profile").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/users/profile").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/users/change-password").authenticated()
                        .requestMatchers("/api/users/**").hasAuthority("ROLE_ADMIN")

                        // Courses
                        .requestMatchers(HttpMethod.GET, "/api/courses/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/courses").hasAnyAuthority("ROLE_TEACHER", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/courses/**").hasAnyAuthority("ROLE_TEACHER", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/courses/**").hasAnyAuthority("ROLE_TEACHER", "ROLE_ADMIN")

                        // Lessons
                        .requestMatchers(HttpMethod.GET, "/api/lessons/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/lessons").hasAnyAuthority("ROLE_TEACHER", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/lessons/**").hasAnyAuthority("ROLE_TEACHER", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/lessons/**").hasAnyAuthority("ROLE_TEACHER", "ROLE_ADMIN")

                        // Enrollments
                        .requestMatchers("/api/enrollments/**").authenticated()

                        // Assignments
                        .requestMatchers(HttpMethod.POST, "/api/assignments").hasAnyAuthority("ROLE_TEACHER", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/assignments/*/submit").hasAuthority("ROLE_STUDENT")
                        .requestMatchers(HttpMethod.PUT, "/api/assignments/*/grade").hasAnyAuthority("ROLE_TEACHER", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/assignments/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/assignments/**").hasAnyAuthority("ROLE_TEACHER", "ROLE_ADMIN")

                        .anyRequest().authenticated()
                )
                .httpBasic(basic -> {});

        return http.build();
    }
}

package com.flight.booking.gateway.config;

import com.flight.booking.gateway.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        CorsConfiguration corsConfig = getCorsConfiguration();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(source))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler))
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec

                        .pathMatchers("/ws/**").permitAll()

                        // Open actuators to admins
                        .pathMatchers("/auth-service/actuator/**").hasRole("Admin")
                        .pathMatchers("/user-service/actuator/**").hasRole("Admin")
                        .pathMatchers("/flight-service/actuator/**").hasRole("Admin")
                        .pathMatchers("/airport-service/actuator/**").hasRole("Admin")
                        .pathMatchers("/schedule-service/actuator/**").hasRole("Admin")
                        .pathMatchers("/payment-service/actuator/**").hasRole("Admin")
                        .pathMatchers("/booking-service/actuator/**").hasRole("Admin")

                        .pathMatchers("/booking-service/api/**").authenticated()
                        .pathMatchers("/payment-service/api/**").authenticated()

                        // Explicitly permit OPTIONS requests for CORS preflight
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Auth Service - Public endpoints
                        .pathMatchers(HttpMethod.POST, "/auth-service/api/login").permitAll()
                        .pathMatchers(HttpMethod.POST, "/auth-service/api/register").permitAll()

                        // User Service - Admin only endpoints
                        .pathMatchers(HttpMethod.GET, "/user-service/api/user/email/**").hasRole("Admin")
                        .pathMatchers(HttpMethod.GET, "/user-service/api/users").hasRole("Admin")
                        .pathMatchers(HttpMethod.POST, "/user-service/api/user").hasRole("Admin")
                        .pathMatchers(HttpMethod.DELETE, "/user-service/api/user/**").hasRole("Admin")
                        .pathMatchers(HttpMethod.DELETE, "/user-service/api/users").hasRole("Admin")

                        // User Service - User/Admin endpoints (handled by controller-level checks)
                        .pathMatchers(HttpMethod.GET, "/user-service/api/user/**").authenticated()
                        .pathMatchers(HttpMethod.PUT, "/user-service/api/user").authenticated()
                        .pathMatchers(HttpMethod.PUT, "/user-service/api/user-role").hasRole("Admin")

                        // Flight Service - Public endpoints
                        .pathMatchers(HttpMethod.GET, "/flight-service/api/flights").permitAll()
                        .pathMatchers(HttpMethod.GET, "/flight-service/api/flight/**").permitAll()

                        // Flight Service - Admin only endpoints
                        .pathMatchers(HttpMethod.POST, "/flight-service/api/flight").hasRole("Admin")
                        .pathMatchers(HttpMethod.PUT, "/flight-service/api/flight").hasRole("Admin")
                        .pathMatchers(HttpMethod.DELETE, "/flight-service/api/flight/**").hasRole("Admin")
                        .pathMatchers(HttpMethod.DELETE, "/flight-service/api/flights").hasRole("Admin")

                        // Airport Service - Public endpoints
                        .pathMatchers(HttpMethod.GET, "/airport-service/api/airports").permitAll()
                        .pathMatchers(HttpMethod.GET, "/airport-service/api/airport/**").permitAll()

                        // Airport Service - Admin only endpoints
                        .pathMatchers(HttpMethod.POST, "/airport-service/api/airport").hasRole("Admin")
                        .pathMatchers(HttpMethod.PUT, "/airport-service/api/airport").hasRole("Admin")
                        .pathMatchers(HttpMethod.DELETE, "/airport-service/api/airport/**").hasRole("Admin")

                        // Schedule Service - Public endpoints
                        .pathMatchers(HttpMethod.GET, "/schedule-service/api/schedules").permitAll()

                        // Schedule Service -  Authenticated Endpoint
                        .pathMatchers(HttpMethod.GET,"/schedule-service/api/schedule/**").authenticated()

                        // Schedule Service - Admin only endpoints
                        .pathMatchers(HttpMethod.POST, "/schedule-service/api/schedule").hasRole("Admin")
                        .pathMatchers(HttpMethod.DELETE, "/schedule-service/api/schedules").hasRole("Admin")
                        .pathMatchers(HttpMethod.DELETE, "/schedule-service/api/schedule/airport/**").hasRole("Admin")
                        .pathMatchers(HttpMethod.DELETE, "/schedule-service/api/schedule/flight/**").hasRole("Admin")
                        .pathMatchers(HttpMethod.GET,"/schedule-service/api/all-schedules").hasRole("Admin")
                        .pathMatchers(HttpMethod.PUT,"/schedule-service/api/schedule").hasRole("Admin")
                        // Default policy - require authentication for any other endpoints
                        .anyExchange().authenticated())

                .addFilterBefore(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .build();
    }

    private static CorsConfiguration getCorsConfiguration() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedOriginPattern("*");
        corsConfig.addAllowedMethod("*");
        corsConfig.addAllowedHeader("*");
        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(3600L);

        // Add required exposed headers
        corsConfig.addExposedHeader("Authorization");
        corsConfig.addExposedHeader("Access-Control-Allow-Origin");
        corsConfig.addExposedHeader("Access-Control-Allow-Credentials");
        return corsConfig;
    }
}
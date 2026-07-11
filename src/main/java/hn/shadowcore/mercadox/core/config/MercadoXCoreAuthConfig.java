package hn.shadowcore.mercadox.core.config;

import hn.shadowcore.mercadox.context.filter.JwtAuthFilter;
import hn.shadowcore.mercadox.context.filter.TenantValidatorFilter;
import hn.shadowcore.mercadox.context.utils.JwtUtil;
import hn.shadowcore.mercadox.context.validator.AnonymousTenantValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class MercadoXCoreAuthConfig {

    private final JwtUtil jwtUtil;

    private final AnonymousTenantValidator tenantValidatorFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/public/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(new JwtAuthFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new TenantValidatorFilter(jwtUtil, tenantValidatorFilter), JwtAuthFilter.class)
                .build();

    }

}

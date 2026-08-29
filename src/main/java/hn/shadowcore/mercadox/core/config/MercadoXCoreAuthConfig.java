package hn.shadowcore.mercadox.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import hn.shadowcore.mercadox.context.filter.JwtAuthFilter;
import hn.shadowcore.mercadox.context.filter.TenantValidatorFilter;
import hn.shadowcore.mercadox.context.security.JwtVerifier;
import hn.shadowcore.mercadox.context.validator.AnonymousTenantValidator;
import hn.shadowcore.mercadox.core.filter.LeadRateLimitFilter;
import hn.shadowcore.mercadox.library.redis.util.RedisRateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.time.Duration;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class MercadoXCoreAuthConfig {

    private final JwtVerifier jwtVerifier;

    private final AnonymousTenantValidator anonymousTenantValidator;

    private final RedisRateLimiter redisRateLimiter;

    private final ObjectMapper objectMapper;

    @Value("${leads.ratelimit.max-body-bytes:8192}")
    private long leadsMaxBodyBytes;

    @Value("${leads.ratelimit.ip.max-requests:5}")
    private int leadsIpMaxRequests;

    @Value("${leads.ratelimit.ip.window-seconds:60}")
    private long leadsIpWindowSeconds;

    @Value("${leads.ratelimit.org.max-requests:20}")
    private int leadsOrgMaxRequests;

    @Value("${leads.ratelimit.org.window-seconds:60}")
    private long leadsOrgWindowSeconds;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        LeadRateLimitFilter leadRateLimitFilter = new LeadRateLimitFilter(
                redisRateLimiter,
                objectMapper,
                leadsMaxBodyBytes,
                leadsIpMaxRequests,
                Duration.ofSeconds(leadsIpWindowSeconds),
                leadsOrgMaxRequests,
                Duration.ofSeconds(leadsOrgWindowSeconds));

        return http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/public/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(new JwtAuthFilter(jwtVerifier), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(leadRateLimitFilter, JwtAuthFilter.class)
                .addFilterAfter(new TenantValidatorFilter(anonymousTenantValidator), LeadRateLimitFilter.class)
                .build();

    }

}

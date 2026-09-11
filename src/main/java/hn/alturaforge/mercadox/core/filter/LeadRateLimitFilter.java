package hn.alturaforge.mercadox.core.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import hn.alturaforge.mercadox.library.entity.response.BaseResponseDto;
import hn.alturaforge.mercadox.library.redis.util.RedisRateLimiter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

/**
 * Applies abuse controls to the public lead-generation endpoint only:
 * a request-body size cap (413) and two independent Redis-backed rate
 * limiters, one per source IP and one per orgId (429). Runs before
 * {@link hn.alturaforge.mercadox.context.filter.TenantValidatorFilter} so
 * spam is rejected before that filter's DB lookup.
 */
@RequiredArgsConstructor
public class LeadRateLimitFilter extends OncePerRequestFilter {

    private static final String LEAD_PATH_PREFIX = "/api/v1/public/orgs/";
    private static final String LEAD_PATH_SUFFIX = "/leads";
    private static final String IP_KEY_PREFIX = "leadRate:ip:";
    private static final String ORG_KEY_PREFIX = "leadRate:org:";

    private final RedisRateLimiter rateLimiter;
    private final ObjectMapper objectMapper;
    private final long maxBodyBytes;
    private final int ipMaxRequests;
    private final Duration ipWindow;
    private final int orgMaxRequests;
    private final Duration orgWindow;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

        if (!isLeadEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (request.getContentLengthLong() > maxBodyBytes) {
            writeError(response, HttpStatus.PAYLOAD_TOO_LARGE, "Request body too large.");
            return;
        }

        String clientIp = extractClientIp(request);
        if (!rateLimiter.tryConsume(IP_KEY_PREFIX + clientIp, ipMaxRequests, ipWindow)) {
            writeError(response, HttpStatus.TOO_MANY_REQUESTS, "Too many requests. Please try again later.");
            return;
        }

        String orgId = extractOrgId(request.getRequestURI());
        if (StringUtils.hasText(orgId)
                && !rateLimiter.tryConsume(ORG_KEY_PREFIX + orgId, orgMaxRequests, orgWindow)) {
            writeError(response, HttpStatus.TOO_MANY_REQUESTS, "Too many requests. Please try again later.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isLeadEndpoint(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return "POST".equalsIgnoreCase(request.getMethod())
                && uri.startsWith(LEAD_PATH_PREFIX)
                && uri.endsWith(LEAD_PATH_SUFFIX);
    }

    private String extractOrgId(String uri) {
        String[] segments = uri.split("/");
        return segments.length >= 6 ? segments[5] : null;
    }

    // X-Forwarded-For is attacker-controllable unless a trusted proxy in
    // front of this service overwrites it — this is a best-effort abuse
    // signal, not a security boundary.
    private String extractClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwardedFor)) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void writeError(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        BaseResponseDto<String> body = new BaseResponseDto<>();
        body.buildResponseEntity(status, message, message);

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}

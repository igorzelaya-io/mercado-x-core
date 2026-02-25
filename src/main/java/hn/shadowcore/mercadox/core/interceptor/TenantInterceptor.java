package hn.shadowcore.mercadox.core.interceptor;

import hn.shadowcore.mercadox.core.service.validator.TenantValidatorService;
import hn.shadowcore.mercadoxcontext.utils.OrgIdContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class TenantInterceptor implements HandlerInterceptor {

    private final TenantValidatorService tenantValidatorService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(OrgIdContextHolder.getTenantId() != null) {
            return true;
        }

        @SuppressWarnings("unchecked")
        Map<String, String> pathVariables =
                (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        if(pathVariables != null && pathVariables.containsKey("orgId")) {
            final String orgId = pathVariables.get("orgId");
            tenantValidatorService.validate(orgId);
            OrgIdContextHolder.setTenantId(orgId);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        OrgIdContextHolder.clear();
    }
}

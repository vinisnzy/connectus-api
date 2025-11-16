package com.vinisnzy.connectus_api.infra.tenant;

import com.vinisnzy.connectus_api.infra.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TenantInterceptor implements HandlerInterceptor {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        UUID companyId;
        try {
            companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        } catch (IllegalStateException _) {
            return true;
        }

        jdbcTemplate.execute(String.format("SET app.current_company = '%s';", companyId));
        return true;
    }
}

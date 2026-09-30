package com.capstone.champ.configuration;

import com.capstone.champ.model.User;
import com.capstone.champ.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import java.util.Map;

/** Enforces the current account-ID session convention without token authentication. */
@Component @RequiredArgsConstructor
public class AccountOwnershipInterceptor implements HandlerInterceptor {
    public static final String ACCOUNT_HEADER = "X-CHAMP-Account-Id";
    private final UserRepository users;

    @Override
    @SuppressWarnings("unchecked")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        if (path.startsWith("/auth/") || path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs")
                || path.startsWith("/webjars/") || path.startsWith("/master-data/")) return true;
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;

        String accountId = request.getHeader(ACCOUNT_HEADER);
        if (accountId == null || accountId.isBlank()) return reject(response, 401, "Account identity is required");
        User account = users.findByAadhaarNumber(accountId).orElse(null);
        if (account == null) return reject(response, 401, "Account identity is invalid");

        Map<String, String> variables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (path.startsWith("/doctor/")) {
            if (!"DOCTOR".equals(account.getRole())) return reject(response, 403, "Doctor account is required");
            String routeDoctor = first(variables, "doctor", "doctorAccountId", "input");
            if (routeDoctor != null && !accountId.equals(routeDoctor)) return reject(response, 403, "Doctor account does not own this operation");
        } else if (path.startsWith("/patient/")) {
            if (!"USER".equals(account.getRole())) return reject(response, 403, "Patient account is required");
            String routePatient = first(variables, "patientAccountId");
            if (routePatient != null && !accountId.equals(routePatient)) return reject(response, 403, "Patient account does not own this operation");
        } else if (path.startsWith("/user/")) {
            if (!"USER".equals(account.getRole())) return reject(response, 403, "Patient account is required");
            String routePatient = first(variables, "input");
            if (routePatient != null && !accountId.equals(routePatient))
                return reject(response, 403, "Patient account does not own this record");
        } else if (path.startsWith("/admin/")) {
            if (!"ADMIN".equals(account.getRole())) return reject(response, 403, "Administrator account is required");
        }
        return true;
    }

    private String first(Map<String, String> values, String... keys) {
        if (values == null) return null;
        for (String key : keys) if (values.get(key) != null) return values.get(key);
        return null;
    }

    private boolean reject(HttpServletResponse response, int status, String message) throws Exception {
        response.setStatus(status); response.setContentType("application/json"); response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"status\":false,\"message\":\"" + message + "\"}");
        return false;
    }
}

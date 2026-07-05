package app.security;

import app.security.user.UserData;
import app.service.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class SessionUserValidationFilter extends OncePerRequestFilter {

    private final UserService userService;

    public SessionUserValidationFilter(UserService userService) {
        this.userService = Objects.requireNonNull(userService);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserData) {
            UserData principal = (UserData) auth.getPrincipal();
            UUID userId = principal.getUserId();

            try {
                // Verify user still exists; will throw if not
                userService.getById(userId);
            } catch (Exception ex) {
                // Invalidate session and clear authentication so templates render anonymous links
                try {
                    if (request.getSession(false) != null) {
                        request.getSession(false).invalidate();
                    }
                } catch (Exception ignored) {}
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}

package com.sintad.prueba_tecnica_fullstack.shared.interceptor;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    // ⚠️ Reemplaza con tu clave real (mejor ponerla en application.properties)
    private final Key secretKey = Keys.hmacShaKeyFor(
            "mysupersecurekeymysupersecurekey123456".getBytes(StandardCharsets.UTF_8)
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token JWT faltante o inválido");
            return false;
        }

        try {
            String token = authHeader.substring(7);
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String role = claims.get("role", String.class);
            String path = request.getRequestURI();

            // ✅ Si entra a categorías y no es ADMIN → bloquear
            if (path.startsWith("/api/categories") && !"ADMIN".equalsIgnoreCase(role)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Acceso denegado: se requiere rol ADMIN");
                return false;
            }

            return true;

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token inválido o expirado");
            return false;
        }
    }
}

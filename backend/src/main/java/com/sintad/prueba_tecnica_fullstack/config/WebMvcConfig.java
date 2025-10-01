package com.sintad.prueba_tecnica_fullstack.config;

import com.sintad.prueba_tecnica_fullstack.shared.interceptor.JwtAuthInterceptor;
import com.sintad.prueba_tecnica_fullstack.shared.interceptor.RequestLogInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final RequestLogInterceptor requestLogInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Logs en todas las rutas
        registry.addInterceptor(requestLogInterceptor).addPathPatterns("/**");

        // Seguridad con JWT en todas las APIs excepto /auth/**
        registry.addInterceptor(new JwtAuthInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/**"); // 👈 dejar login libre
    }
}

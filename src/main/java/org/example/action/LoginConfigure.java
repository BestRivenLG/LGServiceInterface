package org.example.action;

import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class LoginConfigure implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration registion = registry.addInterceptor(new UserLoginInterceptor());
        registion.addPathPatterns("/**");
        registion.excludePathPatterns("/api/userLogin");
    }
}

package org.example.action;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class LoginConfigure implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration registion = registry.addInterceptor(new UserLoginInterceptor());
        registion.addPathPatterns("/**");
        registion.excludePathPatterns("/api/userLogin");
        registion.excludePathPatterns("/api/tokenInvail");
    }
}

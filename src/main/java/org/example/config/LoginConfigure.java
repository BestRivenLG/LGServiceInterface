package org.example.config;

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
        registion.excludePathPatterns("/api/hello");
        registion.excludePathPatterns("/api/userRegister");
        registion.excludePathPatterns("/api/bannerList");
        registion.excludePathPatterns("/api/activity");
        registion.excludePathPatterns("/api/photoCategory");
        registion.excludePathPatterns("/api/photoSearch");
        registion.excludePathPatterns("/api/photoList");
        registion.excludePathPatterns("/api/photoList/v2");
        registion.excludePathPatterns("/api/photoList/v3");

        registion.excludePathPatterns("/zxz/user/login");
        registion.excludePathPatterns("/zxz/user/register");
        registion.excludePathPatterns("/zxz/user/logout");
        registion.excludePathPatterns("/zxz/bannerList");
        registion.excludePathPatterns("/zxz/activity");
        registion.excludePathPatterns("/zxz/photoCategory");
        registion.excludePathPatterns("/zxz/photoSearch");
        registion.excludePathPatterns("/zxz/photoList");
        registion.excludePathPatterns("/zxz/photoList/v2");
        registion.excludePathPatterns("/zxz/photoList/v3");

//        registion.excludePathPatterns("/api/myPhotoCollects");
//        registion.excludePathPatterns("/api/photoCollect");
    }
}

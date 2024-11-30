package com.company.encryptedtrafficclassifier.common.configs;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SystemSaTokenConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
                .addPathPatterns("/**")
                .excludePathPatterns("/static/**")
                .excludePathPatterns("/templates/**")
                .excludePathPatterns("/swagger-ui/**")
                .excludePathPatterns("/swagger-resources/**")
                .excludePathPatterns("/v2/**")
                .excludePathPatterns("/index")
                .excludePathPatterns("/login")
                .excludePathPatterns("/")
                .excludePathPatterns("/register")
                .excludePathPatterns("/error")
                .excludePathPatterns("/user/captcha")
                .excludePathPatterns("/user/login")
                .excludePathPatterns("/user/register");
    }

}
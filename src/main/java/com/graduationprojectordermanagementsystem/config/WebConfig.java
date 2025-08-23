package com.graduationprojectordermanagementsystem.config;

import com.graduationprojectordermanagementsystem.interceptor.JwtAuthenticationInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private JwtAuthenticationInterceptor jwtAuthenticationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry  registry){
        registry.addInterceptor(jwtAuthenticationInterceptor)
                .addPathPatterns("/api/**")// 拦截所有请求
                .excludePathPatterns("/api/user/login", "/api/user/register");
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter());
        converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    @Override
    public void addCorsMappings(CorsRegistry  registry){
        registry.addMapping("/api/**")// 拦截所有请求
                .allowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*")// 允许跨域的域名
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")// 允许的请求方法
                .allowedHeaders("*")// 允许的请求头
                .exposedHeaders("Authorization","X-Total-Count")// 允许的响应头
                .allowCredentials(true)// 允许携带cookie
                .maxAge(3600);// 缓存时间
    }
}

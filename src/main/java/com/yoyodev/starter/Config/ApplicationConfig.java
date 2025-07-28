package com.yoyodev.starter.Config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yoyodev.starter.AOP.Jwt.JwtFilter;
import com.yoyodev.starter.AOP.Logging.CustomAuditingFilter;
import org.springframework.context.MessageSource;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class ApplicationConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public FilterRegistrationBean<CustomAuditingFilter> auditingFilter(MessageSource messageSource) {
        FilterRegistrationBean<CustomAuditingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new CustomAuditingFilter(messageSource));
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<JwtFilter> disableJwtFilterAutoRegistration(JwtFilter jwtFilter) {
        FilterRegistrationBean<JwtFilter> registrationBean = new FilterRegistrationBean<>(jwtFilter);
        registrationBean.setEnabled(false);
        return registrationBean;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration("localhost", 6379));
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean(name = "normal-serializer")
    @Primary
    public Gson normalSerializer() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .disableHtmlEscaping()
                .create();
    }

    @Bean(name = "redis-serializer")
    public Gson redisSerializer() {
        return new Gson();
    }
}

package com.workingman.config;

import com.workingman.javaBean.RoleBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.net.UnknownHostException;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, RoleBean> StudentRedisTemplate(RedisConnectionFactory redisConnectionFactory) throws UnknownHostException {
        RedisTemplate<String, RoleBean> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        //序列化器
        Jackson2JsonRedisSerializer<RoleBean> ser= new Jackson2JsonRedisSerializer<RoleBean>(RoleBean.class);
        template.setDefaultSerializer(ser);
        return template;
    }
}

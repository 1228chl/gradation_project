package com.graduationprojectordermanagementsystem.config;



import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;



@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // Key 序列化
        //使用StringRedisSerializer将键序列化为可读字符串，避免默认JDK序列化产生的二进制数据（如\xac\xed\x00前缀）
        //适用于所有键类型（普通键和哈希键），确保通过Redis命令行可直接识别键名
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // 配置泛型
        ObjectMapper objectMapper = new ObjectMapper();
        //LaissezFaireSubTypeValidator 允许反序列化任何类，如果 Redis 被恶意写入特殊 JSON（如包含 java.lang.Runtime），可能导致 RCE（远程代码执行）漏洞。
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL
        );


        // Value 序列化（支持泛型）
        //GenericJackson2JsonRedisSerializer会将对象序列化为JSON，并在JSON中添加@class字段保存类型信息（如com.example.User），支持反序列化时自动还原对象类型
        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        //template.afterPropertiesSet() 是必须调用的生命周期方法，用于验证配置完整性并初始化内部组件。未调用会导致IllegalStateException异常
        template.afterPropertiesSet();
        return template;
    }
}

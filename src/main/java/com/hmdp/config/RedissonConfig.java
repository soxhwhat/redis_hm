package com.hmdp.config;

import io.lettuce.core.ReadFrom;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class  RedissonConfig {

    @Bean
    public RedissonClient redissonClient(){
        // 配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6379");
        // 创建RedissonClient对象
        return Redisson.create(config);
    }

    @Bean
    public LettuceClientConfigurationBuilderCustomizer configurationBuilderCustomizer() {
        //配置主从读写分离模式，采用REPICA_PREFERRED策略:优先从从节点读取数据，如果从节点不可用，从主节点读取数据
        return clientConfigurationBuilder -> clientConfigurationBuilder.readFrom(ReadFrom.REPLICA_PREFERRED);
    }
//
//    @Bean
//    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
//        // 创建 RedisTemplate 对象
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//
//        // 设置开启事务支持
//        template.setEnableTransactionSupport(true);
//
//        // 设置 RedisConnection 工厂。😈 它就是实现多种 Java Redis 客户端接入的秘密工厂。感兴趣的胖友，可以自己去撸下。
//        template.setConnectionFactory(factory);
//
//        // 使用 String 序列化方式，序列化 KEY 。
//        template.setKeySerializer(RedisSerializer.string());
//
//        // 使用 JSON 序列化方式（库是 Jackson ），序列化 VALUE 。
//        template.setValueSerializer(RedisSerializer.json());
//        return template;
//    }

    //        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
//        ObjectMapper objectMapper = new ObjectMapper();// <1>
////        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
////        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//
//        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
//        template.setValueSerializer(jackson2JsonRedisSerializer);

    //    @Bean // PUB/SUB 使用的 Bean ，需要时打开。
//    public RedisMessageListenerContainer listenerContainer(RedisConnectionFactory factory) {
//        // 创建 RedisMessageListenerContainer 对象
//        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//
//        // 设置 RedisConnection 工厂。😈 它就是实现多种 Java Redis 客户端接入的秘密工厂。感兴趣的胖友，可以自己去撸下。
//        container.setConnectionFactory(factory);
//
//        // 添加监听器
//        container.addMessageListener(new TestChannelTopicMessageListener(), new ChannelTopic("TEST"));
////        container.addMessageListener(new TestChannelTopicMessageListener(), new ChannelTopic("AOTEMAN"));
////        container.addMessageListener(new TestPatternTopicMessageListener(), new PatternTopic("TEST"));
//        return container;
//    }
}

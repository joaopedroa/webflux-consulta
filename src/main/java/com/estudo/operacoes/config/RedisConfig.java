package com.estudo.operacoes.config;

import com.estudo.operacoes.core.models.Operacao;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveListOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;


@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, Operacao> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory, ObjectMapper mapper) {
        Jackson2JsonRedisSerializer<Operacao> serializer = new Jackson2JsonRedisSerializer<>(mapper, Operacao.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, Operacao> construtor = RedisSerializationContext.newSerializationContext(new Jackson2JsonRedisSerializer<>(String.class));
        RedisSerializationContext<String, Operacao> contexto = construtor.value(serializer).build();
        return new ReactiveRedisTemplate<>(factory, contexto);
    }




}

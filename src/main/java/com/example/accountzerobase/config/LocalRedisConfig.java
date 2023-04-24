package com.example.accountzerobase.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Configuration
public class LocalRedisConfig {
	@Value("${spring.redis.port}") // 밑에 redisPort 에 값 담아줌. yml에 있는
	private int redisPort;

	private RedisServer redisServer;

	@PostConstruct
	public void startRedis() {
//        redisServer = RedisServer.builder()
//                .port(redisPort)
//                .setting("maxmemory 128M")
//                .build();
		redisServer = new RedisServer(redisPort);
		redisServer.start();
	}

	@PreDestroy
	public void stopRedis() {
		if (redisServer != null) {
			redisServer.stop();
		}
	}

}

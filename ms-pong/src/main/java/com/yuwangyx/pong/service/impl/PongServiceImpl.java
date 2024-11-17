package com.yuwangyx.pong.service.impl;

import com.yuwangyx.pong.service.PongService;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;


@Service
public class PongServiceImpl implements PongService {

    private static int successCount = 0;
    private static int failCount = 0;
    RateLimiterConfig customConfig = RateLimiterConfig.custom()
            .timeoutDuration(Duration.ofMillis(100))
            .limitForPeriod(1)
            .limitRefreshPeriod(Duration.ofSeconds(1))
            .build();
    // 创建限流器注册表
    RateLimiterRegistry registry = RateLimiterRegistry.of(customConfig);

    // 获取限流器实例
    private final RateLimiter rateLimiter = registry.rateLimiter("1RPSRateLimiter");

    public Mono<ServerResponse> handlePong() {
        if (rateLimiter.acquirePermission()){
            System.out.println("allow request, current time : " + Instant.now().getEpochSecond()+ " times: " + ++successCount);
            return ServerResponse.ok().body(Mono.just("World"), String.class);
        }
        System.out.println("429          , current time : " + Instant.now().getEpochSecond() + " times: " + ++failCount);
        return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS).body(Mono.just("Rate Limited"), String.class);
    }
}

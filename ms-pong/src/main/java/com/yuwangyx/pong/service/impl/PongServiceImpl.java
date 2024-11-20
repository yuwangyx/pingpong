
package com.yuwangyx.pong.service.impl;

import com.yuwangyx.pong.service.PongService;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

/**
 * Implementation of the PongService interface to handle Pong requests.
 */
@Service
public class PongServiceImpl implements PongService {

    /**
     * Counter for successfully processed requests.
     */
    private static int successCount = 0;

    /**
     * Counter for rate-limited requests.
     */
    private static int failCount = 0;

    /**
     * Custom rate limiter configuration to allow 1 request per second with a timeout of 100 milliseconds.
     */
    RateLimiterConfig customConfig = RateLimiterConfig.custom()
            .timeoutDuration(Duration.ofMillis(100)) // Set the timeout duration
            .limitForPeriod(1) // Set the number of permits per period
            .limitRefreshPeriod(Duration.ofSeconds(1)) // Set the refresh period
            .build();

    /**
     * Create a rate limiter registry with the custom configuration.
     */
    RateLimiterRegistry registry = RateLimiterRegistry.of(customConfig);

    /**
     * Get an instance of the rate limiter.
     */
    private final RateLimiter rateLimiter = registry.rateLimiter("1RPSRateLimiter");

    /**
     * Handle the Pong request.
     *
     * @param request
     * @return A Mono<ServerResponse> object containing the response status and content.
     */
    public Mono<Boolean> handlePong(String ipAddress, String appName) {
        if (rateLimiter.acquirePermission()) {
            // If permission is acquired, process the request and return a successful response
            System.out.println("allow request, current time : " + Instant.now().getEpochSecond() + " times: " + ++successCount + ", from " + appName + ":" + ipAddress);
            return Mono.just(true);
        }
        // If permission is not acquired, return a 429 Too Many Requests response
        System.out.println("429          , current time : " + Instant.now().getEpochSecond() + " times: " + ++failCount + ", from " + appName + ":" + ipAddress);
        return Mono.just(false);
    }

}
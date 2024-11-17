package com.yuwangyx.ping.service.impl;

import com.alibaba.fastjson.JSON;
import com.yuwangyx.ping.entity.Message;
import com.yuwangyx.ping.service.PingService;
import com.yuwangyx.ping.compoment.RateLimiter;
import com.yuwangyx.ping.util.SnowflakeIdGenerator;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class PingServiceImpl implements PingService {

    private final WebClient webClient;
    private final RocketMQTemplate rocketMQTemplate;
    private final RateLimiter rateLimiter;
    private final String appName;
    private final String ipAddress;
    private final String port;

    public PingServiceImpl(WebClient.Builder webClientBuilder, RocketMQTemplate rocketMQTemplate, RateLimiter rateLimiter, @Value("${spring.application.name}") String appName, @Value("${server.port}") String port) throws UnknownHostException {
        this.webClient = webClientBuilder.baseUrl("http://localhost:18088/pong").build();
        this.rocketMQTemplate = rocketMQTemplate;
        this.rateLimiter = rateLimiter;
        this.appName = appName;
        this.ipAddress = InetAddress.getLocalHost().getHostAddress();
        this.port = port;
    }

    public Mono<String> ping() throws IOException {
        long id = new SnowflakeIdGenerator(1).nextId();
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        AtomicReference<String> content = new AtomicReference<>("");

        String ipPort = ipAddress + ":" + port;

        if (rateLimiter.allowRequest()) {
            return Mono.defer(() ->{
                WebClient modifiedClient = webClient.mutate()
                        .defaultHeader("X-Application-Name", appName)
                        .defaultHeader("X-IP-Address", ipPort)
                        .build();
                return modifiedClient.get()
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnSuccess(content::set)
                    .onErrorResume(WebClientResponseException.class, e -> {
                        if (e.getStatusCode().value() == 429) {
                            // 拒签
                            content.set("sent & Pong throttled");
                        } else {
                            content.set("Request failed: " + e.getMessage());
                        }
                        return Mono.just("Throttled");
                    });}).doFinally(signalType -> {
                System.out.println(content.get());
                Message message = Message.builder().id(id).content(content.get()).dateTime(now).appName(appName).ipAddress(ipPort).build();
                rocketMQTemplate.convertAndSend("pingpong_topic", JSON.toJSONString(message));
            });
        }

        // 本地限流2RPS
        content.set("not sent as being rate limited");
        Message message = Message.builder().id(id).content(content.get()).dateTime(now).appName(appName).ipAddress(ipPort).build();
        System.out.println(content.get());
        rocketMQTemplate.convertAndSend("pingpong_topic", JSON.toJSONString(message));
        return Mono.just("Rate Limited");
    }
}
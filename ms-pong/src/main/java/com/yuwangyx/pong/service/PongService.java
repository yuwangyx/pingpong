package com.yuwangyx.pong.service;


import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface PongService {

    Mono<ServerResponse> handlePong();

}

package com.yuwangyx.pong.service;


import reactor.core.publisher.Mono;

public interface PongService {

    Mono<Boolean> handlePong(String ipAddress, String appName);

}

package com.yuwangyx.ping.service;


import reactor.core.publisher.Mono;

import java.io.IOException;

public interface PingService {
    Mono<String> ping() throws IOException;
}

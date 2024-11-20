package com.yuwangyx.pong.handler;

import com.yuwangyx.pong.service.PongService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class PongHandler {

    PongService pongService;

    public Mono<ServerResponse> handlePong(ServerRequest request) {

        ServerRequest.Headers headers = request.headers();
        String ipAddress = headers.header("X-IP-Address") + "";
        String appName = headers.header("X-Application-Name") + "";

        return pongService.handlePong(ipAddress, appName).flatMap(allow -> {
            if (allow) {
                return ServerResponse.ok().body(Mono.just("World"), String.class);
            }
            return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS).body(Mono.just("Rate Limited"), String.class);
        });
    }
}

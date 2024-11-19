package com.yuwangyx.pong.controller;

import com.yuwangyx.pong.service.PongService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@RestController
@AllArgsConstructor
public class PongController {

    private final PongService pongService;

    @Bean
    public RouterFunction<ServerResponse> route() {
        // todo log the request info
        return RouterFunctions.route(RequestPredicates.GET("/pong"), pongService::handlePong);
    }


}
   
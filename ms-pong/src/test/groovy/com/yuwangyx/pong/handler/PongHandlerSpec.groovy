package com.yuwangyx.pong.handler

import com.yuwangyx.pong.service.PongService
import com.yuwangyx.pong.service.impl.PongServiceImpl
import org.springframework.http.HttpStatus
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import spock.lang.Specification
import spock.lang.Subject

class PongHandlerSpec extends Specification {

    @Subject
    PongHandler pongHandler
    def pongService = Mock(PongServiceImpl)

    def request = Mock(ServerRequest)

    def setup() {
        pongHandler = new PongHandler(pongService)
        def mockRequest = MockServerHttpRequest.get("/api/pong")
                .header("X-IP-Address", "localhost:80")
                .header("X-Application-Name", "test")
                .build()
        def exchange = MockServerWebExchange.from(mockRequest)
        request = ServerRequest.create(exchange, _)
    }


    def "test response success"() {
        given:
        pongService.handlePong(_,_) >>Mono.just(true)

        when:
        def result = pongHandler.handlePong(request).block()

        then:
        result.statusCode() == HttpStatus.OK
    }

    def "test response 429"() {
        given:
        pongService.handlePong(_,_) >>Mono.just(false)

        when:
        pongHandler.handlePong(request).block()
        def result2 = pongHandler.handlePong(request).block()

        then:
        result2.statusCode() == HttpStatus.TOO_MANY_REQUESTS
    }


}

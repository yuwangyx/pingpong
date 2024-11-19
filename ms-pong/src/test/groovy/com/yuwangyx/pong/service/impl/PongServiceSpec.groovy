package com.yuwangyx.pong.service.impl

import org.springframework.http.HttpStatus
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import org.springframework.web.reactive.function.server.ServerRequest
import spock.lang.Specification
import spock.lang.Subject

class PongServiceSpec extends Specification {

    @Subject
    PongServiceImpl pongService

    def request = Mock(ServerRequest)

    def setup() {
        pongService = new PongServiceImpl()
        def mockRequest = MockServerHttpRequest.get("/api/pong")
                .header("X-IP-Address", "localhost:80")
                .header("X-Application-Name", "test")
                .build()
        def exchange = MockServerWebExchange.from(mockRequest)
        request = ServerRequest.create(exchange, _)
    }


    def "test response success"() {

        when:
        def result = pongService.handlePong(request).block()

        then:
        result.statusCode() == HttpStatus.OK
    }

    def "test response 429"() {

        when:
        pongService.handlePong(request).block()
        def result2 = pongService.handlePong(request).block()

        then:
        result2.statusCode() == HttpStatus.TOO_MANY_REQUESTS
    }


}

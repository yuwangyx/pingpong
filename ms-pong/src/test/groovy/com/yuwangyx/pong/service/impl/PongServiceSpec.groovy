package com.yuwangyx.pong.service.impl


import org.springframework.http.HttpStatus
import spock.lang.Specification
import spock.lang.Subject

class PongServiceSpec extends Specification {

    @Subject
    PongServiceImpl pongService

    def setup() {
        pongService = new PongServiceImpl()
    }


    def "test response success"() {

        when:
        def result = pongService.handlePong().block()

        then:
        result.statusCode() == HttpStatus.OK
    }

    def "test response 429"() {

        when:
        pongService.handlePong().block()
        def result2 = pongService.handlePong().block()

        then:
        result2.statusCode() == HttpStatus.TOO_MANY_REQUESTS
    }


}

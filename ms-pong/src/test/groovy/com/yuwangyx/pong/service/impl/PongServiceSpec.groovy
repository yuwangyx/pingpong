package com.yuwangyx.pong.service.impl


import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Subject

@Stepwise
class PongServiceSpec extends Specification {

    @Subject
    PongServiceImpl pongService

    def setup() {
        pongService = new PongServiceImpl()
    }


    def "test response success"() {

        when:
        def result = pongService.handlePong("ip","appName").block()

        then:
        result
    }

    def "test response 429"() {

        when:
        sleep(1000)
        def result = pongService.handlePong("ip","appName").block()
        def result2 = pongService.handlePong("ip","appName").block()

        then:
        result
        !result2
    }


}

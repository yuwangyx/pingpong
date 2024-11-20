package com.yuwangyx.ping.component

import com.yuwangyx.ping.compoment.RateLimiter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification
import spock.lang.Stepwise


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "ping.task.scheduling.enabled=false")
@ActiveProfiles("test")
@Stepwise
class RateLimiterSpec extends Specification {

    @Autowired
    RateLimiter rateLimiter

    def "test allowRequest"() {

        when:
        def result = rateLimiter.allowRequest().block()

        then:
        result
    }

    def "test rejectRequest"() {

        when:
        // Wait for 1 second to avoid being affected by other tests
        Thread.sleep(1000)
        def result1 = rateLimiter.allowRequest().block()
        def result2 = rateLimiter.allowRequest().block()
        def result3 = rateLimiter.allowRequest().block()

        then:
        result1
        result2
        !result3
    }
}

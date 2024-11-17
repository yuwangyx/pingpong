package com.yuwangyx.ping.service.impl

import com.yuwangyx.ping.config.PingScheduledTask
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,properties = "ping.task.scheduling.enabled=true")
@ActiveProfiles("test")
class PingScheduledTaskSpec extends Specification {

    @Autowired
    PingScheduledTask pingScheduledTask

    def "test scheduledPing"() {

        when:
        pingScheduledTask.scheduledPing()

        then:
        noExceptionThrown()
    }
}

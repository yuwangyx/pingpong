package com.yuwangyx.ping.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import java.nio.channels.FileChannel
import java.nio.channels.FileLock
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "ping.task.scheduling.enabled=true")
@ActiveProfiles("test")
class PingScheduledTaskSpec extends Specification {

    @Autowired
    PingScheduledTask pingScheduledTask

    def "test scheduledPing"() {

        when:
        // make OverlappingFileLockException for RateLimiter catch
//        FileChannel channel = FileChannel.open(Paths.get("E:\\workspace\\pingpong\\rate_limit_temp.lock"), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE)
//        FileLock lock = channel.lock()
//        sleep(1000)
//        lock.release()
        pingScheduledTask.scheduledPing()

        then:
        noExceptionThrown()
    }
}

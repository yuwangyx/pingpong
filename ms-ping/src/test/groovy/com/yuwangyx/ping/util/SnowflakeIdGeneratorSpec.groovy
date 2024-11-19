package com.yuwangyx.ping.util

import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Unroll

@Stepwise
class SnowflakeIdGeneratorSpec extends Specification {

    @Unroll
    def "test nextId with machineId #machineId"() {
        given:
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(machineId)

        when:
        long id = generator.nextId()

        then:
        id > 0

        where:
        machineId << [0, 1, 1023]  // 测试不同的机器ID
    }

    def "test invalid machineId"() {
        when:
        new SnowflakeIdGenerator(invalidMachineId)

        then:
        thrown(IllegalArgumentException)

        where:
        invalidMachineId << [-1, 1024]
    }

    def "test clock moved backwards"() {
        given:
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(0)
        // 模拟时间倒退
        generator.setLastTimestamp(System.currentTimeMillis() + 1000)

        when:
        generator.nextId()

        then:
        thrown(RuntimeException)
    }

    def "test sequence wrap around"() {
        given:
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(0)
        generator.sequence = SnowflakeIdGenerator.MAX_SEQUENCE - 1
        generator.lastTimestamp = System.currentTimeMillis()

        when:
        long id1 = generator.nextId()
        long id2 = generator.nextId()

        then:
        id1 != id2
    }


    def "test tilNextMillis is called when timestamp does not change"() {
        given:
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(0)
        long fixedTime = System.currentTimeMillis()
        generator.setLastTimestamp(fixedTime)

        // 模拟时间停滞
        def originalCurrentTimeMillis = System.&currentTimeMillis
        System.metaClass.static.currentTimeMillis = { -> fixedTime }

        when:
        long id1 = generator.nextId()
        long id2 = generator.nextId()

        then:
        id1 != id2

        cleanup:
        System.metaClass.static.currentTimeMillis = originalCurrentTimeMillis
    }
}

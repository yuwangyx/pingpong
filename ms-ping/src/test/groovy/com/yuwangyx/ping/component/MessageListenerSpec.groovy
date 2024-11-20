package com.yuwangyx.ping.component

import com.alibaba.fastjson.JSON
import com.yuwangyx.ping.compoment.MessageListener
import com.yuwangyx.ping.entity.Message
import com.yuwangyx.ping.repository.MessageRepository
import reactor.core.publisher.Mono
import spock.lang.Specification
import spock.lang.Subject

class MessageListenerSpec extends Specification {

    @Subject
    MessageListener messageListener
    def messageRepository = Mock(MessageRepository)

    def setup() {
        messageListener = new MessageListener(messageRepository)
    }

    def "test save success"() {
        given:
        def expectedMessage = "{\n" +
                "        \"id\": 247651493715382272,\n" +
                "        \"content\": \"World\",\n" +
                "        \"dateTime\": \"2024-11-14T09:18:37.244Z\"\n" +
                "    }"
        def parsedMessage = JSON.parseObject(expectedMessage, Message.class)
        def expectedMono = Mono.just(parsedMessage)
        messageRepository.save(_) >> expectedMono

        when:
        messageListener.onMessage(expectedMessage)

        then:
        1 * messageRepository.save(_)
    }

    def "test null exception"() {

        when:
        messageListener.onMessage(null)

        then:
        noExceptionThrown()
    }

    def "test save exception"() {

        when:
        messageListener.onMessage("abc")

        then:
//        thrown(JSONException.class)
        noExceptionThrown()
    }


}

package com.yuwangyx.ping.component

import com.alibaba.fastjson.JSON
import com.yuwangyx.ping.compoment.MessageListener
import com.yuwangyx.ping.entity.Message
import com.yuwangyx.ping.repository.MessageRepository
import spock.lang.Specification
import spock.lang.Subject

class MessageListenerSpec extends Specification {

    @Subject
    MessageListener messageListener

    MessageRepository messageRepository = Mock()

    def setup() {
        messageListener = new MessageListener(messageRepository)
    }

    def "test_onMessage_withValidPayload"() {
        given:
        String validMessageJson = '{"id":1,"content":"Hello World"}'
        Message expectedMessage = JSON.parseObject(validMessageJson, Message.class)

        when:
        messageListener.onMessage(validMessageJson)

        then:
        1 * messageRepository.save(expectedMessage)
    }

    def "test_onMessage_withEmptyPayload"() {
        given:
        String emptyMessage = ""

        when:
        messageListener.onMessage(emptyMessage)

        then:
        0 * messageRepository._
    }
}
package com.yuwangyx.ping.component

import com.yuwangyx.ping.compoment.MessageListener
import org.springframework.data.mongodb.core.MongoTemplate
import spock.lang.Specification
import spock.lang.Subject

class MessageListenerSpec extends Specification {

    @Subject
    MessageListener messageListener
    def mongoTemplate = Mock(MongoTemplate)

    def setup() {
        messageListener = new MessageListener(mongoTemplate)
    }


    def "test save success"() {
        given:
        mongoTemplate.save(_) >> void

        when:
        messageListener.onMessage(
                "{\n" +
                        "        \"id\": 247651493715382272,\n" +
                        "        \"content\": \"World\",\n" +
                        "        \"dateTime\": \"2024-11-14T09:18:37.244Z\"\n" +
                        "    }")

        then:
        1 * mongoTemplate.save(_)
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

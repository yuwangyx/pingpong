package com.yuwangyx.ping.compoment;

import com.alibaba.fastjson.JSON;
import com.yuwangyx.ping.entity.Message;
import com.yuwangyx.ping.repository.MessageRepository;
import io.netty.util.internal.StringUtil;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RocketMQMessageListener(topic = "pingpong_topic", consumerGroup = "testGroup")
public class MessageListener implements RocketMQListener<String> {

    private final MessageRepository messageRepository;

    public MessageListener(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public void onMessage(String message) {
//        System.out.println("Received message: " + message);
        if (!StringUtil.isNullOrEmpty(message)) {
            try {
                Mono<Message> messageMono = Mono.just(JSON.parseObject(message, Message.class));
                messageMono.flatMap(messageRepository::save).subscribe();
            } catch (Exception e) {
                e.printStackTrace();
                // log the error
            }
        }
    }
}
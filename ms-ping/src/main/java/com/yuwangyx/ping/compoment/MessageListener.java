package com.yuwangyx.ping.compoment;

import com.alibaba.fastjson.JSON;
import com.yuwangyx.ping.entity.Message;
import io.netty.util.internal.StringUtil;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RocketMQMessageListener(topic = "pingpong_topic", consumerGroup = "testGroup")
public class MessageListener implements RocketMQListener<String> {

    private final MongoTemplate mongoTemplate;

    public MessageListener(MongoTemplate mongoTemplate){
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Received message: " + message);
        if (!StringUtil.isNullOrEmpty(message)){
            try {
                mongoTemplate.save(JSON.parseObject(message, Message.class));
            }catch (Exception e){
                e.printStackTrace();
                // 重发消息，补日志
            }
        }
    }
}
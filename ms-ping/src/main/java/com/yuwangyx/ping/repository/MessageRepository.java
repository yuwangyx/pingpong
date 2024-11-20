package com.yuwangyx.ping.repository;


import com.yuwangyx.ping.entity.Message;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface MessageRepository extends ReactiveMongoRepository<Message, String> {

}
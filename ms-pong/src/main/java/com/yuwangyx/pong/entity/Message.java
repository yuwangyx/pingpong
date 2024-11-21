package com.yuwangyx.pong.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@Document(collection = "messages")
public class Message {
    @Id
    private Long id;
    private String content;
    @JsonFormat(locale = "zh", timezone = "Asia/Shanghai", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;
    private String appName;
    private String ipAddress;
}
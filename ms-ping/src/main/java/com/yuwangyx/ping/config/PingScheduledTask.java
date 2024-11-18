package com.yuwangyx.ping.config;

import com.yuwangyx.ping.service.PingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(name = "ping.task.scheduling.enabled", havingValue = "true")
public class PingScheduledTask {

    @Autowired
    private PingService pingService;

    private static final Random random = new Random();

    /**
     * Scheduled ping task
     *
     * @throws Exception
     */
    @Scheduled(fixedRate = 100)
    public void scheduledPing() throws Exception {
        // random delay 0 - 50 ms
        int randomDelay = random.nextInt(100);
        TimeUnit.MILLISECONDS.sleep(randomDelay);
        pingService.ping().subscribe();
    }
}
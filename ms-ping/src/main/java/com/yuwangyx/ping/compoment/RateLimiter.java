package com.yuwangyx.ping.compoment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;

@Component
public class RateLimiter {

    @Value("${rateLimiter.lockFilePath}")
    private Path lockFilePath;
    @Value("${rateLimiter.maxRequestsPerSecond}")
    private int maxRequestsPerSecond;

    public boolean allowRequest() throws IOException {
        try (FileChannel channel = FileChannel.open(lockFilePath, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            // 获取独占锁
            try (FileLock lock = channel.lock()) {
                Instant now = Instant.now();
                long currentSecond = now.getEpochSecond();
                int currentCount = readCounter(channel, currentSecond);

                if (currentCount < maxRequestsPerSecond) {
                    writeCounter(channel, currentSecond, currentCount + 1);
                    return true;
                } else {
                    // 超过限制，拒绝请求
                    return false;
                }
            }
        }
    }

    private int readCounter(FileChannel channel, long currentSecond) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(12); // 8 bytes for second, 4 bytes for count
        channel.position(0);
        channel.read(buffer);
        buffer.rewind();

        long storedSecond = buffer.getLong();
        int storedCount = buffer.getInt();

        if (storedSecond != currentSecond) {
            // 如果存储的时间不是当前秒，重置计数器
            buffer.clear();
            buffer.putLong(currentSecond);
            buffer.putInt(0);
            buffer.flip();
            channel.position(0);
            channel.write(buffer);
            return 0;
        }

        return storedCount;
    }

    private void writeCounter(FileChannel channel, long currentSecond, int count) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(12); // 8 bytes for second, 4 bytes for count
        buffer.putLong(currentSecond);
        buffer.putInt(count);
        buffer.flip();
        channel.position(0);
        channel.write(buffer);
    }
}

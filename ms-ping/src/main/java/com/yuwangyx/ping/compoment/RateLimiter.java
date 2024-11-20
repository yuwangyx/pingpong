
package com.yuwangyx.ping.compoment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

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

    /**
     * Determines whether a request should be allowed based on the rate limit.
     *
     * @return true if the request is allowed, false otherwise.
     * @throws IOException if an I/O error occurs.
     */
    public Mono<Boolean> allowRequest() {
        return Mono.create(monoSink -> {
            try (FileChannel channel = FileChannel.open(lockFilePath, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
                // Acquire an exclusive lock on the file channel
                try (FileLock lock = channel.lock()) {
                    Instant now = Instant.now();
                    long currentSecond = now.getEpochSecond();

                    // read and reset count
                    int currentCount = readCounter(channel, currentSecond);

                    if (currentCount < maxRequestsPerSecond) {
                        writeCounter(channel, currentSecond, currentCount + 1);
                        monoSink.success(true);
                    } else {
                        // Exceeds the limit, reject the request
                        monoSink.success(false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Reads the current counter from the file channel.
     *
     * @param channel       the file channel to read from.
     * @param currentSecond the current second in epoch time.
     * @return the current count of requests for the current second.
     * @throws IOException if an I/O error occurs.
     */
    private int readCounter(FileChannel channel, long currentSecond) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(12); // 8 bytes for second, 4 bytes for count
        channel.position(0);
        channel.read(buffer);
        buffer.rewind();

        long storedSecond = buffer.getLong();
        int storedCount = buffer.getInt();

        if (storedSecond != currentSecond) {
            // If the stored time is not the current second, reset the counter
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

    /**
     * Writes the current counter to the file channel.
     *
     * @param channel       the file channel to write to.
     * @param currentSecond the current second in epoch time.
     * @param count         the current count of requests for the current second.
     * @throws IOException if an I/O error occurs.
     */
    private void writeCounter(FileChannel channel, long currentSecond, int count) throws IOException {
        // 12 = 8+4 , 8 bytes for second, 4 bytes for count
        ByteBuffer buffer = ByteBuffer.allocate(12);
        buffer.putLong(currentSecond);
        buffer.putInt(count);
        buffer.flip();
        channel.position(0);
        channel.write(buffer);
    }
}
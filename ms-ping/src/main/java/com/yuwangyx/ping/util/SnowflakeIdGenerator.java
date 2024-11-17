package com.yuwangyx.ping.util;

public class SnowflakeIdGenerator {

    // 起始时间戳（2023-01-01 00:00:00）
    private final static long START_TIMESTAMP = 1672531200000L;

    // 每一部分占用的位数
    private final static long TIMESTAMP_BITS = 41L;
    private final static long MACHINE_ID_BITS = 10L;
    private final static long SEQUENCE_BITS = 12L;

    // 每一部分的最大值
    private final static long MAX_MACHINE_ID = ~(-1L << MACHINE_ID_BITS);
    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    // 每一部分向左的位移
    private final static long MACHINE_ID_SHIFT = SEQUENCE_BITS;
    private final static long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS;

    // 机器ID
    private long machineId;
    // 序列号
    private long sequence = 0L;
    // 上一次时间戳
    private long lastTimestamp = -1L;

    public SnowflakeIdGenerator(long machineId) {
        if (machineId > MAX_MACHINE_ID || machineId < 0) {
            throw new IllegalArgumentException("Machine ID must be between 0 and " + MAX_MACHINE_ID);
        }
        this.machineId = machineId;
    }

    public synchronized long nextId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id for " + (lastTimestamp - timestamp) + " milliseconds");
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - START_TIMESTAMP) << TIMESTAMP_LEFT_SHIFT) |
                (machineId << MACHINE_ID_SHIFT) |
                sequence;
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }

    public void setLastTimestamp(long lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }
}

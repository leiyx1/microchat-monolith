package com.leiyx.microchat.monolith.messaging.util;

import java.util.Optional;
import java.util.Random;

public class RandomWorkerIdSnowflakeIdGenerator implements IdGenerator {

    private final long twepoch;
    private final long workerIdBits = 10L;
    private final long maxWorkerId = ~(-1L << workerIdBits);
    private final long sequenceBits = 12L;
    private final long workerIdShift = sequenceBits;
    private final long timestampLeftShift = sequenceBits + workerIdBits;
    private final long sequenceMask = ~(-1L << sequenceBits);
    private long workerId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;
    private static final Random RANDOM = new Random();

    public RandomWorkerIdSnowflakeIdGenerator() {
        this.twepoch = 1288834974657L;
        workerId = RANDOM.nextLong(maxWorkerId);
    }

    @Override
    public synchronized Optional<Long> get() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;
            if (offset <= 5) {
                try {
                    wait(offset << 1);
                    timestamp = timeGen();
                    if (timestamp < lastTimestamp) {
                        return Optional.empty();
                    }
                } catch (InterruptedException e) {
                    return Optional.empty();
                }
            } else {
                return Optional.empty();
            }
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                sequence = RANDOM.nextInt(100);
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = RANDOM.nextInt(100);
        }
        lastTimestamp = timestamp;
        long id = ((timestamp - twepoch) << timestampLeftShift) | (workerId << workerIdShift) | sequence;
        return Optional.of(id);
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
}
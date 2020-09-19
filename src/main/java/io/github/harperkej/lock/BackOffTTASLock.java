package io.github.harperkej.lock;

import io.github.harperkej.common.Lock;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class BackOffTTASLock implements Lock {

    private AtomicBoolean lock = new AtomicBoolean(false);

    /**
     * The minimum delay for the backoff strategy.
     * One has to find out which is the best minimum delay for
     * backoff by testing the lock with different delay values.
     */
    private final int MIN_DELAY_MILLIS;

    /**
     * The maximum delay for the backoff strategy.
     * One has to find out which is the best maximum delay for
     * backoff by testing the lock with different delay values.
     */
    private final int MAX_DELAY_MILLIS;

    public BackOffTTASLock(int minDelayMillis, int maxDelayMillis) {
        MIN_DELAY_MILLIS = minDelayMillis;
        MAX_DELAY_MILLIS = maxDelayMillis;
    }

    public BackOffTTASLock() {
        MIN_DELAY_MILLIS = 250;
        MAX_DELAY_MILLIS = 10000;
    }

    @Override
    public void lock() {
        int delay = MIN_DELAY_MILLIS;
        Random random = new Random();
        while (true) {
            while (lock.get()) { }
            if (!lock.getAndSet(true)) return;
            try {
                Thread.sleep(random.nextInt(Integer.MAX_VALUE - 1) % delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            delay *= 2;
            if (delay > MAX_DELAY_MILLIS) delay = MAX_DELAY_MILLIS;
        }
    }

    @Override
    public void unlock() {
        lock.set(false);
    }
}

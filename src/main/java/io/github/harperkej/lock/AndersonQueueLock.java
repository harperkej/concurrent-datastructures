package io.github.harperkej.lock;

import io.github.harperkej.common.Lock;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * Queue based lock.
 * Works for any number of up to <<n>> threads.
 * Offers FIFO based fairness.
 * The issue is that you need to maintain a bit for each thread --> memory consumption issue.
 */
public class AndersonQueueLock implements Lock {

    private final int threadCount;
    private final AtomicBoolean[] flags;
    private AtomicInteger nextSlot = new AtomicInteger(0);
    private ThreadLocal<Integer> currentThreadSlot = new ThreadLocal<>();

    public AndersonQueueLock(int threadCount) {
        this.threadCount = threadCount;
        flags = new AtomicBoolean[threadCount];
        flags[0] = new AtomicBoolean(false);
        IntStream.range(1, threadCount).forEach(i -> flags[i] = new AtomicBoolean(true));
    }

    @Override
    public void lock() {
        int currentThreadSlot = nextSlot.getAndIncrement();
        while (flags[currentThreadSlot % threadCount].get()) { }
        this.currentThreadSlot.set(currentThreadSlot);
        flags[currentThreadSlot % threadCount].set(true);
    }

    @Override
    public void unlock() {
        int currentThreadSlot = this.currentThreadSlot.get();
        flags[(currentThreadSlot + 1) % threadCount].set(false); // Release the thread for the next thread in queue.
    }
}

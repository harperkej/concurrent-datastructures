package io.github.harperkej.lock.naive;

import io.github.harperkej.common.Lock;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.stream.IntStream;

/**
 * An improved lock compared to Peterson Lock.
 * Besides no-starvation it provides a decent level of fairness: FIFO fairness.
 */
public class LamportBakeryLock implements Lock {

    private AtomicBoolean flags[];
    private AtomicIntegerArray labels;
    private int numberOfThreads;

    public LamportBakeryLock(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
        flags = new AtomicBoolean[this.numberOfThreads];
        labels = new AtomicIntegerArray(this.numberOfThreads);
        IntStream.range(0, numberOfThreads).forEach(threadId -> {
            flags[threadId] = new AtomicBoolean(false); // Initially no one wants to enter critical section
            labels.set(threadId, 0); // Initially no one has taken the 'ticket' for waiting in line.
        });
    }

    @Override
    public void lock() {
        int currentThreadID = this.threadID();
        flags[currentThreadID].set(true); // Indication that the thread calling the lock method wants to enter critical section.
        labels.set(currentThreadID, maxLabel() + 1); // Pickup the max label of the
        int otherThreadID = 0;
        while (otherThreadID < numberOfThreads) {
            //While there is another thread that wants to enter the critical section
            // and it is lexicographically smaller, then just wait...
            while (otherThreadID != currentThreadID
                    && flags[otherThreadID].get()
                    && isLexicographicallySmaller(otherThreadID)) { }
            otherThreadID += 1;
        }
    }

    /**
     * Checks if the #otherThreadID is lexicographically smaller than the running thread.
     * Thread A with label LA (A,LA) is lexicographically smaller than the thread B with label LB (B,LB) iff
     * LA < LB, or LA = LB and A < B.
     *
     * @param otherThreadID The id of the other thread.
     * @return Whether the thread with ID #otherThreadID is lexicographically smaller than the running thread.
     */
    private boolean isLexicographicallySmaller(int otherThreadID) {
        int currentThreadID = this.threadID();
        int currentThreadLabel = labels.get(currentThreadID);
        int otherThreadLabel = labels.get(otherThreadID);
        return (otherThreadLabel < currentThreadLabel) ||
                (otherThreadLabel == currentThreadLabel && otherThreadID < currentThreadID);
    }

    private int maxLabel() {
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < numberOfThreads; i++) {
            if (labels.get(i) > max) max = labels.get(i);
        }
        return max;
    }

    @Override
    public void unlock() {
        int currentThreadID = this.threadID();
        flags[currentThreadID].set(false);
    }
}

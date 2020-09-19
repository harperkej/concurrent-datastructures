package io.github.harperkej.lock.naive;

import io.github.harperkej.common.Lock;

import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.stream.IntStream;

/**
 * A generalization of the Peterson Lock.
 * Works for any number of n > 2 threads.
 * No fairness at all.
 */
public class FilterLock implements Lock {

    private AtomicIntegerArray levels;
    private AtomicIntegerArray victims;
    private int numberOfThreads;

    public FilterLock(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
        levels = new AtomicIntegerArray(this.numberOfThreads); // As many levels as are there threads.
        victims = new AtomicIntegerArray(this.numberOfThreads);// One 'victim' for each level.
        IntStream.range(0, this.numberOfThreads).forEach(thread -> levels.set(thread, 0)); // Initially, each thread is at level 0.
    }

    public void lock() {
        int currentThread = this.threadID();
        for (int level = 1; level < numberOfThreads; level++) {
            levels.set(currentThread, level); // Level of the thread with ID: #currentThread is #level
            victims.set(level, currentThread);
            int otherThread = 0;
            //while there exists another thread, that is at the current level or higher
            // and I'm still the victim of this level, wait...
            while (otherThread < numberOfThreads) {
                while (otherThread != currentThread
                        && levels.get(otherThread) >= level
                        && victims.get(level) == currentThread) { }
                otherThread += 1;
            }
        }
    }

    public void unlock() {
        int currentThread = this.threadID();
        levels.set(currentThread, 0); //After finishing the n-th level (the critical section), just return the thread to level 0
    }

}

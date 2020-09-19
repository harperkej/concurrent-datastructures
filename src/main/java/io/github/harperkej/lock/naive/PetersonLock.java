package io.github.harperkej.lock.naive;

import io.github.harperkej.common.Lock;

/**
 * A very simple lock that works only for two threads.
 */
public class PetersonLock implements Lock {

    private volatile boolean[] flag = new boolean[2];
    private volatile int victim;

    public void lock() {
        int currentThreadID = this.threadID(); // Should be 1 or 0
        int otherThreadID = 1 - currentThreadID;
        flag[currentThreadID] = true; // Announce to the other thread that you want to enter the critical section.
        victim = currentThreadID; // Give priority to the other thread.
        while (flag[otherThreadID] && victim == currentThreadID) {
            // Wait till the other thread releases the lock, or the other thread wants to enter and gives you priority.
        }
    }

    public void unlock() {
        int currentThreadID = this.threadID();
        flag[currentThreadID] = false; // Announce that you release the lock.
    }

}

package io.github.harperkej.common;

/**
 * Lock interface with two abstract methods #lock() and #unlock
 * and a method to return the ID of the thread calling the method.
 */
public interface Lock {

    void lock();

    void unlock();

    default int threadID() {
        return (int) Thread.currentThread().getId();
    }

}
